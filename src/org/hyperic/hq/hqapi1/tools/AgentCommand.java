/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.AgentResponse;
import org.hyperic.hq.hqapi1.types.AgentsResponse;
import org.hyperic.hq.hqapi1.types.PingAgentResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class AgentCommand extends Command {

    private static final String CMD_LIST    = "list";
    private static final String CMD_PING    = "ping";

    private static final String OPT_ID      = "id";
    private static final String OPT_ADDRESS = "agentAddress";
    private static final String OPT_PORT    = "agentPort";
    private static final String OPT_FQDN    = "fqdn";

    private static String[] COMMANDS = { CMD_LIST, CMD_PING };

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
        } else if (args[0].equals(CMD_PING)) {
            ping(trim(args));
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
        checkSuccess(agents);

        XmlUtil.serialize(agents, System.out, Boolean.TRUE);
    }

    private void printPingResponse(Agent a, PingAgentResponse response) {
        if (response.isUp()) {
            System.out.println("Successfully pinged agent " + a.getAddress() + ":" + a.getPort());
        } else {
            System.out.println("Failure pinging agent at " + a.getAddress() + ":" + a.getPort());
        }
    }

    private void ping(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "The id of the agent to ping").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_ADDRESS, "The address of the agent to ping.  Must be used with --" + OPT_PORT).
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_PORT, "The port of the agent to ping.  Must be used with --" + OPT_ADDRESS).
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_FQDN, "The platform FQDN of the agent to ping").
                withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        AgentApi agentApi = api.getAgentApi();

        if (options.has(OPT_ID)) {
            Integer id = (Integer)options.valueOf(OPT_ID);
            AgentResponse response = agentApi.getAgent(id);
            checkSuccess(response);
            PingAgentResponse pingResponse = agentApi.pingAgent(response.getAgent());
            checkSuccess(pingResponse);
            printPingResponse(response.getAgent(), pingResponse);
        } else if (options.has(OPT_ADDRESS) && options.has(OPT_PORT)) {
            String address = (String)options.valueOf(OPT_ADDRESS);
            Integer port = (Integer)options.valueOf(OPT_PORT);
            AgentResponse response = agentApi.getAgent(address, port);
            checkSuccess(response);
            PingAgentResponse pingResponse = agentApi.pingAgent(response.getAgent());
            checkSuccess(pingResponse);
            printPingResponse(response.getAgent(), pingResponse);
        } else if (options.has(OPT_FQDN)) {
            ResourceApi rApi = api.getResourceApi();
            String fqdn = (String)options.valueOf(OPT_FQDN);
            ResourceResponse resourceResponse = rApi.getPlatformResource(fqdn, false, false);
            checkSuccess(resourceResponse);
            Agent a = resourceResponse.getResource().getAgent();
            PingAgentResponse pingResponse = agentApi.pingAgent(a);
            checkSuccess(pingResponse);
            printPingResponse(a, pingResponse);
        } else {
            // Ping via XML
            InputStream is = getInputStream(options);

            AgentsResponse resp = XmlUtil.deserialize(AgentsResponse.class, is);
            List<Agent> agents = resp.getAgent();
            for (Agent a : agents) {
                PingAgentResponse pingResponse = agentApi.pingAgent(a);
                checkSuccess(pingResponse);
                printPingResponse(a, pingResponse);
            }
        }
    }
}
