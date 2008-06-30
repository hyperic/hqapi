package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.UserApi;
import com.hyperic.hq.hqapi1.jaxb.GetUsersResponse;
import com.hyperic.hq.hqapi1.jaxb.ResponseStatus;

public class InvalidLogin_test extends HQApiTestBase {

    public InvalidLogin_test(String name) {
        super(name);
    }

    public void testInvalidLogin() throws Exception {

        UserApi api = getApi("invalidUser", "invalidPassword").getUserApi();
        GetUsersResponse response = api.getUsers();

        assertEquals(ResponseStatus.FAILURE, response.getStatus());
        assertEquals("LoginFailure", response.getError().getErrorCode());
    }
}
