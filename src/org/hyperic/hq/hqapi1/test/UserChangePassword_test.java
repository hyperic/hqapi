package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.CreateUserResponse;
import org.hyperic.hq.hqapi1.types.ChangePasswordResponse;

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

        // Change that users password.
        User createdUser = createResponse.getUser();
        String PASS = "NEWPASSWORD";
        ChangePasswordResponse response = api.changePassword(createdUser,
                                                             PASS);
        hqAssertSuccess(response);
    }
}
