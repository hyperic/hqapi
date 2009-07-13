package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.AlertApi;

public class AlertFindByResource_test extends AlertTestBase {

    public AlertFindByResource_test(String name) {
        super(name);
    }

    public void testFindValid() throws Exception {
        Resource r = getLocalPlatformResource(false, false);
        AlertDefinition d = generateAlerts();

        AlertApi api = getAlertApi();

        AlertsResponse response = api.findAlerts(r, 0, System.currentTimeMillis(),
                                                 10, 1, false, false);
        hqAssertSuccess(response);
        assertTrue(response.getAlert().size() <= 10);

        // Cleanup
        StatusResponse deleteResponse = getApi().getAlertDefinitionApi().
                deleteAlertDefinition(d.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testFindInvalidSeverity() throws Exception {
        Resource r = getLocalPlatformResource(false, false);
        AlertApi api = getAlertApi();
        AlertsResponse response = api.findAlerts(r, 0, System.currentTimeMillis(),
                                                 10, 4, false, false);
        hqAssertFailureInvalidParameters(response);
    }

    public void testFindInvalidCount() throws Exception {
        Resource r = getLocalPlatformResource(false, false);
        AlertApi api = getAlertApi();
        AlertsResponse response = api.findAlerts(r, 0, System.currentTimeMillis(),
                                                 -5, 2, false, false);
        hqAssertFailureInvalidParameters(response);
    }

    public void testFindInvalidRange() throws Exception {
        Resource r = getLocalPlatformResource(false, false);
        AlertApi api = getAlertApi();

        AlertsResponse response = api.findAlerts(r, System.currentTimeMillis(), 0,
                                                 10, 2, false, false);
        hqAssertFailureInvalidParameters(response);
    }

    public void testFindInvalidResource() throws Exception {
        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);
        r.setName("Invalid resource");
        AlertApi api = getAlertApi();
        AlertsResponse response = api.findAlerts(r, 0, System.currentTimeMillis(),
                                                 10, 1, false, false);
        hqAssertFailureObjectNotFound(response);
    }
}
