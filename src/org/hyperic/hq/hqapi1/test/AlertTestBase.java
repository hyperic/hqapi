package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.EscalationActionBuilder;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.DataPoint;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.EscalationResponse;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public abstract class AlertTestBase extends HQApiTestBase {

    public AlertTestBase(String name) {
        super(name);
    }

    protected AlertApi getAlertApi() {
        return getApi().getAlertApi();
    }

    protected void validateAlert(Alert a) throws Exception {
        assertNotNull("fixed was NULL", a.isFixed());
        assertTrue("ctime is incorrect", a.getCtime() > 0);
        assertTrue("resourceId is invalid", a.getResourceId() > 0);
        assertTrue("alertDefinitionId is invalid", a.getAlertDefinitionId() > 0);
        assertNotNull("Alert name was null", a.getName());
        assertTrue("Alert id is incorrect", a.getId() > 0);
        assertTrue("Empty long reason", a.getReason().length() > 0);
    }

    protected Escalation createEscalation() throws Exception {
        EscalationApi escalationApi = getApi().getEscalationApi();

        Escalation e = new Escalation();
        Random r = new Random();
        e.setName("Test Escalation" + r.nextInt());
        e.setMaxPauseTime(24 * 60 * 60 * 1000);
        e.setRepeat(true);
        e.setDescription("Test escalation for Alert tests");
        e.setPauseAllowed(true);
        e.getAction().add(EscalationActionBuilder.createNoOpAction(60000));

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
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();
        MetricDataApi dataApi = api.getMetricDataApi();

        long start = System.currentTimeMillis();

        // Find availability metric for the passed in resource
        MetricsResponse metricsResponse = metricApi.getMetrics(resource, true);
        hqAssertSuccess(metricsResponse);
        Metric availMetric = null;
        for (Metric m : metricsResponse.getMetric()) {
            if (m.getName().equals("Availability")) {
                availMetric = m;
                break;
            }
        }

        assertNotNull("Unable to find Availability metric for " + resource.getName(),
                      availMetric);

        // Create alert definition
        AlertDefinition d = new AlertDefinition();
        Random r = new Random();
        d.setName("Test Alert Definition" + r.nextInt());
        d.setDescription("Definition that will always fire, allowing for testing of Alerts");
        d.setPriority(AlertDefinitionBuilder.AlertPriority.MEDIUM.getPriority());
        d.setActive(true);
        d.setResource(resource);
        if (e != null) {
            d.setEscalation(e);
        }
        d.getAlertCondition().add(AlertDefinitionBuilder.
                createThresholdCondition(true, availMetric.getName(),
                                         AlertDefinitionBuilder.AlertComparator.GREATER_THAN, -1));

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals("Should have found only one Definition from sync",
                     1, response.getAlertDefinition().size());
        AlertDefinition def = response.getAlertDefinition().get(0);

        // Insert a fake 'up' measurement
        List<DataPoint> dataPoints = new ArrayList<DataPoint>();
        DataPoint dp = new DataPoint();
        dp.setTimestamp(System.currentTimeMillis());
        dp.setValue(1.0);
        dataPoints.add(dp);
        StatusResponse dataResponse = dataApi.addData(availMetric, dataPoints);
        hqAssertSuccess(dataResponse);

        final int TIMEOUT = 120;
        for (int i = 0; i < TIMEOUT; i++) {
            // Wait for alerts
            AlertsResponse alerts = getAlertApi().findAlerts(resource, start,
                                                             System.currentTimeMillis(),
                                                             10, 1, (e != null), false);
            hqAssertSuccess(alerts);

            for (Alert a : alerts.getAlert()) {
                // Verify this alert comes from the definition we just created
                if (a.getAlertDefinitionId() == def.getId()) {
                    return a;
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                // Ignore
            }
        }

        throw new Exception("Unable to find generated alerts for " +
                            resource.getName() + " under alert definition " +
                            def.getName());
    }

    protected void deleteAlertDefinitionByAlert(Alert a) throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        StatusResponse response = api.deleteAlertDefinition(a.getAlertDefinitionId());
        hqAssertSuccess(response);
    }
}
