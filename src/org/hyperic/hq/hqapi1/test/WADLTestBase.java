package org.hyperic.hq.hqapi1.test;

import junit.framework.TestCase;

import org.hyperic.hq.hqapi1.wadl.*;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class WADLTestBase extends TestCase {

    public void setUp() throws Exception {
        final String username ="hqadmin";
        final String password ="hqadmin";

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication (username, password.toCharArray());
            }
        });
    }

    // Assert SUCCESS

    void hqAssertSuccess(Response response) {
        String error = (response.getError() != null) ?
            response.getError().getReasonText() : "";
        assertEquals(error, ResponseStatus.SUCCESS, response.getStatus());
    }
    
    // Assert FAILURE
    void hqAssertFailure(Response response) {
        String error = (response.getError() != null) ?
            response.getError().getReasonText() : "";
        assertEquals(error, ResponseStatus.FAILURE, response.getStatus());
    }
}
