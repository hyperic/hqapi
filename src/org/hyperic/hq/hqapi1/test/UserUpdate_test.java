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

public class UserUpdate_test extends UserTestBase {

    public UserUpdate_test(String name) {
        super(name);
    }

    public void testUpdate() throws Exception {

        UserApi api = getUserApi();

        User user = generateTestUser();

        UserResponse createResponse = api.createUser(user, PASSWORD);
        assertEquals(ResponseStatus.SUCCESS, createResponse.getStatus());

        String FIRST   = "Updated FirstName";
        String LAST    = "Updated LastName";
        String EMAIL   = "Updated EmailAddress";
        String DEPT    = "Updated Department";
        String SMS     = "Updated SMS";
        String PHONE   = "Updated Phone";
        boolean ACTIVE = !user.isActive();
        boolean HTML   = !user.isHtmlEmail();
        String HASHED_PWD = "UpdatedPassword";
        
        user.setFirstName(FIRST);
        user.setLastName(LAST);
        user.setEmailAddress(EMAIL);
        user.setDepartment(DEPT);
        user.setSMSAddress(SMS);
        user.setPhoneNumber(PHONE);
        user.setActive(ACTIVE);
        user.setHtmlEmail(HTML);
        user.setPasswordHash(HASHED_PWD);
        StatusResponse updateResponse = api.updateUser(user);
        // Assert update success
        hqAssertSuccess(updateResponse);

        // Test the name has been updated
        UserResponse getResponse = api.getUser(user.getName());
        hqAssertSuccess(getResponse);
        User u = getResponse.getUser();
        assertEquals(FIRST,  u.getFirstName());
        assertEquals(LAST,   u.getLastName());
        assertEquals(EMAIL,  u.getEmailAddress());
        assertEquals(DEPT,   u.getDepartment());
        assertEquals(SMS,    u.getSMSAddress());
        assertEquals(PHONE,  u.getPhoneNumber());
        assertEquals(ACTIVE, u.isActive());
        assertEquals(HTML,   u.isHtmlEmail());
        assertEquals(HASHED_PWD, u.getPasswordHash());
    }

    public void testUpdateNoPermission() throws Exception {

        UserApi api = getUserApi();

        User user = generateTestUser();

        UserResponse createResponse = api.createUser(user, PASSWORD);
        hqAssertSuccess(createResponse);

        // Reconnect as the new user

        UserApi apiNewUser = getUserApi(user.getName(), PASSWORD);

        User u = new User();
        u.setName("hqadmin");
        u.setFirstName("Updated FirstName");

        StatusResponse updateResponse = apiNewUser.updateUser(u);
        hqAssertFailurePermissionDenied(updateResponse);
    }
}
