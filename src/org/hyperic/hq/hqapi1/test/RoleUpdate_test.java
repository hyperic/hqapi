package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.CreateUserResponse;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.CreateRoleResponse;
import org.hyperic.hq.hqapi1.types.UpdateRoleResponse;
import org.hyperic.hq.hqapi1.types.GetRoleResponse;
import org.hyperic.hq.hqapi1.types.Operation;
import org.hyperic.hq.hqapi1.types.User;

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

    public void testUpdateNull() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        CreateRoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);

        Role role = createResponse.getRole();

        role.setName(null);
        role.setDescription(null);

        UpdateRoleResponse updateResponse = api.updateRole(role);
        hqAssertSuccess(updateResponse);

        GetRoleResponse getResponse = api.getRole(role.getId());
        hqAssertSuccess(getResponse);

        Role updatedRole = getResponse.getRole();
        // Setting to null should preserve the existing fields.
        assertEquals(r.getName(), updatedRole.getName());
        assertEquals(r.getDescription(), updatedRole.getDescription());
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
    
    public void testUpdateRoleDuplicate() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        CreateRoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);

        Role r1 = generateTestRole();

        api.createRole(r1);
        //For the update role use the existing role
        final String UPDATED_ROLENAME    = r1.getName();       

        Role role = createResponse.getRole();

        role.setName(UPDATED_ROLENAME);        

        UpdateRoleResponse updateResponse = api.updateRole(role);
        
        hqAssertFailureObjectExists(updateResponse);
    }
    
    public void testUpdateRoleNoPermission() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        CreateRoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);
        
        //Create an underprivileged user
    	UserApi userapi = getUserApi();

        User user = generateTestUser();

        userapi.createUser(user, PASSWORD);
        
        RoleApi roleapi = getRoleApi(user.getName(), PASSWORD);

        //For the update role use the existing role
        final String UPDATED_ROLENAME    = TESTROLE_NAME_PREFIX + " (Updated)";      

        Role role = createResponse.getRole();

        role.setName(UPDATED_ROLENAME);        

        UpdateRoleResponse updateResponse = roleapi.updateRole(role);
        
        hqAssertFailurePermissionDenied(updateResponse);
    }
}
