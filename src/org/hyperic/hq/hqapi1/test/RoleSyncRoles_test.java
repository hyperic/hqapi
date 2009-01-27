package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.Operation;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RoleResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UsersResponse;

import java.util.ArrayList;
import java.util.List;

public class RoleSyncRoles_test extends RoleTestBase {

    private static final int SYNC_NUM = 3;

    public RoleSyncRoles_test(String name) {
        super(name);
    }

    public void testSyncCreate() throws Exception {

        HQApi api = getApi();
        RoleApi roleApi = api.getRoleApi();
        UserApi userApi = api.getUserApi();

        UsersResponse users = userApi.getUsers();
        hqAssertSuccess(users);

        List<Role> roles = new ArrayList<Role>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Role r = generateTestRole();
            r.getOperation().addAll(VIEW_OPS);
            r.getUser().addAll(users.getUser());
            roles.add(r);
        }

        StatusResponse response = roleApi.syncRoles(roles);
        hqAssertSuccess(response);

        for (Role r : roles) {
            RoleResponse getResponse = roleApi.getRole(r.getName());
            hqAssertSuccess(getResponse);
            Role role = getResponse.getRole();
            // Created roles will have a valid id
            assertNotNull("Role id was null", role.getId());
            assertTrue(role.getOperation().size() == VIEW_OPS.size());
            assertTrue(role.getUser().size() == users.getUser().size());
        }
    }

    public void testSyncUpdate() throws Exception {

        HQApi api = getApi();
        RoleApi roleApi = api.getRoleApi();
        UserApi userApi = api.getUserApi();

        UsersResponse users = userApi.getUsers();
        hqAssertSuccess(users);

        List<Role> createdRoles = new ArrayList<Role>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Role r = generateTestRole();
            r.getOperation().addAll(VIEW_OPS);
            r.getUser().addAll(users.getUser());

            RoleResponse createResponse = roleApi.createRole(r);
            hqAssertSuccess(createResponse);
            createdRoles.add(createResponse.getRole());
        }

        final String UPDATE_STR = " (Updated)";

        for (Role r : createdRoles) {
            r.setName(r.getName() + UPDATE_STR);
            r.setDescription(r.getDescription() + UPDATE_STR);
            r.getOperation().clear();
            r.getOperation().addAll(MODIFY_OPS);
            r.getUser().clear();
        }

        StatusResponse response = roleApi.syncRoles(createdRoles);
        hqAssertSuccess(response);

        for (Role r : createdRoles) {
            RoleResponse getResponse = roleApi.getRole(r.getId());
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

            assertTrue("All users were not removed from role",
                       r.getUser().size() == 0);
        }
    }

    public void testSyncCreateNoPermission() throws Exception {

    	//Create an underprivileged user
    	UserApi userapi = getUserApi();

        User user = generateTestUser();

        userapi.createUser(user, PASSWORD);
        
        RoleApi api = getRoleApi(user.getName(), PASSWORD);

        List<Role> roles = new ArrayList<Role>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Role r = generateTestRole();
            r.getOperation().addAll(VIEW_OPS);
            roles.add(r);
        }

        StatusResponse response = api.syncRoles(roles);
        hqAssertFailurePermissionDenied(response);
    }
    
    public void testSyncUpdateNoPermission() throws Exception {

        RoleApi api = getRoleApi();

        List<Role> createdRoles = new ArrayList<Role>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Role r = generateTestRole();
            r.getOperation().addAll(VIEW_OPS);

            RoleResponse createResponse = api.createRole(r);
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

        StatusResponse response = roleapi.syncRoles(createdRoles);
        hqAssertFailurePermissionDenied(response);
    }

    public void testSyncSystemRole() throws Exception {

        RoleApi api = getRoleApi();

        Role r = new Role();
        r.setId(0);
        r.setName("Updated Super User Role!");

        List<Role> roles = new ArrayList<Role>();
        roles.add(r);

        StatusResponse response = api.syncRoles(roles);
        hqAssertFailureNotSupported(response);              
    }
}
