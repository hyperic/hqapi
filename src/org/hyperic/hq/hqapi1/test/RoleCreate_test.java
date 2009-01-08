package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.Operation;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RoleResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UsersResponse;

import java.util.ArrayList;
import java.util.List;

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

    public void testRoleCreateWithValidUsers() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        UserApi userApi = getUserApi();
        UsersResponse users = userApi.getUsers();
        hqAssertSuccess(users);

        r.getOperation().addAll(VIEW_OPS);
        r.getUser().addAll(users.getUser());

        RoleResponse response = api.createRole(r);
        hqAssertSuccess(response);

        Role role = response.getRole();
        for (Operation o : VIEW_OPS) {
            assertTrue("Created role does not contain operation " + o.value(),
                       role.getOperation().contains(o));
        }
        assertTrue(role.getUser().size() == users.getUser().size());
        for (User u : role.getUser()) {
            // TODO: validateUser()
            assertNotNull(u.getName());
        }
    }

    public void testRoleCreateWithInvalidUsers() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        List<User> users = new ArrayList<User>();
        for (int i = 0; i < 5; i++) {
            users.add(generateTestUser());
        }

        r.getOperation().addAll(VIEW_OPS);
        r.getUser().addAll(users);

        RoleResponse response = api.createRole(r);
        hqAssertSuccess(response);

        Role role = response.getRole();
        for (Operation o : VIEW_OPS) {
            assertTrue("Created role does not contain operation " + o.value(),
                       role.getOperation().contains(o));
        }

        // Should return 0 users since Role creation will not create new users.
        assertTrue(role.getUser().size() == 0);
    }
}
