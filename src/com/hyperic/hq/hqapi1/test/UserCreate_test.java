package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.jaxb.CreateUserResponse;
import org.hyperic.hq.hqapi1.jaxb.ResponseStatus;
import org.hyperic.hq.hqapi1.jaxb.User;

public class UserCreate_test extends UserTestBase {

    public UserCreate_test(String name) {
        super(name);
    }

    public void testCreateValidParameters() throws Exception {
        HQApi api = getApi();

        User user = generateTestUser();

        CreateUserResponse response = api.createUser(user, PASSWORD);

        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
    }

    public void testCreateDuplicate() throws Exception {
        HQApi api = getApi();

        User user = generateTestUser();

        CreateUserResponse response = api.createUser(user, PASSWORD);
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());

        // Attempt to create the same user again
        CreateUserResponse response2 = api.createUser(user, PASSWORD);
        assertEquals(ResponseStatus.FAILURE, response2.getStatus());
    }

    public void testCreateEmptyUser() throws Exception {
        HQApi api = getApi();

        User user = new User();
        CreateUserResponse response = api.createUser(user, PASSWORD);

        assertEquals(ResponseStatus.FAILURE, response.getStatus());
    }

    public void testCreateEmptyName() throws Exception {
        HQApi api = getApi();

        User user = generateTestUser();
        user.setName(null);

        CreateUserResponse response = api.createUser(user, PASSWORD);

        assertEquals(ResponseStatus.FAILURE, response.getStatus());
    }

    public void testCreateEmptyFirstName() throws Exception {
        HQApi api = getApi();

        User user = generateTestUser();
        user.setFirstName(null);

        CreateUserResponse response = api.createUser(user, PASSWORD);

        assertEquals(ResponseStatus.FAILURE, response.getStatus());
    }

    public void testCreateEmptyLastName() throws Exception {
        HQApi api = getApi();

        User user = generateTestUser();
        user.setLastName(null);

        CreateUserResponse response = api.createUser(user, PASSWORD);

        assertEquals(ResponseStatus.FAILURE, response.getStatus());
    }

    public void testCreateEmptyEmailAddress() throws Exception {
        HQApi api = getApi();

        User user = generateTestUser();
        user.setEmailAddress(null);

        CreateUserResponse response = api.createUser(user, PASSWORD);

        assertEquals(ResponseStatus.FAILURE, response.getStatus());
    }

    public void testCreateEmptyPassword() throws Exception {
        HQApi api = getApi();

        User user = generateTestUser();

        CreateUserResponse response = api.createUser(user, null);

        assertEquals(ResponseStatus.FAILURE, response.getStatus());
    }
}
