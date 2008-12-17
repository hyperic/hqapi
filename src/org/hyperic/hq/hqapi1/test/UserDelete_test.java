package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class UserDelete_test extends UserTestBase {

    public UserDelete_test(String name) {
        super(name);
    }

    public void testCreateDelete() throws Exception {

        UserApi api = getUserApi();

        User user = generateTestUser();

        // Create the user
        UserResponse createResponse = api.createUser(user, PASSWORD);
        hqAssertSuccess(createResponse);

        // Assert the new user exists
        UserResponse getResponse = api.getUser(user.getName());
        hqAssertSuccess(getResponse);

        // Delete the user that was just created.
        StatusResponse deleteResponse =
            api.deleteUser(getResponse.getUser().getId());
        hqAssertSuccess(deleteResponse);

        // Assert that user is no longer available
        UserResponse getResponse2 = api.getUser(user.getName());
        hqAssertFailureObjectNotFound(getResponse2);

        // Assert a second delete of this user results in failure
        StatusResponse deleteResponse2 =
            api.deleteUser(getResponse.getUser().getId());
        hqAssertFailureObjectNotFound(deleteResponse2);
    }
}
