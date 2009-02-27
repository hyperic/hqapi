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

import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.UserResponse;

public class UserGetUser_test extends UserTestBase {

    public UserGetUser_test(String name) {
        super(name);
    }
                     
    public void testGetUserValid() throws Exception {
        UserApi api = getUserApi();
        UserResponse response = api.getUser("hqadmin");

        hqAssertSuccess(response);
        assertEquals("HQ", response.getUser().getFirstName());
        assertEquals("Administrator", response.getUser().getLastName());
    }

    public void testGetUserInvalid() throws Exception {
        UserApi api = getUserApi();
        UserResponse response = api.getUser("unknownUser");

        hqAssertFailureObjectNotFound(response);
    }

    public void testGetUserById() throws Exception {
        UserApi api = getUserApi();
        UserResponse response = api.getUser(1);

        hqAssertSuccess(response);
        assertEquals("HQ", response.getUser().getFirstName());
        assertEquals("Administrator", response.getUser().getLastName());
    }

    public void testGetUserByIdInvalid() throws Exception {
        UserApi api = getUserApi();
        UserResponse response = api.getUser(Integer.MAX_VALUE);

        hqAssertFailureObjectNotFound(response);
    }
}
