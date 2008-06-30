package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.HQApi;
import com.hyperic.hq.hqapi1.UserApi;
import com.hyperic.hq.hqapi1.jaxb.User;
import com.hyperic.hq.hqapi1.jaxb.GetUsersResponse;
import com.hyperic.hq.hqapi1.jaxb.ResponseStatus;

import java.util.Iterator;

public class UserGetUsers_test extends UserTestBase {

    public UserGetUsers_test(String name) {
        super(name);
    }

    public void testGetUsers() throws Exception {
        UserApi api = getUserApi();
        GetUsersResponse response = api.getUsers();

        // Assert success response
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());

        // Should never have 0 users
        assertNotNull(response);
        assertTrue(response.getUser().size() != 0);

        // Check each user in the list has a Name
        for (Iterator i = response.getUser().iterator(); i.hasNext(); ) {
            User u = (User)i.next();
            assertNotNull(u.getName());
        }
    }
}
