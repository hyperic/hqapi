package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.AlertApi;

import java.util.List;

public class AlertDelete_test extends AlertTestBase {

    public AlertDelete_test(String name) {
        super(name);
    }

    public void testDeleteAlert() throws Exception {
        AlertApi api = getAlertApi();
        Resource platform = getLocalPlatformResource(false, false);
        Alert a = generateAlerts(platform);

        validateAlert(a);

        // Test delete
        StatusResponse deleteResponse = api.delete(a.getId());
        hqAssertSuccess(deleteResponse);

        // TODO: Valididate alert was deleted? Will require a getById API.

        // Cleanup
        deleteAlertDefinitionByAlert(a);
    }

    public void testDeleteAlertNoPermission() throws Exception {
        AlertApi api = getAlertApi();
        Resource platform = getLocalPlatformResource(false, false);
        Alert a = generateAlerts(platform);

        validateAlert(a);

        // Test delete with an unprivledged user

        List<User> users = createTestUsers(1);
        User unprivUser = users.get(0);
        AlertApi apiUnpriv = getApi(unprivUser.getName(), TESTUSER_PASSWORD).getAlertApi();

        StatusResponse deleteResponse = apiUnpriv.delete(a.getId());
        hqAssertFailurePermissionDenied(deleteResponse);

        // TODO: Valididate alert was deleted? Will require a getById API.

        // Cleanup
        deleteAlertDefinitionByAlert(a);
        deleteTestUsers(users);
    }

    public void testDeleteInvalidAlert() throws Exception {

        AlertApi api = getAlertApi();

        StatusResponse response = api.delete(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
}
