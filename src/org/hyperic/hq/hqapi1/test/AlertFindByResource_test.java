package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.AlertApi;

public class AlertFindByResource_test extends AlertTestBase {

    public AlertFindByResource_test(String name) {
        super(name);
    }

    public void testFindValid() throws Exception {
        AlertApi api = getAlertApi();
        Resource r = getLocalPlatformResource(false, false);
        Alert a = generateAlerts(r);

        AlertsResponse response = api.findAlerts(r, 0, System.currentTimeMillis(),
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
        Resource r = getLocalPlatformResource(false, false);
        Alert a = generateAlerts(r);

        // Issue find 1 ms < a.ctime and 1 ms > a.ctime
        long start = a.getCtime() - 1;
        long end = a.getCtime() + 1;

        AlertsResponse response = api.findAlerts(r, start, end,
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

    public void testFindSmallRangeNoAlerts() throws Exception {
        AlertApi api = getAlertApi();
        Resource r = getLocalPlatformResource(false, false);
        Alert a = generateAlerts(r);

        // Issue find 1 ms > a.ctime and 2 ms > a.ctime
        long start = a.getCtime() + 1;
        long end = a.getCtime() + 2;

        AlertsResponse response = api.findAlerts(r, start, end,
                                                 10, 1, false, false);
        hqAssertSuccess(response);
        assertTrue(response.getAlert().size() == 0);

        // Cleanup
        deleteAlertDefinitionByAlert(a);
    }

    public void testFindInvalidSeverity() throws Exception {
        AlertApi api = getAlertApi();
        Resource r = getLocalPlatformResource(false, false);
        AlertsResponse response = api.findAlerts(r, 0, System.currentTimeMillis(),
                                                 10, 4, false, false);
        hqAssertFailureInvalidParameters(response);
    }

    public void testFindInvalidCount() throws Exception {
        AlertApi api = getAlertApi();
        Resource r = getLocalPlatformResource(false, false);
        AlertsResponse response = api.findAlerts(r, 0, System.currentTimeMillis(),
                                                 -5, 2, false, false);
        hqAssertFailureInvalidParameters(response);
    }

    public void testFindInvalidRange() throws Exception {
        AlertApi api = getAlertApi();
        Resource r = getLocalPlatformResource(false, false);

        AlertsResponse response = api.findAlerts(r, System.currentTimeMillis(), 0,
                                                 10, 2, false, false);
        hqAssertFailureInvalidParameters(response);
    }

    public void testFindInvalidResource() throws Exception {
        AlertApi api = getAlertApi();

        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);
        r.setName("Invalid resource");
        AlertsResponse response = api.findAlerts(r, 0, System.currentTimeMillis(),
                                                 10, 1, false, false);
        hqAssertFailureObjectNotFound(response);
    }
}
