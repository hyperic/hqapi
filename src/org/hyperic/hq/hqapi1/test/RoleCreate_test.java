package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.RoleResponse;
import org.hyperic.hq.hqapi1.types.Operation;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.User;

public class RoleCreate_test extends RoleTestBase {

    public RoleCreate_test(String name) {
        super(name);
    }

    public void testRoleCreateNoOps() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        RoleResponse response = api.createRole(r);
        hqAssertSuccess(response);
    }

    public void testRoleCreateViewOps() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        r.getOperation().addAll(VIEW_OPS);

        RoleResponse response = api.createRole(r);
        hqAssertSuccess(response);

        Role role = response.getRole();
        for (Operation o : VIEW_OPS) {
            assertTrue("Created role does not contain operation " + o.value(),
                       role.getOperation().contains(o));
        }
    }

    public void testRoleCreateDuplicate() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        RoleResponse response = api.createRole(r);
        hqAssertSuccess(response);

        RoleResponse existsResponse = api.createRole(r);
        hqAssertFailureObjectExists(existsResponse);
    }
    
    public void testRoleCreateNoPermission() throws Exception {

    	//Create an underprivileged user
    	UserApi userapi = getUserApi();

        User user = generateTestUser();

        userapi.createUser(user, PASSWORD);
        
        RoleApi api = getRoleApi(user.getName(), PASSWORD);
        Role r = generateTestRole();

        RoleResponse roleResponse = api.createRole(r);
        hqAssertFailurePermissionDenied(roleResponse);
    }
}
