package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.jaxb.CreateUserResponse;
import org.hyperic.hq.hqapi1.jaxb.ResponseStatus;
import org.hyperic.hq.hqapi1.jaxb.User;

public class UserCreate_test extends HQApiTestBase {

    public UserCreate_test(String name) {
        super(name);
    }

    public void testCreateValidParameters() throws Exception {
        HQApi api = getApi();

        User user = new User();
        user.setName("rmorgan");
        user.setFirstName("Ryan");
        user.setLastName("Morgan");
        user.setEmailAddress("rmorgan@hyperic.com");

        CreateUserResponse response = api.createUser(user, "asdfasdf");

        assertEquals(response.getStatus(), ResponseStatus.SUCCESS);
    }

    public void testCreateEmptyUser() throws Exception {
        HQApi api = getApi();

        User user = new User();
        CreateUserResponse response = api.createUser(user, null);

        assertEquals(response.getStatus(), ResponseStatus.FAILURE);
    }

    public void testCreateEmptyName() throws Exception {
        HQApi api = getApi();

        User user = new User();
        // user.setName("rmorgan");
        user.setFirstName("Ryan");
        user.setLastName("Morgan");
        user.setEmailAddress("rmorgan@hyperic.com");

        CreateUserResponse response = api.createUser(user, "asdfasdf");

        assertEquals(response.getStatus(), ResponseStatus.FAILURE);
    }

    public void testCreateEmptyFirstName() throws Exception {
        HQApi api = getApi();

        User user = new User();
        user.setName("rmorgan");
        // user.setFirstName("Ryan");
        user.setLastName("Morgan");
        user.setEmailAddress("rmorgan@hyperic.com");

        CreateUserResponse response = api.createUser(user, "asdfasdf");

        assertEquals(response.getStatus(), ResponseStatus.FAILURE);
    }

    public void testCreateEmptyLastName() throws Exception {
        HQApi api = getApi();

        User user = new User();
        user.setName("rmorgan");
        user.setFirstName("Ryan");
        // user.setLastName("Morgan");
        user.setEmailAddress("rmorgan@hyperic.com");

        CreateUserResponse response = api.createUser(user, "asdfasdf");

        assertEquals(response.getStatus(), ResponseStatus.FAILURE);
    }

    public void testCreateEmptyEmailAddress() throws Exception {
        HQApi api = getApi();

        User user = new User();
        user.setName("rmorgan");
        user.setFirstName("Ryan");
        user.setLastName("Morgan");
        // user.setEmailAddress("rmorgan@hyperic.com");

        CreateUserResponse response = api.createUser(user, "asdfasdf");

        assertEquals(response.getStatus(), ResponseStatus.FAILURE);
    }

    public void testCreateEmptyPassword() throws Exception {
        HQApi api = getApi();

        User user = new User();
        user.setName("rmorgan");
        user.setFirstName("Ryan");
        user.setLastName("Morgan");
        user.setEmailAddress("rmorgan@hyperic.com");

        CreateUserResponse response = api.createUser(user, null);

        assertEquals(response.getStatus(), ResponseStatus.FAILURE);
    }
}
