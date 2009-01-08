package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RoleResponse;

public class RoleGetRole_test extends RoleTestBase {

    public RoleGetRole_test(String name) {
        super(name);
    }

    public void testGetRoleByName() throws Exception {

        RoleApi api = getRoleApi();

        RoleResponse response = api.getRole(GUEST_ROLENAME);
        hqAssertSuccess(response);

        Role r = response.getRole();
        assertEquals(2, r.getId().intValue());
        assertTrue("Guest role does not have a single user",
                   r.getUser().size() == 1);
    }

    public void testGetRoleNameInvalid() throws Exception {

        RoleApi api = getRoleApi();

        RoleResponse response = api.getRole("Non-existant Role Name");
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetRoleId() throws Exception {

        RoleApi api = getRoleApi();

        RoleResponse response = api.getRole(2);
        hqAssertSuccess(response);

        Role r = response.getRole();
        assertEquals(GUEST_ROLENAME, r.getName());
    }

    public void testGetRoleIdInvalid() throws Exception {

        RoleApi api = getRoleApi();

        RoleResponse response = api.getRole(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
}
