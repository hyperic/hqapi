package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.AlertApi;

public class AlertAck_test extends AlertTestBase {

    public AlertAck_test(String name) {
        super(name);
    }

    public void testAckAlert() throws Exception {
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

        // Test ack
        Alert a = response.getAlert().get(0);

        StatusResponse ackResponse = api.ackAlert(a.getId(), "Test ACK", 60000l);
        hqAssertSuccess(ackResponse);

        // TODO: Valididate ack? Will require a getById API.

        // Cleanup
        StatusResponse deleteResponse = getApi().
                getAlertDefinitionApi().deleteAlertDefinition(d.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testAckInvalidAlert() throws Exception {

        AlertApi api = getAlertApi();

        StatusResponse response = api.ackAlert(Integer.MAX_VALUE, "Test ACK", 60000l);
        hqAssertFailureObjectNotFound(response);
    }
}
