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

public class RoleUpdate_test extends RoleTestBase {

    public RoleUpdate_test(String name) {
        super(name);
    }

    public void testUpdateRole() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        RoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);

        final String UPDATED_ROLENAME    = TESTROLE_NAME_PREFIX + " (Updated)";
        final String UPDATED_DESCRIPTION = TESTROLE_DESCRIPTION + " (Updated)";

        Role role = createResponse.getRole();

        role.setName(UPDATED_ROLENAME);
        role.setDescription(UPDATED_DESCRIPTION);

        StatusResponse updateResponse = api.updateRole(role);
        hqAssertSuccess(updateResponse);

        RoleResponse getResponse = api.getRole(role.getId());
        hqAssertSuccess(getResponse);

        Role updatedRole = getResponse.getRole();
        assertEquals(UPDATED_ROLENAME, updatedRole.getName());
        assertEquals(UPDATED_DESCRIPTION, updatedRole.getDescription());
    }

    public void testUpdateNull() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        RoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);

        Role role = createResponse.getRole();

        role.setName(null);
        role.setDescription(null);

        StatusResponse updateResponse = api.updateRole(role);
        hqAssertSuccess(updateResponse);

        RoleResponse getResponse = api.getRole(role.getId());
        hqAssertSuccess(getResponse);

        Role updatedRole = getResponse.getRole();
        // Setting to null should preserve the existing fields.
        assertEquals(r.getName(), updatedRole.getName());
        assertEquals(r.getDescription(), updatedRole.getDescription());
    }

    public void testUpdateRoleAddOperations() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        RoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);

        Role role = createResponse.getRole();
        role.getOperation().addAll(VIEW_OPS);

        StatusResponse updateResponse = api.updateRole(role);
        hqAssertSuccess(updateResponse);

        RoleResponse getResponse = api.getRole(role.getId());
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

        RoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);

        Role role = createResponse.getRole();
        role.getOperation().clear();
        role.getOperation().addAll(MODIFY_OPS);

        StatusResponse updateResponse = api.updateRole(role);
        hqAssertSuccess(updateResponse);

        RoleResponse getResponse = api.getRole(role.getId());
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

        RoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);

        Role r1 = generateTestRole();

        api.createRole(r1);
        //For the update role use the existing role
        final String UPDATED_ROLENAME    = r1.getName();       

        Role role = createResponse.getRole();

        role.setName(UPDATED_ROLENAME);        

        StatusResponse updateResponse = api.updateRole(role);
        
        hqAssertFailureObjectExists(updateResponse);
    }
    
    public void testUpdateRoleNoPermission() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        RoleResponse createResponse = api.createRole(r);
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

        StatusResponse updateResponse = roleapi.updateRole(role);
        
        hqAssertFailurePermissionDenied(updateResponse);
    }

    public void testUpdateSystemRole() throws Exception {

        RoleApi api = getRoleApi();

        Role r = new Role();
        r.setId(0);
        r.setName("Updated Super User Role!");

        StatusResponse response = api.updateRole(r);
        hqAssertFailureNotSupported(response);
    }

    public void testUpdateValidUsers() throws Exception {

        HQApi api = getApi();
        RoleApi roleApi = api.getRoleApi();
        Role r = generateTestRole();

        RoleResponse createResponse = roleApi.createRole(r);
        hqAssertSuccess(createResponse);

        // Add all users.
        UserApi userApi = getUserApi();
        UsersResponse users = userApi.getUsers();
        hqAssertSuccess(users);

        Role role = createResponse.getRole();
        role.getUser().addAll(users.getUser());
        StatusResponse updateResponse = roleApi.updateRole(role);
        hqAssertSuccess(updateResponse);

        RoleResponse getResponse = roleApi.getRole(r.getName());
        hqAssertSuccess(getResponse);
        assertTrue("Updated role does not contain all the users",
                   getResponse.getRole().getUser().size() == users.getUser().size());
    }

    public void testUpdateInvalidUsers() throws Exception {

        HQApi api = getApi();
        RoleApi roleApi = api.getRoleApi();
        Role r = generateTestRole();

        RoleResponse createResponse = roleApi.createRole(r);
        hqAssertSuccess(createResponse);

        // Add invalid users.
        List<User> users = new ArrayList<User>();
        for (int i = 0; i < 5; i++) {
            users.add(generateTestUser());
        }

        Role role = createResponse.getRole();
        role.getUser().addAll(users);
        StatusResponse updateResponse = roleApi.updateRole(role);
        hqAssertFailureObjectNotFound(updateResponse);
    }
}
