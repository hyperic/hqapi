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
import org.hyperic.hq.hqapi1.types.RolesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class RoleGetRoles_test extends RoleTestBase {

    public RoleGetRoles_test(String name) {
        super(name);
    }

    public void testGetAllRoles() throws Exception {
        boolean containsSuperUser = false;
        RoleApi api = getRoleApi();

        RolesResponse response = api.getRoles();
        hqAssertSuccess(response);

        List<Role> roles = response.getRole();
        assertNotNull(roles);

        for (Role role : response.getRole()) {
            for (Operation o : role.getOperation()) {
                assertNotNull(o);
            }
            if (role.getId() == 0) {
                containsSuperUser = true;
            }
            assertNotNull(role.getUser());
        }
        
        assertEquals("Did not include Super User Role", containsSuperUser, true);
        
    }

    public void testGetRolesByUser() throws Exception {

        RoleApi api = getRoleApi();
        UserApi uApi = getUserApi();

        User u = generateTestUser();
        UserResponse uResponse = uApi.createUser(u, TESTUSER_PASSWORD);
        hqAssertSuccess(uResponse);

        List<Role> roles = new ArrayList<Role>();
        // Create 10 Roles adding the generated user to 5 of them.
        int SYNC_NUM = 10;
        for (int i = 0; i < SYNC_NUM; i++) {
            Role r = generateTestRole();
            r.getOperation().addAll(VIEW_OPS);
            if (i < 5) {
                r.getUser().add(uResponse.getUser());
            }
            roles.add(r);
        }

        StatusResponse response = api.syncRoles(roles);
        hqAssertSuccess(response);

        RolesResponse rolesResponse = api.getRoles(uResponse.getUser());
        hqAssertSuccess(rolesResponse);

        assertEquals("Wrong number of roles for user",
                     5, rolesResponse.getRole().size());
    }

    public void testGetRolesInvalidUser() throws Exception {

        RoleApi api = getRoleApi();

        User u = new User();
        u.setName("testUser");
        RolesResponse response = api.getRoles(u);

        hqAssertFailureObjectNotFound(response);
    }
}
