package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.GetUsersResponse;

public class InvalidLogin_test extends HQApiTestBase {

    public InvalidLogin_test(String name) {
        super(name);
    }

    public void testInvalidLogin() throws Exception {

        UserApi api = getApi("invalidUser", "invalidPassword").getUserApi();
        GetUsersResponse response = api.getUsers();

        hqAssertFailureLoginFailure(response);
    }
}
