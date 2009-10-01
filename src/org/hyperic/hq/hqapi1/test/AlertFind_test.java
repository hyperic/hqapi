package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertApi;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.Resource;

public class AlertFind_test extends AlertTestBase {

    public AlertFind_test(String name) {
        super(name);
    }

    public void testFindValid() throws Exception {
        AlertApi api = getAlertApi();
        Resource platform = getLocalPlatformResource(false, false);
        Alert a = generateAlerts(platform);

        AlertsResponse response = api.findAlerts(0, System.currentTimeMillis(),
                                                 10, 1, false, false);
        hqAssertSuccess(response);
        assertTrue(response.getAlert().size() <= 10);
        assertTrue(response.getAlert().size() > 0);

        for (Alert alerts : response.getAlert()) {
            validateAlert(alerts);
        }

        // Cleanup
        deleteAlertDefinitionByAlert(a);
    }

    public void testFindValidSmallRange() throws Exception {
        AlertApi api = getAlertApi();
        Resource platform = getLocalPlatformResource(false, false);
        Alert a = generateAlerts(platform);

        long start = a.getCtime() - 1;
        long end = a.getCtime() + 1;

        AlertsResponse response = api.findAlerts(start, end,
                                                 10, 1, false, false);
        hqAssertSuccess(response);
        assertTrue(response.getAlert().size() <= 10);
        assertTrue(response.getAlert().size() > 0);

        for (Alert alerts : response.getAlert()) {
            validateAlert(alerts);
        }

        // Cleanup
        deleteAlertDefinitionByAlert(a);
    }

    public void testFindValidSmallRangeNoAlerts() throws Exception {
        AlertApi api = getAlertApi();

        // Issue a find slightly into the future, should return 0 alerts
        long start = System.currentTimeMillis() + 100;
        long end = start + 1;

        AlertsResponse response = api.findAlerts(start, end,
                                                 10, 1, false, false);
        hqAssertSuccess(response);
        assertTrue(response.getAlert().size() == 0);
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
