package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.GetAgentResponse;
import org.hyperic.hq.hqapi1.types.PingAgentResponse;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.GetAgentsResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Hyperic HQ Agent API.
 * <br><br>
 * This class provides access to the agents within the HQ system.  Each of the
 * methods in this class return {@link org.hyperic.hq.hqapi1.types.Response}
 * objects that wrap the result of the method with a
 * {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 */
public class AgentApi extends BaseApi {

    public AgentApi(HQConnection connection) {
        super(connection);
    }

    /**
     * Get an {@link Agent} by address and port.
     *
     * @param address The address of the requested agent.  This can be a hostname
     * or IP address.
     * @param port The port of the requested Agent.
     *
     * @return  On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a the requested agent is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetAgentResponse#getAgent()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public GetAgentResponse getAgent(String address, int port)
        throws IOException
    {
        Map<String,String> params = new HashMap<String,String>();
        params.put("address", address);
        params.put("port", String.valueOf(port));
        return doGet("agent/getAgent.hqu", params, GetAgentResponse.class);
    }

    /**
     * Get a list of all {@link Agent}s.
     *
     * @return  On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of agents is returned via 
     * {@link org.hyperic.hq.hqapi1.types.GetAgentsResponse#getAgent()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public GetAgentsResponse getAgents()
        throws IOException
    {
        return doGet("agent/list.hqu", new HashMap<String,String>(),
                     GetAgentsResponse.class);
    }

    /**
     * Ping an {@link Agent}.
     *
     * @param agent The agent to ping.
     * 
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a the requested agent status is returned via
     * {@link org.hyperic.hq.hqapi1.types.PingAgentResponse#isUp()}.
     * 
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public PingAgentResponse pingAgent(Agent agent)
        throws IOException
    {
        Map <String,String> params = new HashMap<String,String>();
        params.put("id", String.valueOf(agent.getId()));
        return doGet("agent/pingAgent.hqu", params, PingAgentResponse.class);
    }
}
