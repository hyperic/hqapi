package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.types.GetAgentResponse;

public class AgentGet_test extends HQApiTestBase {

    public AgentGet_test(String name) {
        super(name);
    }

    public void testGetAgentInvalid() throws Exception {

        AgentApi api = getApi().getAgentApi();

        GetAgentResponse response = api.getAgent("invalid.hyperic.com", 2144);
        hqAssertFailureObjectNotFound(response);
    }
}
