package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.CreateUserResponse;
import org.hyperic.hq.hqapi1.types.DeleteUserResponse;
import org.hyperic.hq.hqapi1.types.GetUserResponse;

public class UserDelete_test extends UserTestBase {

    public UserDelete_test(String name) {
        super(name);
    }

    public void testCreateDelete() throws Exception {

        UserApi api = getUserApi();

        User user = generateTestUser();

        // Create the user
        CreateUserResponse createResponse = api.createUser(user, PASSWORD);
        hqAssertSuccess(createResponse);

        // Assert the new user exists
        GetUserResponse getResponse = api.getUser(user.getName());
        hqAssertSuccess(getResponse);

        // Delete the user that was just created.
        DeleteUserResponse deleteResponse =
            api.deleteUser(getResponse.getUser().getId());
        hqAssertSuccess(deleteResponse);

        // Assert that user is no longer available
        GetUserResponse getResponse2 = api.getUser(user.getName());
        hqAssertFailureObjectNotFound(getResponse2);

        // Assert a second delete of this user results in failure
        DeleteUserResponse deleteResponse2 =
            api.deleteUser(getResponse.getUser().getId());
        hqAssertFailureObjectNotFound(deleteResponse2);
    }
}
