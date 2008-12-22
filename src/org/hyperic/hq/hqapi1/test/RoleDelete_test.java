package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RoleResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.UserApi;

public class RoleDelete_test extends RoleTestBase {

    public RoleDelete_test(String name) {
        super(name);
    }

    public void testDelete() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        RoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);

        Role role = createResponse.getRole();
        StatusResponse deleteResponse = api.deleteRole(role.getId());
        hqAssertSuccess(deleteResponse);

        RoleResponse getResponse = api.getRole(role.getId());
        hqAssertFailureObjectNotFound(getResponse);
    }

    public void testDeleteNonExistantRole() throws Exception {

        RoleApi api = getRoleApi();

        StatusResponse response = api.deleteRole(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
    
    public void testDeleteNoPermission() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        RoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);
        
        //Create an underprivileged user
    	UserApi userapi = getUserApi();

        User user = generateTestUser();

        userapi.createUser(user, PASSWORD);
        
        RoleApi roleapi = getRoleApi(user.getName(), PASSWORD);
        Role role = createResponse.getRole();
        StatusResponse deleteResponse = roleapi.deleteRole(role.getId());
        hqAssertFailurePermissionDenied(deleteResponse);
    }
}
