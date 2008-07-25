package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.CreateUserResponse;
import org.hyperic.hq.hqapi1.types.ChangePasswordResponse;
import org.hyperic.hq.hqapi1.types.GetUsersResponse;

public class UserChangePassword_test extends UserTestBase {

    public UserChangePassword_test(String name) {
        super(name);
    }


    public void testChangePassword() throws Exception {

        UserApi api = getUserApi();

        // Create a new user.
        User u = generateTestUser();
        CreateUserResponse createResponse = api.createUser(u, PASSWORD);
        hqAssertSuccess(createResponse);

        final String NEWPASS = "NEWPASSWORD";
        // Change that users password.
        User createdUser = createResponse.getUser();
        ChangePasswordResponse response = api.changePassword(createdUser,
                                                             NEWPASS);
        hqAssertSuccess(response);

        // Log in as the new user and list the users.
        UserApi api2 = getUserApi(createdUser.getName(), NEWPASS);
        GetUsersResponse getResponse = api2.getUsers();
        hqAssertSuccess(getResponse);
    }

    public void testChangePasswordEmpty() throws Exception {

        UserApi api = getUserApi();

        // Create a new user.
        User u = generateTestUser();
        CreateUserResponse createResponse = api.createUser(u, PASSWORD);
        hqAssertSuccess(createResponse);

        // Change that users password.
        User createdUser = createResponse.getUser();
        ChangePasswordResponse response = api.changePassword(createdUser,
                                                             "");
        hqAssertFailureInvalidParameters(response);        
    }

    public void testChangePasswordNull() throws Exception {
        UserApi api = getUserApi();

        // Create a new user.
        User u = generateTestUser();
        CreateUserResponse createResponse = api.createUser(u, PASSWORD);
        hqAssertSuccess(createResponse);

        // Change that users password.
        User createdUser = createResponse.getUser();
        ChangePasswordResponse response = api.changePassword(createdUser,
                                                             null);
        hqAssertFailureInvalidParameters(response);
    }
}
