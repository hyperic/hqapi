package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.UserApi;
import com.hyperic.hq.hqapi1.types.User;
import com.hyperic.hq.hqapi1.types.GetUsersResponse;
import com.hyperic.hq.hqapi1.types.ResponseStatus;

import java.util.Iterator;

public class UserGetUsers_test extends UserTestBase {

    public UserGetUsers_test(String name) {
        super(name);
    }

    public void testGetUsers() throws Exception {
        UserApi api = getUserApi();
        GetUsersResponse response = api.getUsers();

        // Assert success response
        hqAssertSuccess(response.getStatus());

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
