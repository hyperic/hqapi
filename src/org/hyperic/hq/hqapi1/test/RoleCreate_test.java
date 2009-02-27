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

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.Operation;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RoleResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UsersResponse;

import java.util.ArrayList;
import java.util.List;

public class RoleCreate_test extends RoleTestBase {

    public RoleCreate_test(String name) {
        super(name);
    }

    public void testRoleCreateNoOps() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        RoleResponse response = api.createRole(r);
        hqAssertSuccess(response);
    }

    public void testRoleCreateViewOps() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        r.getOperation().addAll(VIEW_OPS);

        RoleResponse response = api.createRole(r);
        hqAssertSuccess(response);

        Role role = response.getRole();
        for (Operation o : VIEW_OPS) {
            assertTrue("Created role does not contain operation " + o.value(),
                       role.getOperation().contains(o));
        }
    }

    public void testRoleCreateDuplicate() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        RoleResponse response = api.createRole(r);
        hqAssertSuccess(response);

        RoleResponse existsResponse = api.createRole(r);
        hqAssertFailureObjectExists(existsResponse);
    }
    
    public void testRoleCreateNoPermission() throws Exception {

    	//Create an underprivileged user
    	UserApi userapi = getUserApi();

        User user = generateTestUser();

        userapi.createUser(user, PASSWORD);
        
        RoleApi api = getRoleApi(user.getName(), PASSWORD);
        Role r = generateTestRole();

        RoleResponse roleResponse = api.createRole(r);
        hqAssertFailurePermissionDenied(roleResponse);
    }

    public void testRoleCreateWithValidUsers() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        UserApi userApi = getUserApi();
        UsersResponse users = userApi.getUsers();
        hqAssertSuccess(users);

        r.getOperation().addAll(VIEW_OPS);
        r.getUser().addAll(users.getUser());

        RoleResponse response = api.createRole(r);
        hqAssertSuccess(response);

        Role role = response.getRole();
        for (Operation o : VIEW_OPS) {
            assertTrue("Created role does not contain operation " + o.value(),
                       role.getOperation().contains(o));
        }
        assertTrue(role.getUser().size() == users.getUser().size());
        for (User u : role.getUser()) {
            assertNotNull(u.getName());
        }

        // Assert a later look up of this Role is correct
        RoleResponse roleResponse = api.getRole(role.getId());
        hqAssertSuccess(roleResponse);

        role = roleResponse.getRole();
        for (Operation o : VIEW_OPS) {
            assertTrue("Looked up role does not contain operation " + o.value(),
                       role.getOperation().contains(o));
        }

        assertTrue(role.getUser().size() == users.getUser().size());
        for (User u : role.getUser()) {
            assertNotNull(u.getName());
        }
    }

    public void testRoleCreateWithInvalidUsers() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        List<User> users = new ArrayList<User>();
        for (int i = 0; i < 5; i++) {
            users.add(generateTestUser());
        }

        r.getOperation().addAll(VIEW_OPS);
        r.getUser().addAll(users);

        RoleResponse response = api.createRole(r);
        hqAssertSuccess(response);

        Role role = response.getRole();
        for (Operation o : VIEW_OPS) {
            assertTrue("Created role does not contain operation " + o.value(),
                       role.getOperation().contains(o));
        }

        // Should return 0 users since Role creation will not create new users.
        assertTrue(role.getUser().size() == 0);
    }
}
