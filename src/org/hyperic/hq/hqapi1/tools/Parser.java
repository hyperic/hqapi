package org.hyperic.hq.hqapi1.tools;

import jargs.gnu.CmdLineParser;

import java.util.List;
import java.util.ArrayList;

public class Parser extends CmdLineParser {

    private List<String> _requiredHelp = new ArrayList<String>();
    private List<String> _optionalHelp = new ArrayList<String>();

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
            _requiredHelp.add(help);
        } else {
            _optionalHelp.add(help);
        }

        return option;
    }

    public void printUsage() {
        System.err.println("Usage: prog [arguments]");
        System.err.println("Required arguments:");

        for (String help : _requiredHelp) {
            System.err.println(help);
        }

        System.err.println("Optional arguments:");
        for (String help : _optionalHelp) {
            System.err.println(help);
        }
    }
}
