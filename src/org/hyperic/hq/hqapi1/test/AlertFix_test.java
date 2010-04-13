package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.AlertActionLog;
import org.hyperic.hq.hqapi1.types.AlertResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.Operation;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.AlertApi;
import org.hyperic.hq.hqapi1.ResourceApi;

import java.util.Collections;
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
        AlertResponse fixResponse = api.fixAlert(a.getId());
        hqAssertSuccess(fixResponse);
        a = fixResponse.getAlert();
        List<AlertActionLog> logs = a.getAlertActionLog();
                
        assertTrue("Alert was not fixed!", a.isFixed());
        assertTrue("Alert action log for the fix is missing",
                    logs.size() > 0);
        assertTrue("Expecting an alert action log containing 'Fixed by'",
                    logs.get(0).getDetail().indexOf("Fixed by") > -1 );
        
        // Cleanup
        deleteAlertDefinitionByAlert(a);
    }

    public void testFixAlertWithReason() throws Exception {
        AlertApi api = getAlertApi();
        Resource platform = getLocalPlatformResource(false, false);
        Alert a = generateAlerts(platform);

        validateAlert(a);

        // Test marking fixed
        String REASON = "HQApi Alert Fix Test";
        AlertResponse fixResponse = api.fixAlert(a.getId(), REASON);
        hqAssertSuccess(fixResponse);
        a = fixResponse.getAlert();
        List<AlertActionLog> logs = a.getAlertActionLog();
        
        assertTrue("Alert was not fixed!", a.isFixed());
        assertTrue("Alert action log for the fix is missing",
                    logs.size() > 0);
        assertEquals("Wrong reason for the alert fix",
                     REASON, logs.get(0).getDetail());
        
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
        AlertResponse fixResponse = apiUnpriv.fixAlert(a.getId());
        hqAssertFailurePermissionDenied(fixResponse);


        // Cleanup
        deleteAlertDefinitionByAlert(a);
        deleteTestUsers(users);
    }

    public void testFixPlatformAlertingPermission() throws Exception {
        Resource platform = getLocalPlatformResource(false, false);
        Alert a = generateAlerts(platform);

        validateAlert(a);

        // Create user/group/role with insufficient permissions
        List<User> users = createTestUsers(1);
        User unprivUser = users.get(0);
        Role alertRole = createRole(Collections.singletonList(unprivUser),
                                    Collections.singletonList(Operation.MANAGE_PLATFORM_ALERTS));
        Group groupWithRole = createGroup(Collections.singletonList(platform),
                                          Collections.singletonList(alertRole));

        AlertApi apiUnpriv = getApi(unprivUser.getName(), TESTUSER_PASSWORD).getAlertApi();

        // Test marking fixed with an unprivileged user.
        // Role needs alerting and at least view resource permissions
        // in order to fix alerts
        AlertResponse fixResponse = apiUnpriv.fixAlert(a.getId());
        hqAssertFailurePermissionDenied(fixResponse);

        // Cleanup
        deleteAlertDefinitionByAlert(a);
        deleteTestUsers(users);
        cleanupRole(alertRole);
        cleanupGroup(groupWithRole);
    }
    
    public void testFixServerAlertNoPermission() throws Exception {
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
        AlertResponse fixResponse = apiUnpriv.fixAlert(a.getId());
        hqAssertFailurePermissionDenied(fixResponse);

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
        AlertResponse fixResponse = apiUnpriv.fixAlert(a.getId());
        hqAssertFailurePermissionDenied(fixResponse);

        // Cleanup
        deleteAlertDefinitionByAlert(a);
        deleteTestUsers(users);
    }

    public void testFixInvalidAlert() throws Exception {

        AlertApi api = getAlertApi();

        AlertResponse response = api.fixAlert(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
}
