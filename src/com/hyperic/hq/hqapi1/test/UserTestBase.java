package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.types.User;
import com.hyperic.hq.hqapi1.UserApi;

import java.util.Random;

public class UserTestBase extends HQApiTestBase {

    static final String PASSWORD = "apitest";
        
    public UserTestBase(String name) {
        super(name);
    }

    UserApi getUserApi() {
        return getApi().getUserApi();
    }

    UserApi getUserApi(String user, String password) {
        return getApi(user, password).getUserApi();
    }

    /**
     * Generate a valid User object that's guaranteed to have a unique Name
     * @return A valid User object.
     */
    public User generateTestUser() {

        Random r = new Random();

        User user = new User();
        user.setName("apitest" + r.nextInt());
        user.setFirstName("API");
        user.setLastName("Test");
        user.setEmailAddress("apitest@hyperic.com");
        user.setActive(true);
        return user;
    }
}
