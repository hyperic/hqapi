package com.hyperic.hq.hqapi1.test;

import junit.framework.TestCase;

import com.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.jaxb.User;
import org.hyperic.hq.hqapi1.jaxb.GetUserResponse;
import org.hyperic.hq.hqapi1.jaxb.GetUsersResponse;
import org.hyperic.hq.hqapi1.jaxb.ResponseStatus;

import java.util.Iterator;

public class Users_test extends TestCase {

    private static final String  HOST        = "localhost";
    private static final int     PORT        = 7080;
    private static final boolean IS_SECURE   = false;
    private static final String  USER        = "hqadmin";
    private static final String  PASSWORD    = "hqadmin";
    private static final String  BADPASSWORD = "badpassword";

    public Users_test(String name) {
        super(name);
    }

    private HQApi getApiBadCredentials() {
        return new HQApi(HOST, PORT, IS_SECURE, USER, BADPASSWORD);
    }

    private HQApi getApi() {
        return new HQApi(HOST, PORT, IS_SECURE, USER, PASSWORD);
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

    public void testGetUsers() throws Exception {
        HQApi api = getApi();
        GetUsersResponse response = api.getUsers();

        // Assert success response
        assertEquals(response.getStatus(), ResponseStatus.SUCCESS);

        // Should never have 0 users
        assertNotNull(response);
        assertTrue(response.getUser().size() != 0);

        // Check each user in the list has a Name
        for (Iterator i = response.getUser().iterator(); i.hasNext(); ) {
            User u = (User)i.next();
            assertNotNull(u.getName());
        }
    }
}
