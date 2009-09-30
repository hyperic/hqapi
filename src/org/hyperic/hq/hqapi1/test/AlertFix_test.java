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
        Resource platform = getLocalPlatformResource(false, false);
        AlertDefinition d = generateAlerts(platform);
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

    public void testFixPlatformAlertNoPermission() throws Exception {
        Resource platform = getLocalPlatformResource(false, false);
        AlertDefinition d = generateAlerts(platform);
        AlertApi api = getAlertApi();

        AlertsResponse response = api.findAlerts(platform, 0, System.currentTimeMillis(),
                                                 10, 1, false, false);
        hqAssertSuccess(response);
        assertTrue(response.getAlert().size() <= 10);
        assertTrue(response.getAlert().size() > 0);

        for (Alert a : response.getAlert()) {
            validateAlert(a);
        }

        List<User> users = createTestUsers(1);
        User unprivUser = users.get(0);
        AlertApi apiUnpriv = getApi(unprivUser.getName(), TESTUSER_PASSWORD).getAlertApi();

        // Test marking fixed with an unprivlidged user
        Alert a = response.getAlert().get(0);

        StatusResponse fixResponse = apiUnpriv.fixAlert(a.getId());
        hqAssertFailurePermissionDenied(fixResponse);

        // TODO: Valididate fix flag was set? Will require a getById API.

        // Cleanup
        StatusResponse deleteResponse = getApi().
                getAlertDefinitionApi().deleteAlertDefinition(d.getId());
        hqAssertSuccess(deleteResponse);
        deleteTestUsers(users);
    }

    public void testFixServerAlertNoPermission() throws Exception {
        ResourceApi rApi = getApi().getResourceApi();
        AlertApi api = getAlertApi();

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

        AlertDefinition d = generateAlerts(server);

        AlertsResponse response = api.findAlerts(server, 0, System.currentTimeMillis(),
                                                 10, 1, false, false);
        hqAssertSuccess(response);
        assertTrue(response.getAlert().size() <= 10);
        assertTrue(response.getAlert().size() > 0);

        for (Alert a : response.getAlert()) {
            validateAlert(a);
        }

        List<User> users = createTestUsers(1);
        User unprivUser = users.get(0);
        AlertApi apiUnpriv = getApi(unprivUser.getName(), TESTUSER_PASSWORD).getAlertApi();

        // Test marking fixed with an unprivlidged user
        Alert a = response.getAlert().get(0);

        StatusResponse fixResponse = apiUnpriv.fixAlert(a.getId());
        hqAssertFailurePermissionDenied(fixResponse);

        // TODO: Valididate fix flag was set? Will require a getById API.

        // Cleanup
        StatusResponse deleteResponse = getApi().
                getAlertDefinitionApi().deleteAlertDefinition(d.getId());
        hqAssertSuccess(deleteResponse);
        deleteTestUsers(users);
    }

    public void testFixServiceAlertNoPermission() throws Exception {
        ResourceApi rApi = getApi().getResourceApi();
        AlertApi api = getAlertApi();

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

        AlertDefinition d = generateAlerts(service);

        AlertsResponse response = api.findAlerts(service, 0, System.currentTimeMillis(),
                                                 10, 1, false, false);
        hqAssertSuccess(response);
        assertTrue(response.getAlert().size() <= 10);
        assertTrue(response.getAlert().size() > 0);

        for (Alert a : response.getAlert()) {
            validateAlert(a);
        }

        List<User> users = createTestUsers(1);
        User unprivUser = users.get(0);
        AlertApi apiUnpriv = getApi(unprivUser.getName(), TESTUSER_PASSWORD).getAlertApi();

        // Test marking fixed with an unprivlidged user
        Alert a = response.getAlert().get(0);

        StatusResponse fixResponse = apiUnpriv.fixAlert(a.getId());
        hqAssertFailurePermissionDenied(fixResponse);

        // TODO: Valididate fix flag was set? Will require a getById API.

        // Cleanup
        StatusResponse deleteResponse = getApi().
                getAlertDefinitionApi().deleteAlertDefinition(d.getId());
        hqAssertSuccess(deleteResponse);
        deleteTestUsers(users);
    }

    public void testFixInvalidAlert() throws Exception {

        AlertApi api = getAlertApi();

        StatusResponse response = api.fixAlert(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
}
