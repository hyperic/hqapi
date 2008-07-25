package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.GetRolesResponse;
import org.hyperic.hq.hqapi1.types.GetAgentResponse;
import org.hyperic.hq.hqapi1.types.PingAgentResponse;

import java.io.IOException;
import java.util.HashMap;

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

    public GetAgentResponse getAgent(String ip, int port)
        throws IOException
    {
        return null;
    }

    public PingAgentResponse pingAgent()
        throws IOException
    {
        return null;
    }
}
