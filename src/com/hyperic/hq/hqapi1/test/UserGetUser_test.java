package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.HQApi;
import com.hyperic.hq.hqapi1.UserApi;
import com.hyperic.hq.hqapi1.jaxb.GetUserResponse;
import com.hyperic.hq.hqapi1.jaxb.ResponseStatus;

public class UserGetUser_test extends UserTestBase {

    public UserGetUser_test(String name) {
        super(name);
    }
                     
    public void testGetUserValid() throws Exception {
        UserApi api = getUserApi();
        GetUserResponse response = api.getUser("hqadmin");

        // Assert success response
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());

        // Assert First & Last Name
        assertEquals(response.getUser().getFirstName(), "HQ");
        assertEquals(response.getUser().getLastName(), "Administrator");
    }

    public void testGetUserInvalid() throws Exception {
        UserApi api = getUserApi();
        GetUserResponse response = api.getUser("unknownUser");

        // Assert Failure
        assertEquals(ResponseStatus.FAILURE, response.getStatus());
        assertEquals("ObjectNotFound", response.getError().getErrorCode());
    }
}
