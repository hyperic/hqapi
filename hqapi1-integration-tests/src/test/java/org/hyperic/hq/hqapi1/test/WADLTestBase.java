package org.hyperic.hq.hqapi1.test;

import junit.framework.TestCase;

import org.hyperic.hq.hqapi1.wadl.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

public abstract class WADLTestBase extends TestCase {

    private Properties getClientProperties() {
        Properties props = new Properties();

        String home = System.getProperty("user.home");
        File hq = new File(home, ".hq");
        File clientProperties = new File(hq, "client.properties");

        if (clientProperties.exists()) {
            FileInputStream fis = null;
            props = new Properties();
            try {
                fis = new FileInputStream(clientProperties);
                props.load(fis);
            } catch (IOException e) {
                return props;
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException ioe) {
                    // Ignore
                }
            }
        }
        return props;
    }
    
    public void setUp() throws Exception {
        Properties props = getClientProperties();

        final String username = props.getProperty("user", "hqadmin");
        final String password = props.getProperty("password", "hqadmin");

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
