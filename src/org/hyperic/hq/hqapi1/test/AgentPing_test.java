package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.types.PingAgentResponse;
import org.hyperic.hq.hqapi1.types.Agent;

public class AgentPing_test extends HQApiTestBase {

    public AgentPing_test(String name) {
        super(name);
    }

    public void testPingInvalidAgent() throws Exception {

        AgentApi api = getApi().getAgentApi();

        Agent a = new Agent();
        a.setId(Integer.MAX_VALUE);
        PingAgentResponse response = api.pingAgent(a);
        hqAssertFailureObjectNotFound(response);
    }
}
