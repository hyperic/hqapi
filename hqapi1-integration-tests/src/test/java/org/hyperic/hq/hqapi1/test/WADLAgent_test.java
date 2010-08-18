package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLAgent_test extends WADLTestBase {

    public void testAgent() throws Exception {

        HttpLocalhost8080HquHqapi1.AgentGetHqu agentGet = new HttpLocalhost8080HquHqapi1.AgentGetHqu();
        HttpLocalhost8080HquHqapi1.AgentListHqu agentList = new HttpLocalhost8080HquHqapi1.AgentListHqu();
        HttpLocalhost8080HquHqapi1.AgentPingHqu agentPing = new HttpLocalhost8080HquHqapi1.AgentPingHqu();

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
