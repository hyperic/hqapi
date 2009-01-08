package org.hyperic.hq.hqapi1.tools;

import java.util.HashMap;
import java.util.Map;

public class Shell {

    private static Map<String,Command> _commands = new HashMap<String,Command>();

    static {
        _commands.put("agent", new AgentCommand());
        _commands.put("autodiscovery", new AutoDiscoveryCommand());
        _commands.put("escalation", new EscalationCommand());
        _commands.put("group", new GroupCommand());
        _commands.put("metric", new MetricCommand());
        _commands.put("metricData", new MetricDataCommand());
        _commands.put("metricTemplate", new MetricTemplateCommand());
        _commands.put("resource", new ResourceCommand());
        _commands.put("role", new RoleCommand());
        _commands.put("user", new UserCommand());
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

        cmd.handleCommand(Command.trim(args));
    }
}
