package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ServerConfigApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.ServerConfigResponse;
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

        ServerConfigResponse configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);

        StatusResponse response = sApi.setConfig(configResponse.getServerConfig());
        hqAssertSuccess(response);
    }

    public void testSetSingleConfig() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        ServerConfigResponse configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);

        String configName = "HQ_ALERTS_ENABLED";
        ServerConfig c = new ServerConfig();
        c.setKey(configName);
        c.setValue("false");

        StatusResponse response = sApi.setConfig(c);
        hqAssertSuccess(response);

        // Validate update of HQ_ALERTS_ENABLED
        configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);

        List<ServerConfig> configs = configResponse.getServerConfig();
        for (ServerConfig config : configs) {
            if (config.getKey().equals(configName)) {
                assertTrue(configName + " was not false",
                           config.getValue().equals("false"));
                config.setValue("true"); // Re-enable
            }
        }

        response = sApi.setConfig(configs);
        hqAssertSuccess(response);
        assertTrue(configName + " was not true",
                   getConfigValue(configName).equals("true"));
    }

    public void testSetUnknownConfig() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        ServerConfig c = new ServerConfig();
        c.setKey("HQ_UNKNOWN_CONFIG");
        c.setValue("false");

        StatusResponse response = sApi.setConfig(c);
        hqAssertFailureInvalidParameters(response);
    }

    public void testSetRestrictedConfig() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        ServerConfig c = new ServerConfig();
        c.setKey("CAM_GUIDE_ENABLED");
        c.setValue("false");

        StatusResponse response = sApi.setConfig(c);
        hqAssertFailureOperationDenied(response);
    }

    public void testSetConfigAtMin() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        String configName = "CAM_DATA_PURGE_RAW";
        String serverValue = getConfigValue(configName);
        
        long minValue = 24 * 60 * 60 * 1000L;
        ServerConfig c = new ServerConfig();
        c.setKey(configName);
        c.setValue(Long.toString(minValue));

        StatusResponse response = sApi.setConfig(c);
        hqAssertSuccess(response);
        assertTrue(configName + " was not " + minValue,
                    getConfigValue(configName).equals(Long.toString(minValue)));
        
        // reset to previous value
        c.setValue(serverValue);
        response = sApi.setConfig(c);
        hqAssertSuccess(response);
        assertTrue(configName + " was not " + serverValue,
                    getConfigValue(configName).equals(serverValue));
        
    }
    
    public void testSetConfigBelowMin() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        ServerConfig c = new ServerConfig();
        c.setKey("CAM_DATA_PURGE_RAW");
        c.setValue("1");

        StatusResponse response = sApi.setConfig(c);
        hqAssertFailureInvalidParameters(response);
    }

    public void testSetConfigAtMax() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        String configName = "ALERT_PURGE";
        String serverValue = getConfigValue(configName);
        
        long maxValue = 9999 * 60 * 60 * 1000L;
        ServerConfig c = new ServerConfig();
        c.setKey(configName);
        c.setValue(Long.toString(maxValue));

        StatusResponse response = sApi.setConfig(c);
        hqAssertSuccess(response);
        assertTrue(configName + " was not " + maxValue,
                    getConfigValue(configName).equals(Long.toString(maxValue)));
        
        // reset to previous value
        c.setValue(serverValue);
        response = sApi.setConfig(c);
        hqAssertSuccess(response);
        assertTrue(configName + " was not " + serverValue,
                    getConfigValue(configName).equals(serverValue));
        
    }
    
    public void testSetConfigAboveMax() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        long maxValue = 60 * 60 * 1000L * 99999;
        ServerConfig c = new ServerConfig();
        c.setKey("ALERT_PURGE");
        c.setValue(Long.toString(maxValue));

        StatusResponse response = sApi.setConfig(c);
        hqAssertFailureInvalidParameters(response);
    }

    public void testSetConfigValidMultiple() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        String configName = "EVENT_LOG_PURGE";
        String serverValue = getConfigValue(configName);
        
        long multipleValue = 50 * 60 * 60 * 1000L;
        ServerConfig c = new ServerConfig();
        c.setKey(configName);
        c.setValue(Long.toString(multipleValue));

        StatusResponse response = sApi.setConfig(c);
        hqAssertSuccess(response);
        assertTrue(configName + " was not " + multipleValue,
                    getConfigValue(configName).equals(Long.toString(multipleValue)));
        
        // reset to previous value
        c.setValue(serverValue);
        response = sApi.setConfig(c);
        hqAssertSuccess(response);
        assertTrue(configName + " was not " + serverValue,
                    getConfigValue(configName).equals(serverValue));
        
    }
    
    public void testSetConfigInvalidMultiple() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        ServerConfigResponse configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);

        long value = (2 * 60 * 60 * 1000L) + 1;
        ServerConfig c = new ServerConfig();
        c.setKey("CAM_DATA_MAINTENANCE");
        c.setValue(Long.toString(value));

        StatusResponse response = sApi.setConfig(c);
        hqAssertFailureInvalidParameters(response);
    }

    public void testSetConfigInvalidNumber() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        ServerConfigResponse configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);

        String value = "invalid";
        ServerConfig c = new ServerConfig();
        c.setKey("EVENT_LOG_PURGE");
        c.setValue(value);

        StatusResponse response = sApi.setConfig(c);
        hqAssertFailureInvalidParameters(response);
    }

    public void testSetConfigEmptyString() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        ServerConfigResponse configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);

        ServerConfig c = new ServerConfig();
        c.setKey("CAM_DATA_MAINTENANCE");
        c.setValue("");

        StatusResponse response = sApi.setConfig(c);
        hqAssertFailureInvalidParameters(response);
    }
    
    public void testSetConfigEmpty() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        List<ServerConfig> configs = new ArrayList<ServerConfig>();

        StatusResponse response = sApi.setConfig(configs);
        // At least one config parameter required
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

        ServerConfigResponse response = api.getServerConfigApi().getConfig();
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
    
    /**
     * TODO: The api should directly support getting config by name
     */
    private String getConfigValue(String configName) throws Exception {
        ServerConfigApi sApi = getApi().getServerConfigApi();

        ServerConfigResponse configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);

        String configValue = null;
        List<ServerConfig> configs = configResponse.getServerConfig();
        for (ServerConfig config : configs) {
            if (config.getKey().equals(configName)) {
                configValue = config.getValue();
                break;
            }
        }
        
        return configValue;
    }
}
