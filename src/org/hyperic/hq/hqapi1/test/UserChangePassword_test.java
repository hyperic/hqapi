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
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UsersResponse;

public class UserChangePassword_test extends UserTestBase {

    public UserChangePassword_test(String name) {
        super(name);
    }


    public void testChangePassword() throws Exception {

        UserApi api = getUserApi();

        // Create a new user.
        User u = createTestUsers(1).get(0);

        final String NEWPASS = "NEWPASSWORD";
        // Change that users password.
        StatusResponse response = api.changePassword(u, NEWPASS);
        hqAssertSuccess(response);

        // Log in as the new user and list the users.
        UserApi api2 = getUserApi(u.getName(), NEWPASS);
        UsersResponse getResponse = api2.getUsers();
        hqAssertSuccess(getResponse);
    }

    public void testChangePasswordEmpty() throws Exception {

        UserApi api = getUserApi();

        // Test changing a password to an empty string.
        User u = createTestUsers(1).get(0);
        StatusResponse response = api.changePassword(u, "");
        hqAssertFailureInvalidParameters(response);        
    }

    public void testChangePasswordNull() throws Exception {
        UserApi api = getUserApi();

        // Test changing a password to a null string.
        User u = createTestUsers(1).get(0);
        StatusResponse response = api.changePassword(u, null);
        hqAssertFailureInvalidParameters(response);
    }

    public void testChangePasswordNoPermission() throws Exception {

        User u = createTestUsers(1).get(0);
        UserApi api = getUserApi(u.getName(), PASSWORD);

        User admin = new User();
        admin.setId(1);
        admin.setName("hqadmin");

        StatusResponse response = api.changePassword(admin, "NEWPASS");
        hqAssertFailurePermissionDenied(response);
    }

    public void testChangePasswordInvalidUser() throws Exception {

        User nonexistant = new User();
        nonexistant.setId(Integer.MAX_VALUE);
        nonexistant.setName("non-existant");

        UserApi api = getUserApi();
        StatusResponse response = api.changePassword(nonexistant, PASSWORD);
        hqAssertFailureObjectNotFound(response);
    }
}
