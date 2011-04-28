/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

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

        userapi.createUser(user, TESTUSER_PASSWORD);
        
        RoleApi api = getRoleApi(user.getName(), TESTUSER_PASSWORD);

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

        userapi.createUser(user, TESTUSER_PASSWORD);
        
        RoleApi roleapi = getRoleApi(user.getName(), TESTUSER_PASSWORD);
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

    public void testSyncRenameSuperUserRole() throws Exception {

        RoleApi api = getRoleApi();

        Role r = new Role();
        r.setId(0);
        r.setName("Updated Super User Role!");

        List<Role> roles = new ArrayList<Role>();
        roles.add(r);

        StatusResponse response = api.syncRoles(roles);
        hqAssertFailureNotSupported(response);              
    }

    public void testSyncRenameGuestRole() throws Exception {

        RoleApi api = getRoleApi();

        Role r = new Role();
        r.setId(2);
        r.setName("Updated Guest Role!");

        List<Role> roles = new ArrayList<Role>();
        roles.add(r);

        StatusResponse response = api.syncRoles(roles);
        hqAssertFailureNotSupported(response);              
    }

    
    public void testSyncRolesInvalidUsers() throws Exception {
        RoleApi api = getRoleApi();

        Role r = new Role();
        r.setName("New Role with invalid users");

        User u = new User();
        u.setName("Invalid User");
        r.getUser().add(u);

        List<Role> roles = new ArrayList<Role>();
        roles.add(r);

        StatusResponse response = api.syncRoles(roles);
        hqAssertFailureObjectNotFound(response);
    }
    
    public void testSyncSuperUserRoleWithOutHqadmin() throws Exception {
        RoleApi api = getRoleApi();

        Role r = new Role();
        r.setId(0);
        r.setName("Super User Role");

        List<Role> roles = new ArrayList<Role>();
        roles.add(r);

        StatusResponse response = api.syncRoles(roles);
        hqAssertFailureNotSupported(response);   
    }

    public void testSyncGuestRoleWithOutGuest() throws Exception {
        RoleApi api = getRoleApi();

        Role r = new Role();
        r.setId(2);
        r.setName("Guest Role");

        List<Role> roles = new ArrayList<Role>();
        roles.add(r);

        StatusResponse response = api.syncRoles(roles);
        hqAssertFailureNotSupported(response);   
    }

    public void testSyncSuperUserRoleWithOutHqadminNoPermission() throws Exception {
        //Create an underprivileged user
        UserApi userapi = getUserApi();

        User user = generateTestUser();

        userapi.createUser(user, TESTUSER_PASSWORD);
        
        RoleApi roleapi = getRoleApi(user.getName(), TESTUSER_PASSWORD);

        Role r = new Role();
        r.setId(0);
        r.setName("Super User Role");
        User hqadmin = new User();
        hqadmin.setName("HQ Administrator");
        hqadmin.setId(0);
        r.getUser().add(hqadmin);

        r.getUser().add(hqadmin);
        r.getUser().add(user); 
        
        List<Role> roles = new ArrayList<Role>();
        roles.add(r);

        StatusResponse response = roleapi.syncRoles(roles);
        hqAssertFailurePermissionDenied(response);   
    }

    public void testSyncGuestRoleWithOutGuestNoPermission() throws Exception {
        //Create an underprivileged user
        UserApi userapi = getUserApi();

        User user = generateTestUser();

        userapi.createUser(user, TESTUSER_PASSWORD);
        
        RoleApi roleapi = getRoleApi(user.getName(), TESTUSER_PASSWORD);

        Role r = new Role();
        r.setId(2);
        r.setName("Guest Role");
        
        User guest = new User();
        guest.setName("Guest User");
        guest.setId(2);
        r.getUser().add(guest);
        r.getUser().add(user); 

        List<Role> roles = new ArrayList<Role>();
        roles.add(r);
        
        //RoleManager.addSubjects() doesn't throw a PermissionException
        //as it claims. So we need to work around it
        
        StatusResponse response = roleapi.syncRoles(roles);
        hqAssertFailurePermissionDenied(response);   
    }
    
    
}
