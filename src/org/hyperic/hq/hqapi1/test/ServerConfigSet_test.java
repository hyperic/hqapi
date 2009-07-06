package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ServerConfigApi;
import org.hyperic.hq.hqapi1.types.ServerConfigResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class ServerConfigSet_test extends HQApiTestBase {

    public ServerConfigSet_test(String name) {
        super(name);
    }

    public void testSetConfig() throws Exception {

        ServerConfigApi sApi = getApi().getServerConfigApi();

        ServerConfigResponse configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);

        StatusResponse response = sApi.setConfig(configResponse.getServerConfig());
        hqAssertSuccess(response);
    }
}
