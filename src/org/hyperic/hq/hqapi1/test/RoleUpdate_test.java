package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.CreateRoleResponse;
import org.hyperic.hq.hqapi1.types.UpdateRoleResponse;
import org.hyperic.hq.hqapi1.types.GetRoleResponse;

public class RoleUpdate_test extends RoleTestBase {

    public RoleUpdate_test(String name) {
        super(name);
    }

    public void testUpdateRole() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        CreateRoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);

        final String UPDATED_ROLENAME    = TESTROLE_NAME_PREFIX + " (Updated)";
        final String UPDATED_DESCRIPTION = TESTROLE_DESCRIPTION + " (Updated)";

        Role role = createResponse.getRole();

        role.setName(UPDATED_ROLENAME);
        role.setDescription(UPDATED_DESCRIPTION);

        UpdateRoleResponse updateResponse = api.updateRole(role);
        hqAssertSuccess(updateResponse);

        GetRoleResponse getResponse = api.getRole(role.getId());
        hqAssertSuccess(getResponse);

        Role updatedRole = getResponse.getRole();
        assertEquals(UPDATED_ROLENAME, updatedRole.getName());
        assertEquals(UPDATED_DESCRIPTION, updatedRole.getDescription());
    }
}
