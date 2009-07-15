package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.Alert;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class AlertTestBase extends HQApiTestBase {

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
    protected AlertDefinition generateAlerts() throws Exception {
        Resource platform = getLocalPlatformResource(false, false);
        AlertDefinition d = new AlertDefinition();

        Random r = new Random();
        d.setName("Test Alert Definition" + r.nextInt());
        d.setDescription("Definition that will always fire, allowing for testing of Alerts");
        d.setPriority(AlertDefinitionBuilder.AlertPriority.MEDIUM.getPriority());
        d.setActive(true);
        d.setResource(platform);
        d.getAlertCondition().add(AlertDefinitionBuilder.
                createThresholdCondition(true, "Availability",
                                         AlertDefinitionBuilder.AlertComparator.GREATER_THAN, -1));

        AlertDefinitionApi defApi = getApi().getAlertDefinitionApi();

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        // Now we wait..
        for (int i = 0; i < 120; i++) {
            // Wait for alerts
            System.out.println("Waiting for alerts...");
            AlertsResponse alerts = getAlertApi().findAlerts(platform, 0,
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
