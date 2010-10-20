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
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;

public class UserCreate_test extends UserTestBase {

    public UserCreate_test(String name) {
        super(name);
    }

    public void testCreateValidParameters() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();

        UserResponse response = api.createUser(user, TESTUSER_PASSWORD);

        hqAssertSuccess(response);

        User createdUser = response.getUser();
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getFirstName(), createdUser.getFirstName());
        assertEquals(user.getLastName(), createdUser.getLastName());
    }

    public void testCreateWithHtmlEmail() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();
        user.setHtmlEmail(true);  // test for setting to true
        
        UserResponse response = api.createUser(user, TESTUSER_PASSWORD);

        hqAssertSuccess(response);

        User createdUser = response.getUser();
        assertEquals(user.isHtmlEmail(), createdUser.isHtmlEmail());
        
        user = generateTestUser();
        user.setHtmlEmail(false);  // test for setting to false
        
        response = api.createUser(user, TESTUSER_PASSWORD);

        hqAssertSuccess(response);

        createdUser = response.getUser();
        assertEquals(user.isHtmlEmail(), createdUser.isHtmlEmail());
        
    }
    
    public void testCreateDuplicate() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();

        UserResponse response = api.createUser(user, TESTUSER_PASSWORD);
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());

        // Attempt to create the same user again
        UserResponse response2 = api.createUser(user, TESTUSER_PASSWORD);
        hqAssertFailureObjectExists(response2);
    }

    public void testCreateEmptyUser() throws Exception {
        UserApi api = getUserApi();

        User user = new User();
        UserResponse response = api.createUser(user, TESTUSER_PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyName() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();
        user.setName(null);

        UserResponse response = api.createUser(user, TESTUSER_PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyFirstName() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();
        user.setFirstName(null);

        UserResponse response = api.createUser(user, TESTUSER_PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyLastName() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();
        user.setLastName(null);

        UserResponse response = api.createUser(user, TESTUSER_PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyEmailAddress() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();
        user.setEmailAddress(null);

        UserResponse response = api.createUser(user, TESTUSER_PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyPassword() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();

        UserResponse response = api.createUser(user, null);

        hqAssertFailureInvalidParameters(response);
    }
    
    public void testCreateValidNoPermission() throws Exception {
    	UserApi api = getUserApi();

        User user = generateTestUser();

        UserResponse response = api.createUser(user, TESTUSER_PASSWORD);

        hqAssertSuccess(response);
        
        UserApi api1 = getUserApi(user.getName(), TESTUSER_PASSWORD);
        
        User user1 = generateTestUser();
        
        UserResponse response1 = api1.createUser(user1, TESTUSER_PASSWORD);
        
        hqAssertFailurePermissionDenied(response1);
    }
}
