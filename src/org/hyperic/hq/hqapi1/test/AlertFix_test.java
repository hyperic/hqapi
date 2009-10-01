package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.AlertApi;
import org.hyperic.hq.hqapi1.ResourceApi;

import java.util.List;

public class AlertFix_test extends AlertTestBase {

    public AlertFix_test(String name) {
        super(name);
    }

    public void testFixAlert() throws Exception {
        AlertApi api = getAlertApi();
        Resource platform = getLocalPlatformResource(false, false);
        Alert a = generateAlerts(platform);

        validateAlert(a);

        // Test marking fixed
        StatusResponse fixResponse = api.fixAlert(a.getId());
        hqAssertSuccess(fixResponse);

        // TODO: Valididate fix flag was set? Will require a getById API.

        // Cleanup
        deleteAlertDefinitionByAlert(a);
    }

    public void testFixPlatformAlertNoPermission() throws Exception {
        Resource platform = getLocalPlatformResource(false, false);
        Alert a = generateAlerts(platform);

        validateAlert(a);

        List<User> users = createTestUsers(1);
        User unprivUser = users.get(0);
        AlertApi apiUnpriv = getApi(unprivUser.getName(), TESTUSER_PASSWORD).getAlertApi();

        // Test marking fixed with an unprivlidged user
        StatusResponse fixResponse = apiUnpriv.fixAlert(a.getId());
        hqAssertFailurePermissionDenied(fixResponse);

        // TODO: Valididate fix flag was set? Will require a getById API.

        // Cleanup
        deleteAlertDefinitionByAlert(a);
        deleteTestUsers(users);
    }

    public void testFixServerAlertNoPermission() throws Exception {
        AlertApi api = getAlertApi();
        ResourceApi rApi = getApi().getResourceApi();

        ResourcePrototypeResponse protoResponse =
                rApi.getResourcePrototype("HQ Agent");
        hqAssertSuccess(protoResponse);

        ResourcesResponse resourcesResponse =
                rApi.getResources(protoResponse.getResourcePrototype(),
                                  false, false);
        hqAssertSuccess(resourcesResponse);

        assertTrue("No resources of type " +
                   protoResponse.getResourcePrototype().getName() +
                   " could be found!", resourcesResponse.getResource().size() > 0);

        Resource server = resourcesResponse.getResource().get(0);

        Alert a = generateAlerts(server);

        validateAlert(a);

        List<User> users = createTestUsers(1);
        User unprivUser = users.get(0);
        AlertApi apiUnpriv = getApi(unprivUser.getName(), TESTUSER_PASSWORD).getAlertApi();

        // Test marking fixed with an unprivlidged user
        StatusResponse fixResponse = apiUnpriv.fixAlert(a.getId());
        hqAssertFailurePermissionDenied(fixResponse);

        // TODO: Valididate fix flag was set? Will require a getById API.

        // Cleanup
        deleteAlertDefinitionByAlert(a);
        deleteTestUsers(users);
    }

    public void testFixServiceAlertNoPermission() throws Exception {
        AlertApi api = getAlertApi();
        ResourceApi rApi = getApi().getResourceApi();

        ResourcePrototypeResponse protoResponse =
                rApi.getResourcePrototype("FileServer Mount");
        hqAssertSuccess(protoResponse);

        ResourcesResponse resourcesResponse =
                rApi.getResources(protoResponse.getResourcePrototype(),
                                  false, false);
        hqAssertSuccess(resourcesResponse);

        assertTrue("No resources of type " +
                   protoResponse.getResourcePrototype().getName() +
                   " could be found!", resourcesResponse.getResource().size() > 0);

        Resource service = resourcesResponse.getResource().get(0);

        Alert a = generateAlerts(service);
        validateAlert(a);

        List<User> users = createTestUsers(1);
        User unprivUser = users.get(0);
        AlertApi apiUnpriv = getApi(unprivUser.getName(), TESTUSER_PASSWORD).getAlertApi();

        // Test marking fixed with an unprivlidged user
        StatusResponse fixResponse = apiUnpriv.fixAlert(a.getId());
        hqAssertFailurePermissionDenied(fixResponse);

        // TODO: Valididate fix flag was set? Will require a getById API.

        // Cleanup
        deleteAlertDefinitionByAlert(a);
        deleteTestUsers(users);
    }

    public void testFixInvalidAlert() throws Exception {

        AlertApi api = getAlertApi();

        StatusResponse response = api.fixAlert(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
}
