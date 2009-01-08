package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.AgentsResponse;

import java.util.Arrays;

public class AgentCommand extends Command {

    private static String CMD_LIST    = "list";

    private static String[] COMMANDS = { CMD_LIST };

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }

    protected void handleCommand(String[] args) throws Exception {
        
        if (args.length == 0) {
            printUsage();
            System.exit(-1);
        }

        if (args[0].equals(CMD_LIST)) {
            list(trim(args));
        } else {
            printUsage();
            System.exit(-1);
        }
    }

    private void list(String[] args) throws Exception {
        
        OptionParser p = getOptionParser();

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        AgentApi agentApi = api.getAgentApi();

        AgentsResponse agents = agentApi.getAgents();

        XmlUtil.serialize(agents, System.out, Boolean.TRUE);
    }
}
