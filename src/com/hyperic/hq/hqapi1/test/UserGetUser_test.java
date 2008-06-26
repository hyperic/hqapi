package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.jaxb.GetUserResponse;
import org.hyperic.hq.hqapi1.jaxb.ResponseStatus;

public class UserGetUser_test extends HQApiTestBase {

    public UserGetUser_test(String name) {
        super(name);
    }
                     
    public void testGetUserValid() throws Exception {
        HQApi api = getApi();
        GetUserResponse response = api.getUser("hqadmin");

        // Assert success response
        assertEquals(response.getStatus(), ResponseStatus.SUCCESS);

        // Assert First & Last Name
        assertEquals(response.getUser().getFirstName(), "HQ");
        assertEquals(response.getUser().getLastName(), "Administrator");
    }

    public void testGetUserInvalid() throws Exception {
        HQApi api = getApi();
        GetUserResponse response = api.getUser("unknownUser");

        // Assert Failure
        assertEquals(response.getStatus(), ResponseStatus.FAILURE);
    }
}
