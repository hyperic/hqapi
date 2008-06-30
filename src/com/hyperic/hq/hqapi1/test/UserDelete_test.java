package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.HQApi;
import com.hyperic.hq.hqapi1.UserApi;
import com.hyperic.hq.hqapi1.jaxb.User;
import com.hyperic.hq.hqapi1.jaxb.CreateUserResponse;
import com.hyperic.hq.hqapi1.jaxb.ResponseStatus;
import com.hyperic.hq.hqapi1.jaxb.DeleteUserResponse;
import com.hyperic.hq.hqapi1.jaxb.GetUserResponse;

public class UserDelete_test extends UserTestBase {

    public UserDelete_test(String name) {
        super(name);
    }

    public void testCreateDelete() throws Exception {

        UserApi api = getUserApi();

        User user = generateTestUser();

        // Create the user
        CreateUserResponse createResponse = api.createUser(user, PASSWORD);
        assertEquals(ResponseStatus.SUCCESS, createResponse.getStatus());

        // Assert the new user exists
        GetUserResponse getResponse = api.getUser(user.getName());
        assertEquals(ResponseStatus.SUCCESS, getResponse.getStatus());

        // Delete the same user
        DeleteUserResponse deleteResponse = api.deleteUser(user);
        assertEquals(ResponseStatus.SUCCESS, deleteResponse.getStatus());

        // Assert that user is no longer available
        GetUserResponse getResponse2 = api.getUser(user.getName());
        assertEquals(ResponseStatus.FAILURE, getResponse2.getStatus());
        assertEquals("ObjectNotFound", getResponse2.getError().getErrorCode());

        // Assert a second delete of this user results in failure
        DeleteUserResponse deleteResponse2 = api.deleteUser(user);
        assertEquals(ResponseStatus.FAILURE, deleteResponse2.getStatus());
        assertEquals("ObjectNotFound", deleteResponse2.getError().getErrorCode());
    }
}
