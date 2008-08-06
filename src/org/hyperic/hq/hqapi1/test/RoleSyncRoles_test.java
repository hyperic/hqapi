package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.SyncRolesResponse;
import org.hyperic.hq.hqapi1.types.CreateRoleResponse;
import org.hyperic.hq.hqapi1.types.GetRoleResponse;
import org.hyperic.hq.hqapi1.types.Operation;
import org.hyperic.hq.hqapi1.types.User;

import java.util.List;
import java.util.ArrayList;

public class RoleSyncRoles_test extends RoleTestBase {

    public RoleSyncRoles_test(String name) {
        super(name);
    }

    public void testSyncCreate() throws Exception {

        RoleApi api = getRoleApi();

        List<Role> roles = new ArrayList<Role>();
        for (int i = 0; i < 5; i++) {
            Role r = generateTestRole();
            r.getOperation().addAll(VIEW_OPS);
            roles.add(r);
        }

        SyncRolesResponse response = api.syncRoles(roles);
        hqAssertSuccess(response);

        for (Role r : roles) {
            GetRoleResponse getResponse = api.getRole(r.getName());
            hqAssertSuccess(getResponse);
        }
    }

    public void testSyncUpdate() throws Exception {

        RoleApi api = getRoleApi();

        List<Role> createdRoles = new ArrayList<Role>();
        for (int i = 0; i < 5; i++) {
            Role r = generateTestRole();
            r.getOperation().addAll(VIEW_OPS);

            CreateRoleResponse createResponse = api.createRole(r);
            hqAssertSuccess(createResponse);
            createdRoles.add(createResponse.getRole());
        }

        final String UPDATE_STR = " (Updated)";

        for (Role r : createdRoles) {
            r.setName(r.getName() + UPDATE_STR);
            r.setDescription(r.getDescription() + UPDATE_STR);
            r.getOperation().clear();
            r.getOperation().addAll(MODIFY_OPS);
        }

        SyncRolesResponse response = api.syncRoles(createdRoles);
        hqAssertSuccess(response);

        for (Role r : createdRoles) {
            GetRoleResponse getResponse = api.getRole(r.getId());
            hqAssertSuccess(getResponse);

            Role updatedRole = getResponse.getRole();
            assertTrue("Could not find " + UPDATE_STR + " in name for role " + r.getId(),
                       updatedRole.getName().indexOf(UPDATE_STR) > 0);
            assertTrue("Could not find " + UPDATE_STR + " in role desc for role " + r.getId(),
                       updatedRole.getName().indexOf(UPDATE_STR) > 0);

            for (Operation o : VIEW_OPS) {
                assertTrue("Updated role contains operation " + o.value(),
                           !updatedRole.getOperation().contains(o));
            }
            
            for (Operation o : MODIFY_OPS) {
                assertTrue("Synced role does not contain operation " + o.value(),
                           updatedRole.getOperation().contains(o));
            }
        }
    }
    
    public void testSyncCreateNoPermission() throws Exception {

    	//Create an underprivileged user
    	UserApi userapi = getUserApi();

        User user = generateTestUser();

        userapi.createUser(user, PASSWORD);
        
        RoleApi api = getRoleApi(user.getName(), PASSWORD);

        List<Role> roles = new ArrayList<Role>();
        for (int i = 0; i < 5; i++) {
            Role r = generateTestRole();
            r.getOperation().addAll(VIEW_OPS);
            roles.add(r);
        }

        SyncRolesResponse response = api.syncRoles(roles);
        hqAssertFailurePermissionDenied(response);
    }
    
    public void testSyncUpdateNoPermission() throws Exception {

        RoleApi api = getRoleApi();

        List<Role> createdRoles = new ArrayList<Role>();
        for (int i = 0; i < 5; i++) {
            Role r = generateTestRole();
            r.getOperation().addAll(VIEW_OPS);

            CreateRoleResponse createResponse = api.createRole(r);
            hqAssertSuccess(createResponse);
            createdRoles.add(createResponse.getRole());
        }

        //Create an underprivileged user
    	UserApi userapi = getUserApi();

        User user = generateTestUser();

        userapi.createUser(user, PASSWORD);
        
        RoleApi roleapi = getRoleApi(user.getName(), PASSWORD);
        final String UPDATE_STR = " (Updated)";

        for (Role r : createdRoles) {
            r.setName(r.getName() + UPDATE_STR);
            r.setDescription(r.getDescription() + UPDATE_STR);
            r.getOperation().clear();
            r.getOperation().addAll(MODIFY_OPS);
        }

        SyncRolesResponse response = roleapi.syncRoles(createdRoles);
        hqAssertFailurePermissionDenied(response);
        }
}
