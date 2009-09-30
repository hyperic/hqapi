package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.DataPoint;

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

    /**
     * Setup an AlertDefinition that will fire Alert instances waiting for
     * at least 1 alert to be generated.
     *
     * @return The AlertDefinition that was created to generate the alerts.
     */
    protected AlertDefinition generateAlerts(Resource resource) throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();
        MetricDataApi dataApi = api.getMetricDataApi();

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
        d.getAlertCondition().add(AlertDefinitionBuilder.
                createThresholdCondition(true, availMetric.getName(),
                                         AlertDefinitionBuilder.AlertComparator.GREATER_THAN, -1));

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        // Insert a fake 'up' measurement
        List<DataPoint> dataPoints = new ArrayList<DataPoint>();
        DataPoint dp = new DataPoint();
        dp.setTimestamp(System.currentTimeMillis());
        dp.setValue(1.0);
        dataPoints.add(dp);
        StatusResponse dataResponse = dataApi.addData(availMetric, dataPoints);
        hqAssertSuccess(dataResponse);

        // Now we wait..
        System.out.println("Waiting for alerts on " + resource.getName() + "...");
        for (int i = 0; i < 120; i++) {
            // Wait for alerts
            AlertsResponse alerts = getAlertApi().findAlerts(resource, 0,
                                                             System.currentTimeMillis(),
                                                             10, 1, false, false);
            hqAssertSuccess(alerts);
            if (alerts.getAlert().size() > 0) {
                System.out.println("Found " + alerts.getAlert().size() + " alerts!");
                return response.getAlertDefinition().get(0);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Ignore
            }
        }

        throw new Exception("Unable to find generated alerts!");
    }
}
