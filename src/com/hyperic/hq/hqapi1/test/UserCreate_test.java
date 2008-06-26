package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.jaxb.CreateUserResponse;
import org.hyperic.hq.hqapi1.jaxb.ResponseStatus;
import org.hyperic.hq.hqapi1.jaxb.User;

import java.util.Random;

public class UserCreate_test extends HQApiTestBase {

    static final String PASSWORD = "apitest";

    public UserCreate_test(String name) {
        super(name);
    }

    /**
     * Return a valid User object that's guaranteed to have a unique Name
     * @return A valid User object.
     */
    public User getTestUser() {

        Random r = new Random();

        User user = new User();
        user.setName("apitest" + r.nextInt());
        user.setFirstName("API");
        user.setLastName("Test");
        user.setEmailAddress("apitest@hyperic.com");
        user.setActive(true);
        return user;
    }

    public void testCreateValidParameters() throws Exception {
        HQApi api = getApi();

        User user = getTestUser();

        CreateUserResponse response = api.createUser(user, PASSWORD);

        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
    }

    public void testCreateDuplicate() throws Exception {
        HQApi api = getApi();

        User user = getTestUser();

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

        User user = getTestUser();
        user.setName(null);

        CreateUserResponse response = api.createUser(user, PASSWORD);

        assertEquals(ResponseStatus.FAILURE, response.getStatus());
    }

    public void testCreateEmptyFirstName() throws Exception {
        HQApi api = getApi();

        User user = getTestUser();
        user.setFirstName(null);

        CreateUserResponse response = api.createUser(user, PASSWORD);

        assertEquals(ResponseStatus.FAILURE, response.getStatus());
    }

    public void testCreateEmptyLastName() throws Exception {
        HQApi api = getApi();

        User user = getTestUser();
        user.setLastName(null);

        CreateUserResponse response = api.createUser(user, PASSWORD);

        assertEquals(ResponseStatus.FAILURE, response.getStatus());
    }

    public void testCreateEmptyEmailAddress() throws Exception {
        HQApi api = getApi();

        User user = getTestUser();
        user.setEmailAddress(null);

        CreateUserResponse response = api.createUser(user, PASSWORD);

        assertEquals(ResponseStatus.FAILURE, response.getStatus());
    }

    public void testCreateEmptyPassword() throws Exception {
        HQApi api = getApi();

        User user = getTestUser();

        CreateUserResponse response = api.createUser(user, null);

        assertEquals(ResponseStatus.FAILURE, response.getStatus());
    }
}
