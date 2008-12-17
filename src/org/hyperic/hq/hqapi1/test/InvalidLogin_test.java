package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.UsersResponse;

public class InvalidLogin_test extends HQApiTestBase {

    public InvalidLogin_test(String name) {
        super(name);
    }

    public void testInvalidLogin() throws Exception {

        UserApi api = getApi("invalidUser", "invalidPassword").getUserApi();
        UsersResponse response = api.getUsers();

        hqAssertFailureLoginFailure(response);
    }

    public void testNullUsername() throws Exception {

        UserApi api = getApi(null, "invalidPassword").getUserApi();
        UsersResponse response = api.getUsers();

        hqAssertFailureLoginFailure(response);
    }

    public void testNullPassword() throws Exception {

        UserApi api = getApi("invalidUser", null).getUserApi();
        UsersResponse response = api.getUsers();

        hqAssertFailureLoginFailure(response);
    }

    public void testEmptyUsername() throws Exception {

        UserApi api = getApi("", "invalidPassword").getUserApi();
        UsersResponse response = api.getUsers();

        hqAssertFailureLoginFailure(response);
    }

    public void testEmptyPassword() throws Exception {

        UserApi api = getApi("hqadmin", "").getUserApi();
        UsersResponse response = api.getUsers();

        hqAssertFailureLoginFailure(response);
    }
}
