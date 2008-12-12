package org.hyperic.hq.hqapi1.tools;

import jargs.gnu.CmdLineParser;

import java.util.List;
import java.util.ArrayList;

public class Parser extends CmdLineParser {

    List<String> requiredHelp = new ArrayList<String>();
    List<String> optionalHelp = new ArrayList<String>();

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
            requiredHelp.add(help);
        } else {
            optionalHelp.add(help);
        }

        return option;
    }

    public void printUsage() {
        System.err.println("Usage: prog [arguments]");
        System.err.println("Required arguments:");

        for (String help : requiredHelp) {
            System.err.println(help);
        }

        System.err.println("Optional arguments:");
        for (String help : optionalHelp) {
            System.err.println(help);
        }
    }
}
