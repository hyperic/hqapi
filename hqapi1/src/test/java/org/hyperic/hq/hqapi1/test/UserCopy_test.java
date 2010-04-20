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
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;
import org.hyperic.hq.hqapi1.types.UsersRequest;
import org.hyperic.hq.hqapi1.types.UsersResponse;

import java.util.List;

public class UserCopy_test extends UserTestBase {

    public UserCopy_test(String name) {
        super(name);
    }
  
    /**
     * This test will create a user then deletes the same user and re-create the user 
     * using the same User object that had created initially and test the user login. 
     */
    public void testCopy() throws Exception {

        UserApi api = getUserApi();

        User user = generateTestUser();
        
        //create the new user     
        UserResponse createResponse = api.createUser(user, TESTUSER_PASSWORD);
        assertEquals(ResponseStatus.SUCCESS, createResponse.getStatus());
        
        // Assert the new user exists
        UserResponse getResponse = api.getUser(user.getName());
        hqAssertSuccess(getResponse);
        
        // store the user that was just created before deleting 
        User createdUser = getResponse.getUser();

        // Delete the user that was just created.
        StatusResponse deleteResponse = api.deleteUser(createdUser.getId());
        hqAssertSuccess(deleteResponse);

        // Assert that user is no longer available
        UserResponse getResponse2 = api.getUser(createdUser.getId());
        hqAssertFailureObjectNotFound(getResponse2);

        // create the user using the existing user object
        UsersRequest syncRequest = new UsersRequest();
        List<User> users = syncRequest.getUser();
        users.add(createdUser);
        StatusResponse syncResponse = api.syncUsers(users);
        assertEquals(ResponseStatus.SUCCESS, syncResponse.getStatus());
        
        // test login
        UserApi api2 = getUserApi(user.getName(), TESTUSER_PASSWORD);
        UsersResponse getResponse3 = api2.getUsers();
        hqAssertSuccess(getResponse3);        
    }
}
