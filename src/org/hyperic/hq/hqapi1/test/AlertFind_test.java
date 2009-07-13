package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.AlertApi;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class AlertFind_test extends AlertTestBase {

    public AlertFind_test(String name) {
        super(name);
    }

    public void testFindValid() throws Exception {
        AlertDefinition d = generateAlerts();
        AlertApi api = getAlertApi();

        AlertsResponse response = api.findAlerts(0, System.currentTimeMillis(),
                                                 10, 1, false, false);
        hqAssertSuccess(response);
        assertTrue(response.getAlert().size() <= 10);

        // Cleanup
        StatusResponse deleteResponse = getApi().
                getAlertDefinitionApi().deleteAlertDefinition(d.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testFindInvalidSeverity() throws Exception {
        AlertApi api = getAlertApi();
        AlertsResponse response = api.findAlerts(0, System.currentTimeMillis(),
                                                 10, 4, false, false);
        hqAssertFailureInvalidParameters(response);
    }

    public void testFindInvalidCount() throws Exception {
        AlertApi api = getAlertApi();
        AlertsResponse response = api.findAlerts(0, System.currentTimeMillis(),
                                                 -5, 2, false, false);
        hqAssertFailureInvalidParameters(response);
    }

    public void testFindInvalidRange() throws Exception {
        AlertApi api = getAlertApi();
        AlertsResponse response = api.findAlerts(System.currentTimeMillis(), 0,
                                                 10, 2, false, false);
        hqAssertFailureInvalidParameters(response);
    }
}
