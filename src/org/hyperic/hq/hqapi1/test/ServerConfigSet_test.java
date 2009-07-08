package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ServerConfigApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.ServerConfigsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;
import org.hyperic.hq.hqapi1.types.ServerConfig;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class ServerConfigSet_test extends HQApiTestBase {

    public ServerConfigSet_test(String name) {
        super(name);
    }

    public void testSetAllConfig() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        ServerConfigsResponse configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);

        StatusResponse response = sApi.setConfig(configResponse.getServerConfig());
        hqAssertSuccess(response);
    }

    public void testSetSingleConfig() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        ServerConfigsResponse configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);

        List<ServerConfig> configs = configResponse.getServerConfig();
        for (ServerConfig config : configs) {
            if (config.getKey().equals("HQ_ALERTS_ENABLED")) {
                config.setValue("false");
            }
        }

        StatusResponse response = sApi.setConfig(configs);
        hqAssertSuccess(response);

        // Validate update of HQ_ALERTS_ENABLED
        configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);

        configs = configResponse.getServerConfig();
        for (ServerConfig config : configs) {
            if (config.getKey().equals("HQ_ALERTS_ENABLED")) {
                assertTrue("HQ_ALERTS_ENABLED was not false",
                           config.getValue().equals("false"));
                config.setValue("true"); // Re-enable
            }
        }

        response = sApi.setConfig(configs);
        hqAssertSuccess(response);
    }

    public void testSetConfigEmpty() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        List<ServerConfig> configs = new ArrayList<ServerConfig>();

        StatusResponse response = sApi.setConfig(configs);
        // All configs required
        hqAssertFailureInvalidParameters(response);
    }

    public void testSetConfigInvalidUser() throws Exception {

        HQApi api = getApi();

        Random r = new Random();

        User user = new User();
        user.setName("test" + r.nextInt());
        user.setFirstName("Get Server Config");
        user.setLastName("Test User");
        user.setEmailAddress("testuser@springsource.com");
        user.setActive(true);

        UserResponse userCreateResponse =
                api.getUserApi().createUser(user, "test"); // Create test user w/o Admin
        hqAssertSuccess(userCreateResponse);

        ServerConfigsResponse response = api.getServerConfigApi().getConfig();
        hqAssertSuccess(response);

        // Re-sync with invalid user
        ServerConfigApi sApi = getApi(user.getName(), "test").getServerConfigApi();
        StatusResponse putResponse = sApi.setConfig(response.getServerConfig());
        hqAssertFailurePermissionDenied(putResponse);

        // Cleanup
        StatusResponse deleteResponse =
                api.getUserApi().deleteUser(userCreateResponse.getUser().getId());
        hqAssertSuccess(deleteResponse);
    }
}
