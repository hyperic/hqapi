package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.types.AgentResponse;
import org.hyperic.hq.hqapi1.types.Agent;

public class AgentGet_test extends HQApiTestBase {

    public AgentGet_test(String name) {
        super(name);
    }

    private void validateAgent(Agent a) {
        assertTrue(a.getAddress().length() > 0);
        assertTrue(a.getPort() > 0);
        assertTrue(a.getVersion().length() > 0);
        assertTrue(a.getId() > 0);
    }

    public void testGetAgentInvalid() throws Exception {

        AgentApi api = getApi().getAgentApi();

        AgentResponse response = api.getAgent("invalid.hyperic.com", 2144);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetAgentValid() throws Exception {

        Agent agent = getRunningAgent();

        AgentApi api = getApi().getAgentApi();

        AgentResponse response = api.getAgent(agent.getAddress(),
                                              agent.getPort());
        hqAssertSuccess(response);

        Agent a = response.getAgent();
        validateAgent(a);
    }

    public void testGetAgentById() throws Exception {

        Agent agent = getRunningAgent();

        AgentApi api = getApi().getAgentApi();

        AgentResponse response = api.getAgent(agent.getId());
        hqAssertSuccess(response);

        Agent a = response.getAgent();
        validateAgent(a);
    }

    public void testGetAgentByInvalidId() throws Exception {

        AgentApi api = getApi().getAgentApi();

        AgentResponse response = api.getAgent(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);        
    }
}
