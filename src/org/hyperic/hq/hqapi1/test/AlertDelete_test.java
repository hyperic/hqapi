package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.AlertApi;

public class AlertDelete_test extends AlertTestBase {

    public AlertDelete_test(String name) {
        super(name);
    }

    public void testDeleteAlert() throws Exception {
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
        
        // Test delete
        Alert a = response.getAlert().get(0);

        StatusResponse deleteResponse = api.delete(a.getId());
        hqAssertSuccess(deleteResponse);

        // TODO: Valididate alert was deleted? Will require a getById API.

        // Cleanup
        StatusResponse deleteDefResponse = getApi().
                getAlertDefinitionApi().deleteAlertDefinition(d.getId());
        hqAssertSuccess(deleteDefResponse);
    }

    public void testDeleteInvalidAlert() throws Exception {

        AlertApi api = getAlertApi();

        StatusResponse response = api.delete(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
}
