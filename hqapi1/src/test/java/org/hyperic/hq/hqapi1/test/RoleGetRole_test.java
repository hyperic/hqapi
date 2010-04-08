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
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RoleResponse;

public class RoleGetRole_test extends RoleTestBase {

    public RoleGetRole_test(String name) {
        super(name);
    }

    public void testGetRoleByName() throws Exception {

        RoleApi api = getRoleApi();

        RoleResponse response = api.getRole(GUEST_ROLENAME);
        hqAssertSuccess(response);

        Role r = response.getRole();
        assertEquals(2, r.getId().intValue());
        assertTrue("Guest role does not have a single user",
                   r.getUser().size() == 1);
    }

    public void testGetRoleNameInvalid() throws Exception {

        RoleApi api = getRoleApi();

        RoleResponse response = api.getRole("Non-existant Role Name");
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetRoleId() throws Exception {

        RoleApi api = getRoleApi();

        RoleResponse response = api.getRole(2);
        hqAssertSuccess(response);

        Role r = response.getRole();
        assertEquals(GUEST_ROLENAME, r.getName());
    }

    public void testGetRoleIdInvalid() throws Exception {

        RoleApi api = getRoleApi();

        RoleResponse response = api.getRole(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetSystemRole() throws Exception {
        // System roles may not be modified

        RoleApi api = getRoleApi();

        RoleResponse response = api.getRole(0);
        hqAssertFailureNotSupported(response);
    }
}
