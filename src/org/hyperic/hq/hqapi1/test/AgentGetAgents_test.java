package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.types.GetAgentsResponse;
import org.hyperic.hq.hqapi1.types.Agent;

import java.util.List;

public class AgentGetAgents_test extends HQApiTestBase {

    public AgentGetAgents_test(String name) {
        super(name);
    }

    public void testGetAllAgents() throws Exception {

        AgentApi api = getApi().getAgentApi();

        GetAgentsResponse response = api.getAgents();
        hqAssertSuccess(response);

        List<Agent> agents = response.getAgent();
        if (agents.size() == 0) {
            getLog().warn("Agent listing returned 0 results.  Test results " +
                          "may not be accurate.");
        }

        for (Agent a : agents) {
            assertTrue(a.getAddress().length() > 0);
            assertTrue(a.getPort() > 0);
            assertTrue(a.getVersion().length() > 0);
            assertTrue(a.getId() > 0);
        }
    }
}
