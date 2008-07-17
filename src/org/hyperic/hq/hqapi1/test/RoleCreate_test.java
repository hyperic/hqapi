package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.CreateRoleResponse;
import org.hyperic.hq.hqapi1.RoleApi;

public class RoleCreate_test extends RoleTestBase {

    public RoleCreate_test(String name) {
        super(name);
    }

    public void testRoleCreate() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        CreateRoleResponse response = api.createRole(r);
        hqAssertSuccess(response);
    }

    public void testRoleCreateDuplicate() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        CreateRoleResponse response = api.createRole(r);
        hqAssertSuccess(response);

        CreateRoleResponse existsResponse = api.createRole(r);
        hqAssertFailureObjectExists(existsResponse);
    }
}
