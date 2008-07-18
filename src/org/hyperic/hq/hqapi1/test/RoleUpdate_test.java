package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.CreateRoleResponse;
import org.hyperic.hq.hqapi1.types.UpdateRoleResponse;
import org.hyperic.hq.hqapi1.types.GetRoleResponse;
import org.hyperic.hq.hqapi1.types.Operation;

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

    public void testUpdateRoleAddOperations() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        CreateRoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);

        Role role = createResponse.getRole();
        role.getOperation().addAll(VIEW_OPS);

        UpdateRoleResponse updateResponse = api.updateRole(role);
        hqAssertSuccess(updateResponse);

        GetRoleResponse getResponse = api.getRole(role.getId());
        hqAssertSuccess(getResponse);

        Role updatedRole = getResponse.getRole();
        for (Operation o : VIEW_OPS) {
            assertTrue("Created role does not contain operation " + o.value(),
                       updatedRole.getOperation().contains(o));
        }        
    }

    public void testUpdateRoleUpdateOperations() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();
        r.getOperation().addAll(VIEW_OPS);

        CreateRoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);

        Role role = createResponse.getRole();
        role.getOperation().clear();
        role.getOperation().addAll(MODIFY_OPS);

        UpdateRoleResponse updateResponse = api.updateRole(role);
        hqAssertSuccess(updateResponse);

        GetRoleResponse getResponse = api.getRole(role.getId());
        hqAssertSuccess(getResponse);

        Role updatedRole = getResponse.getRole();

        // Assert none of the original operations exist
        for (Operation o : VIEW_OPS) {
            assertTrue("Updated role contains operation " + o.value(),
                       !updatedRole.getOperation().contains(o));
        }

        // Assert all the added operations are present
        for (Operation o : MODIFY_OPS) {
            assertTrue("Updated role does not contain operation " + o.value(),
                       updatedRole.getOperation().contains(o));
        }
    }
}
