package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ServerConfigApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.ServerConfigResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.UserResponse;

import java.util.Random;

public class ServerConfigGet_test extends HQApiTestBase {

    public ServerConfigGet_test(String name) {
        super(name);
    }

    public void testGetConfig() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        ServerConfigResponse response = sApi.getConfig();
        hqAssertSuccess(response);
        assertTrue("No server configuration settings found",
                   response.getServerConfig().size() > 0);
    }

    public void testGetConfigInvalidUser() throws Exception {

        HQApi api = getApi();
        UserApi uApi = api.getUserApi();

        Random r = new Random();

        User user = new User();
        user.setName("test" + r.nextInt());
        user.setFirstName("Get Server Config");
        user.setLastName("Test User");
        user.setEmailAddress("testuser@springsource.com");
        user.setActive(true);

        UserResponse userCreateResponse =
                uApi.createUser(user, "test"); // Create test user w/o Admin
        hqAssertSuccess(userCreateResponse);

        ServerConfigApi sApi = getApi(user.getName(), "test").getServerConfigApi();

        ServerConfigResponse response = sApi.getConfig();
        hqAssertFailurePermissionDenied(response);

        // Cleanup
        StatusResponse deleteResponse =
                uApi.deleteUser(userCreateResponse.getUser().getId());
        hqAssertSuccess(deleteResponse);
    }
}
