package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.CreateUserResponse;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.User;

public class UserCreate_test extends UserTestBase {

    public UserCreate_test(String name) {
        super(name);
    }

    public void testCreateValidParameters() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();

        CreateUserResponse response = api.createUser(user, PASSWORD);

        hqAssertSuccess(response);
    }

    public void testCreateDuplicate() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();

        CreateUserResponse response = api.createUser(user, PASSWORD);
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());

        // Attempt to create the same user again
        CreateUserResponse response2 = api.createUser(user, PASSWORD);
        hqAssertFailureObjectExists(response2);
    }

    public void testCreateEmptyUser() throws Exception {
        UserApi api = getUserApi();

        User user = new User();
        CreateUserResponse response = api.createUser(user, PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyName() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();
        user.setName(null);

        CreateUserResponse response = api.createUser(user, PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyFirstName() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();
        user.setFirstName(null);

        CreateUserResponse response = api.createUser(user, PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyLastName() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();
        user.setLastName(null);

        CreateUserResponse response = api.createUser(user, PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyEmailAddress() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();
        user.setEmailAddress(null);

        CreateUserResponse response = api.createUser(user, PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyPassword() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();

        CreateUserResponse response = api.createUser(user, null);

        hqAssertFailureInvalidParameters(response);
    }
}
