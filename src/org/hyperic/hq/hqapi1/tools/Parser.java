package org.hyperic.hq.hqapi1.tools;

import jargs.gnu.CmdLineParser;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Parser extends CmdLineParser {

    private Map<Option,String> _requiredHelp = new HashMap<Option,String>();
    private Map<Option,String> _optionalHelp = new HashMap<Option,String>();

    public Option addOption(Option option, boolean required, String helpString)
    {
        addOption(option);

        String help;
        if (option.shortForm() != null) {
            help = " -" + option.shortForm() + "/--" + option.longForm() +
                ": " + helpString;
        } else {
            help = "--" + option.longForm() + ": " + helpString;
        }
        if (required) {
            _requiredHelp.put(option, help);
        } else {
            _optionalHelp.put(option, help);
        }

        return option;
    }

    public Object getRequiredOptionValue(Option opt) {
        Object o = getOptionValue(opt);

        if (o == null && _requiredHelp.containsKey(opt)) {
            System.err.println("Required argument " + opt.longForm() + " not given");
            System.exit(-1);
        }

        return o;
    }

    public void printUsage() {
        System.err.println("Usage: prog [arguments]");
        System.err.println("Required arguments:");

        for (Option opt : _requiredHelp.keySet()) {
            System.err.println(_requiredHelp.get(opt));
        }

        System.err.println("Optional arguments:");
        for (Option opt : _optionalHelp.keySet()) {
            System.err.println(_optionalHelp.get(opt));
        }
    }
}
