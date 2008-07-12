package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.GetRoleResponse;
import org.hyperic.hq.hqapi1.types.Role;

public class RoleGetRole_test extends RoleTestBase {

    public RoleGetRole_test(String name) {
        super(name);
    }

    public void testGetRoleByName() throws Exception {

        RoleApi api = getRoleApi();

        GetRoleResponse response = api.getRole(GUEST_ROLENAME);
        hqAssertSuccess(response);

        Role r = response.getRole();
        assertEquals(2, r.getId());
    }

    public void testGetRoleNameInvalid() throws Exception {

        RoleApi api = getRoleApi();

        GetRoleResponse response = api.getRole("Non-existant Role Name");
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetRoleId() throws Exception {

        RoleApi api = getRoleApi();

        GetRoleResponse response = api.getRole(2);
        hqAssertSuccess(response);

        Role r = response.getRole();
        assertEquals(GUEST_ROLENAME, r.getName());
    }

    public void testGetRoleIdInvalid() throws Exception {

        RoleApi api = getRoleApi();

        GetRoleResponse response = api.getRole(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
}
