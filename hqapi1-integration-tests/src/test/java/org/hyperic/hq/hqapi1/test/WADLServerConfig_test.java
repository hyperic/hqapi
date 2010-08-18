package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLServerConfig_test extends WADLTestBase {

    public void testGet() throws Exception {
        HttpLocalhost8080HquHqapi1.ServerConfigGetConfigHqu get =
                new HttpLocalhost8080HquHqapi1.ServerConfigGetConfigHqu();

        ServerConfigResponse response = get.getAsServerConfigResponse();
        hqAssertSuccess(response);
    }

    public void testSet() throws Exception {
        HttpLocalhost8080HquHqapi1.ServerConfigGetConfigHqu get =
                new HttpLocalhost8080HquHqapi1.ServerConfigGetConfigHqu();
        HttpLocalhost8080HquHqapi1.ServerConfigSetConfigHqu set =
                new HttpLocalhost8080HquHqapi1.ServerConfigSetConfigHqu();

        ServerConfigResponse response = get.getAsServerConfigResponse();
        hqAssertSuccess(response);

        ServerConfigRequest request = new ServerConfigRequest();
        request.getServerConfig().addAll(response.getServerConfig());

        StatusResponse setResponse = set.postApplicationXmlAsStatusResponse(request);
        hqAssertSuccess(setResponse);  
    }
}
