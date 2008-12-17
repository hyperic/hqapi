package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.RoleResponse;

import java.util.List;
import java.util.ArrayList;

public class RoleSetUsers_test extends RoleTestBase {

    public RoleSetUsers_test(String name) {
        super(name);
    }

    public void testSetUser() throws Exception {

        RoleApi roleApi = getRoleApi();
        Role r = generateTestRole();

        RoleResponse createRoleResponse = roleApi.createRole(r);
        hqAssertSuccess(createRoleResponse);

        Role role = createRoleResponse.getRole();
        List<User> users = createTestUsers(1);
        StatusResponse setUserResponse = roleApi.setUsers(role, users);
        hqAssertSuccess(setUserResponse);
    }

    public void testSetUsers() throws Exception {

        RoleApi roleApi = getRoleApi();
        Role r = generateTestRole();

        RoleResponse createRoleResponse = roleApi.createRole(r);
        hqAssertSuccess(createRoleResponse);

        Role role = createRoleResponse.getRole();
        List<User> users = createTestUsers(5);
        StatusResponse setUserResponse = roleApi.setUsers(role, users);
        hqAssertSuccess(setUserResponse);
    }

    public void testSetEmptyList() throws Exception {

        RoleApi roleApi = getRoleApi();
        Role r = generateTestRole();

        RoleResponse createRoleResponse = roleApi.createRole(r);
        hqAssertSuccess(createRoleResponse);

        Role role = createRoleResponse.getRole();
        List<User> users = new ArrayList<User>();
        StatusResponse setUserResponse = roleApi.setUsers(role, users);
        hqAssertSuccess(setUserResponse);
    }
}
