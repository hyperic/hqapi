package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.types.AgentsResponse;

public class AgentList extends ToolsBase {

    public static void listAgents(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        AgentApi agentApi = api.getAgentApi();

        AgentsResponse agents = agentApi.getAgents();

        XmlUtil.serialize(agents, System.out, Boolean.TRUE);
    }

    public static void main(String[] args) throws Exception {
        try {
            listAgents(args);
        } catch (Exception e) {
            System.err.println("Error listing agents: " + e.getMessage());
            System.exit(-1);
        }
    }
}
