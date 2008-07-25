package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.GetAgentResponse;
import org.hyperic.hq.hqapi1.types.PingAgentResponse;
import org.hyperic.hq.hqapi1.types.Agent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Hyperic HQ Agent API.
 *
 * This class provides access to the agents within the HQ system.  Each of the
 * methods in this class return {@link org.hyperic.hq.hqapi1.types.Response}
 * objects that wrap the result of the method with a
 * {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 *
 */
public class AgentApi {

   private HQConnection _connection;

    public AgentApi(HQConnection connection) {
        _connection = connection;
    }

    public GetAgentResponse getAgent(String address, int port)
        throws IOException
    {
        Map<String,String> params = new HashMap<String,String>();
        params.put("address", address);
        params.put("port", String.valueOf(port));
        return _connection.doGet("/hqu/hqapi1/agent/getAgent.hqu",
                                 params, GetAgentResponse.class);
    }

    public PingAgentResponse pingAgent(Agent agent)
        throws IOException
    {
        Map <String,String> params = new HashMap<String,String>();
        params.put("id", String.valueOf(agent.getId()));
        return _connection.doGet("/hqu/hqapi1/agent/pingAgent.hqu",
                                 params, PingAgentResponse.class);
    }
}
