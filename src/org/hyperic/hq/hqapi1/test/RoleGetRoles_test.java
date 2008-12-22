package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.RolesResponse;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.Operation;

import java.util.List;

public class RoleGetRoles_test extends RoleTestBase {

    public RoleGetRoles_test(String name) {
        super(name);
    }

    public void testGetAllRoles() throws Exception {

        RoleApi api = getRoleApi();

        RolesResponse response = api.getRoles();
        hqAssertSuccess(response);

        List<Role> roles = response.getRole();
        assertNotNull(roles);

        for (Role role : response.getRole()) {
            for (Operation o : role.getOperation()) {
                assertNotNull(o);
            }
            assertNotNull(role.getUser());
        }
    }
}
