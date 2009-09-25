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

import java.util.Map;
import java.util.TreeMap;

public class Shell {

    private static Map<String,Command> _commands = new TreeMap<String,Command>();

    static {
        _commands.put("agent", new AgentCommand());
        _commands.put("alertdefinition", new AlertDefinitionCommand());
        _commands.put("application", new ApplicationCommand());
        _commands.put("autodiscovery", new AutoDiscoveryCommand());
        _commands.put("escalation", new EscalationCommand());
        _commands.put("group", new GroupCommand());
        _commands.put("maintenance", new MaintenanceCommand());
        _commands.put("metric", new MetricCommand());
        _commands.put("metricData", new MetricDataCommand());
        _commands.put("metricTemplate", new MetricTemplateCommand());
        _commands.put("resource", new ResourceCommand());
        _commands.put("dependency", new ResourceEdgeCommand());
        _commands.put("role", new RoleCommand());
        _commands.put("user", new UserCommand());
        _commands.put("serverConfig", new ServerConfigCommand());
        _commands.put("alert", new AlertCommand());
        _commands.put("event", new EventCommand());
        _commands.put("control", new ControlCommand());
    }

    private static void printHelp() {
        System.out.println("HQ Api Command Shell");
        System.out.println("");
        System.out.println("Available commands:");
        for (String command : _commands.keySet()) {
            System.out.println("    " + command);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printHelp();
            System.exit(-1);
        }

        Command cmd = _commands.get(args[0]);

        if (cmd == null) {
            printHelp();
            System.exit(-1);
        }

        try {
            cmd.handleCommand(Command.trim(args));
        } catch (Exception e) {
            System.err.println("Error running command: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }
}
