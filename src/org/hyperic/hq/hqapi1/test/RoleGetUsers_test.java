package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UsersResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.RoleResponse;

import java.util.List;

public class RoleGetUsers_test extends RoleTestBase {

    public RoleGetUsers_test(String name) {
        super(name);
    }

    public void testRoleGetUsers() throws Exception {

        RoleApi roleApi = getRoleApi();
        Role r = generateTestRole();

        RoleResponse createRoleResponse = roleApi.createRole(r);
        hqAssertSuccess(createRoleResponse);

        Role role = createRoleResponse.getRole();
        List<User> users = createTestUsers(5);
        StatusResponse setUserResponse = roleApi.setUsers(role, users);
        hqAssertSuccess(setUserResponse);

        UsersResponse getResponse = roleApi.getUsers(role);
        hqAssertSuccess(getResponse);
        assertEquals(users.size(), getResponse.getUser().size());
    }
}
