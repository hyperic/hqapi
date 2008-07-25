package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.types.GetAgentResponse;
import org.hyperic.hq.hqapi1.types.Agent;

public class AgentGet_test extends HQApiTestBase {

    public AgentGet_test(String name) {
        super(name);
    }

    public void testGetAgentInvalid() throws Exception {

        AgentApi api = getApi().getAgentApi();

        GetAgentResponse response = api.getAgent("invalid.hyperic.com", 2144);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetAgentValid() throws Exception {

        Agent agent = getLocalAgent();

        if (agent == null) {
            getLog().warn("No agent running, skipping test.");
            return;
        }

        AgentApi api = getApi().getAgentApi();

        GetAgentResponse response = api.getAgent(agent.getAddress(),
                                                 agent.getPort());
        hqAssertSuccess(response);

        Agent a = response.getAgent();
        assertTrue(a.getAddress().length() > 0);
        assertTrue(a.getPort() > 0);
        assertTrue(a.getVersion().length() > 0);
        assertTrue(a.getId() > 0);
    }
}
