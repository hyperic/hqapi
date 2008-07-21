package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.CreateRoleResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.SetUsersResponse;
import org.hyperic.hq.hqapi1.types.GetUsersResponse;

import java.util.List;

public class RoleGetUsers_test extends RoleTestBase {

    public RoleGetUsers_test(String name) {
        super(name);
    }

    public void testRoleGetUsers() throws Exception {

        RoleApi roleApi = getRoleApi();
        Role r = generateTestRole();

        CreateRoleResponse createRoleResponse = roleApi.createRole(r);
        hqAssertSuccess(createRoleResponse);

        Role role = createRoleResponse.getRole();
        List<User> users = createTestUsers(5);
        SetUsersResponse setUserResponse = roleApi.setUsers(role, users);
        hqAssertSuccess(setUserResponse);

        GetUsersResponse getResponse = roleApi.getUsers(role);
        hqAssertSuccess(getResponse);
        assertEquals(users.size(), getResponse.getUser().size());
    }
}
