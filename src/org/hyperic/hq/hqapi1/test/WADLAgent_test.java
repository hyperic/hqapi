package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLAgent_test extends WADLTestBase {

    public void testAgent() throws Exception {

        Endpoint.AgentGetHqu agentGet = new Endpoint.AgentGetHqu();
        Endpoint.AgentListHqu agentList = new Endpoint.AgentListHqu();
        Endpoint.AgentPingHqu agentPing = new Endpoint.AgentPingHqu();

        AgentsResponse agents = agentList.getAsAgentsResponse();
        hqAssertSuccess(agents);
        assertTrue("No agents found!", agents.getAgent().size() > 0);

        Agent a = agents.getAgent().get(0);
        AgentResponse agent = agentGet.getAsAgentResponse(a.getId());
        hqAssertSuccess(agent);
        
        PingAgentResponse ping = agentPing.getAsPingAgentResponse(a.getId());
        hqAssertSuccess(ping);
    }
}
