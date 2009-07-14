package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.AlertApi;

public class AlertFix_test extends AlertTestBase {

    public AlertFix_test(String name) {
        super(name);
    }

    public void testFixAlert() throws Exception {
        AlertDefinition d = generateAlerts();
        AlertApi api = getAlertApi();

        AlertsResponse response = api.findAlerts(0, System.currentTimeMillis(),
                                                 10, 1, false, false);
        hqAssertSuccess(response);
        assertTrue(response.getAlert().size() <= 10);
        assertTrue(response.getAlert().size() > 0);

        for (Alert a : response.getAlert()) {
            validateAlert(a);
        }
        
        // Test marking fixed
        Alert a = response.getAlert().get(0);

        StatusResponse fixResponse = api.fixAlert(a.getId());
        hqAssertSuccess(fixResponse);

        // TODO: Valididate fix flag was set? Will require a getById API.

        // Cleanup
        StatusResponse deleteResponse = getApi().
                getAlertDefinitionApi().deleteAlertDefinition(d.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testFixInvalidAlert() throws Exception {

        AlertApi api = getAlertApi();

        StatusResponse response = api.fixAlert(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
}
