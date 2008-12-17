package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.UserResponse;

public class UserGetUser_test extends UserTestBase {

    public UserGetUser_test(String name) {
        super(name);
    }
                     
    public void testGetUserValid() throws Exception {
        UserApi api = getUserApi();
        UserResponse response = api.getUser("hqadmin");

        hqAssertSuccess(response);
        assertEquals("HQ", response.getUser().getFirstName());
        assertEquals("Administrator", response.getUser().getLastName());
    }

    public void testGetUserInvalid() throws Exception {
        UserApi api = getUserApi();
        UserResponse response = api.getUser("unknownUser");

        hqAssertFailureObjectNotFound(response);
    }

    public void testGetUserById() throws Exception {
        UserApi api = getUserApi();
        UserResponse response = api.getUser(1);

        hqAssertSuccess(response);
        assertEquals("HQ", response.getUser().getFirstName());
        assertEquals("Administrator", response.getUser().getLastName());
    }

    public void testGetUserByIdInvalid() throws Exception {
        UserApi api = getUserApi();
        UserResponse response = api.getUser(Integer.MAX_VALUE);

        hqAssertFailureObjectNotFound(response);
    }
}
