package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.jaxb.CreateUserResponse;
import org.hyperic.hq.hqapi1.jaxb.ResponseStatus;

public class UserCreate_test extends HQApiTestBase {

    public UserCreate_test(String name) {
        super(name);
    }

    public void testCreateValidParameters() throws Exception {
        HQApi api = getApi();

        CreateUserResponse response =
            api.createUser("rmorgan", "Ryan", "Morgan", "asdfasdf",
                           "rmorgan@hyperic.com");

        assertEquals(response.getStatus(), ResponseStatus.SUCCESS);
    }

    public void testCreateNoName() throws Exception {
        HQApi api = getApi();

        CreateUserResponse response =
            api.createUser(null, "Ryan", "Morgan", "asdfasdf",
                           "rmorgan@hyperic.com");

        assertEquals(response.getStatus(), ResponseStatus.FAILURE);
    }
}
