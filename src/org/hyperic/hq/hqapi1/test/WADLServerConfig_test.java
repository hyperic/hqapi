package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLServerConfig_test extends WADLTestBase {

    public void testGet() throws Exception {
        Endpoint.ServerConfigGetConfigHqu get =
                new Endpoint.ServerConfigGetConfigHqu();

        ServerConfigResponse response = get.getAsServerConfigResponse();
        hqAssertSuccess(response);
    }

    public void testSet() throws Exception {
        Endpoint.ServerConfigGetConfigHqu get =
                new Endpoint.ServerConfigGetConfigHqu();
        Endpoint.ServerConfigSetConfigHqu set =
                new Endpoint.ServerConfigSetConfigHqu();

        ServerConfigResponse response = get.getAsServerConfigResponse();
        hqAssertSuccess(response);

        ServerConfigRequest request = new ServerConfigRequest();
        request.getServerConfig().addAll(response.getServerConfig());

        StatusResponse setResponse = set.postAsStatusResponse(request);
        hqAssertSuccess(setResponse);  
    }
}
