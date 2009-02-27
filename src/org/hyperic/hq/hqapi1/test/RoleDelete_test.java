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
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RoleResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;

public class RoleDelete_test extends RoleTestBase {

    public RoleDelete_test(String name) {
        super(name);
    }

    public void testDelete() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        RoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);

        Role role = createResponse.getRole();
        StatusResponse deleteResponse = api.deleteRole(role.getId());
        hqAssertSuccess(deleteResponse);

        RoleResponse getResponse = api.getRole(role.getId());
        hqAssertFailureObjectNotFound(getResponse);
    }

    public void testDeleteNonExistantRole() throws Exception {

        RoleApi api = getRoleApi();

        StatusResponse response = api.deleteRole(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
    
    public void testDeleteNoPermission() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        RoleResponse createResponse = api.createRole(r);
        hqAssertSuccess(createResponse);
        
        //Create an underprivileged user
    	UserApi userapi = getUserApi();

        User user = generateTestUser();

        userapi.createUser(user, PASSWORD);
        
        RoleApi roleapi = getRoleApi(user.getName(), PASSWORD);
        Role role = createResponse.getRole();
        StatusResponse deleteResponse = roleapi.deleteRole(role.getId());
        hqAssertFailurePermissionDenied(deleteResponse);
    }
}
