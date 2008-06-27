package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.jaxb.User;
import org.hyperic.hq.hqapi1.jaxb.CreateUserResponse;
import org.hyperic.hq.hqapi1.jaxb.ResponseStatus;
import org.hyperic.hq.hqapi1.jaxb.SyncUserResponse;

public class UserSync_test extends UserCreate_test {

    public UserSync_test(String name) {
        super(name);
    }

    public void testSyncFirstName() throws Exception {

        HQApi api = getApi();

        User user = getTestUser();

        CreateUserResponse createResponse = api.createUser(user, PASSWORD);
        assertEquals(ResponseStatus.SUCCESS, createResponse.getStatus());

        user.setFirstName("Updated First Name");
        SyncUserResponse syncResponse = api.syncUser(user);
        assertEquals(ResponseStatus.SUCCESS, syncResponse.getStatus());
    }
}
