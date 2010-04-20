package org.hyperic.hq.hqapi1.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hyperic.hq.hqapi1.AlertApi;
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.EscalationActionBuilder;
import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertResponse;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.DataPoint;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.EscalationAction;
import org.hyperic.hq.hqapi1.types.EscalationResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public abstract class AlertTestBase extends AlertDefinitionTestBase {

    public AlertTestBase(String name) {
        super(name);
    }

    protected AlertApi getAlertApi() {
        return getApi().getAlertApi();
    }

    protected Alert getAlert(Integer alertId) throws IOException {
        
        AlertResponse response = getAlertApi().getAlert(alertId);
        hqAssertSuccess(response);
        Alert a = response.getAlert();
        validateAlert(a);
        
        return a;
    }
    
    protected void validateAlert(Alert a) {
        assertNotNull("fixed was NULL", a.isFixed());
        assertTrue("ctime is incorrect", a.getCtime() > 0);
        assertTrue("resourceId is invalid", a.getResourceId() > 0);
        assertTrue("alertDefinitionId is invalid", a.getAlertDefinitionId() > 0);
        assertNotNull("Alert name was null", a.getName());
        assertTrue("Alert id is incorrect", a.getId() > 0);
        assertTrue("Empty long reason", a.getReason().length() > 0);
    }
    
    protected boolean validateAlertAttributes(Alert a) throws Exception {
        return a.getCtime() > 0 && a.getResourceId() > 0 && 
            a.getAlertDefinitionId() > 0 && a.getName() != null && a.getId() >0 && 
            a.getReason().length() > 0;
    }

    protected Escalation createEscalation() throws Exception {
        EscalationAction action = EscalationActionBuilder.createNoOpAction(60000);
        return createEscalation(action);
    }

    protected Escalation createEscalation(EscalationAction action) throws Exception {
        EscalationApi escalationApi = getApi().getEscalationApi();

        Escalation e = new Escalation();
        Random r = new Random();
        e.setName("Test Escalation" + r.nextInt());
        e.setMaxPauseTime(24 * 60 * 60 * 1000);
        e.setRepeat(true);
        e.setDescription("Test escalation for Alert tests");
        e.setPauseAllowed(true);
        e.getAction().add(action);

        EscalationResponse response = escalationApi.createEscalation(e);
        hqAssertSuccess(response);
        return response.getEscalation();
    }

    protected void deleteEscalation(Escalation e) throws Exception {
        EscalationApi api = getApi().getEscalationApi();
        StatusResponse response = api.deleteEscalation(e.getId());
        hqAssertSuccess(response);
    }

    protected Alert generateAlerts(Resource resource) throws Exception {
        return generateAlerts(resource, null);
    }

    /**
     * Setup an AlertDefinition that will fire Alert instances waiting for
     * at least 1 alert to be generated.
     *
     * @param resource The resource to generate Alerts on
     * @param e Optional escalation to assign to the AlertDefiniton that is created
     *
     * @return The Alert that was created.
     * @throws Exception If an error occurs generating the alerts
     */
    protected Alert generateAlerts(Resource resource,
                                   Escalation e) throws Exception {

        final long start = System.currentTimeMillis();

        // Find availability metric for the passed in resource
        Metric availMetric = findAvailabilityMetric(resource);

        // Create alert definition
        AlertDefinition d = generateTestDefinition();
        d.setDescription("Definition that will always fire, allowing for testing of Alerts");
        d.setResource(resource);
        if (e != null) {
            d.setEscalation(e);
        }
        d.getAlertCondition().add(AlertDefinitionBuilder.
                createThresholdCondition(true, availMetric.getName(),
                                         AlertDefinitionBuilder.AlertComparator.GREATER_THAN, -1));

        AlertDefinition def = syncAlertDefinition(d);


        // Insert a fake 'up' measurement
        sendAvailabilityDataPoint(resource, 1.0);

        return findAlert(def, start);
    }

    protected Alert fireAvailabilityAlert(AlertDefinition def,
                                          boolean willRecover,
                                          double availability)
        throws Exception {
        
        return fireAvailabilityAlert(def, false, willRecover, availability);
    }

    protected Alert fireAvailabilityAlert(AlertDefinition def,
                                          boolean withActionLog,
                                          boolean willRecover,
                                          double availability)
        throws Exception {
        
        long start = System.currentTimeMillis();
        
        // Insert a fake availability data point so that
        // the alert definition will fire.
        sendAvailabilityDataPoint(def.getResource(), availability);

        Alert alert = findAlert(def, withActionLog, start);
        assertFalse("The alert should not be fixed",
                     alert.isFixed());

        // Get the updated alert definition
        AlertDefinition updatedDef = getAlertDefinition(alert.getAlertDefinitionId());
        validateAvailabilityAlertDefinition(updatedDef, 
                                            willRecover, 
                                            willRecover ? false : true);
        
        return alert;
    }
    
    protected void sendAvailabilityDataPoint(Resource resource, double availability) 
        throws IOException {
    
        MetricDataApi dataApi = getApi().getMetricDataApi();

        // Find availability metric for the resource
        Metric availMetric = findAvailabilityMetric(resource);

        List<DataPoint> dataPoints = new ArrayList<DataPoint>();
        DataPoint dp = new DataPoint();
        dp.setTimestamp(System.currentTimeMillis());
        dp.setValue(availability);
        dataPoints.add(dp);
        StatusResponse dataResponse = dataApi.addData(availMetric, dataPoints);
        hqAssertSuccess(dataResponse);
    }
    
    protected Alert findAlert(AlertDefinition def, long start) 
        throws Exception {
        
        return findAlert(def, false, start);
    }
    
    protected Alert findAlert(AlertDefinition def,
                              boolean withActionLog,
                              long start) 
        throws Exception {

        final int TIMEOUT = 90;
        for (int i = 0; i < TIMEOUT; i++) {
            // Wait for alerts
            AlertsResponse alerts = getAlertApi().findAlerts(def.getResource(), start,
                                                             System.currentTimeMillis(),
                                                             10, 1, 
                                                             (def.getEscalation() != null), 
                                                             false);
            hqAssertSuccess(alerts);

            for (Alert a : alerts.getAlert()) {
                // Verify this alert comes from the definition we just created
                if (a.getAlertDefinitionId() == def.getId()) {
                    if (withActionLog
                            && a.getAlertActionLog().isEmpty()) {
                        // wait for the next loop iteration so that the 
                        // escalation action has time to be executed 
                        continue;
                    }
                    validateAlert(a);
                    return a;
                }
            }

            pauseTest(1000);
        }

        throw new Exception("Unable to find generated alerts for " +
                            def.getResource().getName() + " under alert definition " +
                            def.getName());
    }

    protected void deleteAlertDefinitionByAlert(Alert a) throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        StatusResponse response = api.deleteAlertDefinition(a.getAlertDefinitionId());
        hqAssertSuccess(response);
    }
}
