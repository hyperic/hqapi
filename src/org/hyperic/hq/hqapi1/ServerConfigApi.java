package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.ServerConfigsResponse;
import org.hyperic.hq.hqapi1.types.ServerConfig;
import org.hyperic.hq.hqapi1.types.ServerConfigRequest;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.ServerConfigResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  The Hyperic HQ Server Configuration API.
 */
public class ServerConfigApi extends BaseApi {

    ServerConfigApi(HQConnection conn) {
        super(conn);
    }

    /**
     * Get the HQ server configuration.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a List of {@link org.hyperic.hq.hqapi1.types.ServerConfig}s.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ServerConfigsResponse getConfig() throws IOException {
        return doGet("serverconfig/getConfig.hqu", new HashMap<String, String[]>(),
                     ServerConfigsResponse.class);
    }

    /**
     * Set the HQ server configuration.  The List of ServerConfig's must include
     * all configurations returned from #getConfig.
     *
     * @param configs An array of ServerConfig objects.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if
     * the server configuration was updated sucessfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse setConfig(List<ServerConfig> configs) throws IOException {
        ServerConfigRequest request = new ServerConfigRequest();
        request.getServerConfig().addAll(configs);
        return doPost("serverConfig/setConfig.hqu", request, StatusResponse.class);
    }
}
