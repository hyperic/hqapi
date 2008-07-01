package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.UserApi;
import com.hyperic.hq.hqapi1.types.GetUsersResponse;
import com.hyperic.hq.hqapi1.types.ResponseStatus;

public class InvalidLogin_test extends HQApiTestBase {

    public InvalidLogin_test(String name) {
        super(name);
    }

    public void testInvalidLogin() throws Exception {

        UserApi api = getApi("invalidUser", "invalidPassword").getUserApi();
        GetUsersResponse response = api.getUsers();

        hqAssertFailure(response.getStatus());
        hqAssertErrorLoginFailure(response.getError());
    }
}
