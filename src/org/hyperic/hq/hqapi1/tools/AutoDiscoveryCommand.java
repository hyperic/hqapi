package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.AutodiscoveryApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.AIPlatform;
import org.hyperic.hq.hqapi1.types.QueueResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoDiscoveryCommand extends Command {

    private static String CMD_LIST    = "list";
    private static String CMD_APPROVE = "approve";

    private static String[] COMMANDS = { CMD_LIST, CMD_APPROVE };

    private static String OPT_REGEX = "regex";

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
        } else if (args[0].equals(CMD_APPROVE)) {
            approve(trim(args));
        } else {
            printUsage();
            System.exit(-1);
        }
    }

    private void list(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        AutodiscoveryApi autodiscoveryApi = api.getAutodiscoveryApi();

        QueueResponse queue = autodiscoveryApi.getQueue();

        XmlUtil.serialize(queue, System.out, Boolean.TRUE);
    }

    private void approve(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_REGEX, "If specified, only platforms that match the given " +
                  "regular expression will be approved").
                withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);


        Pattern pattern;
        if (options.has(OPT_REGEX)) {
            pattern = Pattern.compile((String)options.valueOf(OPT_REGEX));
        } else {
            pattern = Pattern.compile(".*");
        }

        HQApi api = getApi(options);
        AutodiscoveryApi autodiscoveryApi = api.getAutodiscoveryApi();

        QueueResponse queue = autodiscoveryApi.getQueue();
        checkSuccess(queue);

        int num = 0;
        for (AIPlatform plat : queue.getAIPlatform()) {
            Matcher m = pattern.matcher(plat.getName());
            if (m.matches()) {
                System.out.println("Approving " + plat.getName());
                StatusResponse approveResponse = autodiscoveryApi.approve(plat.getId());
                checkSuccess(approveResponse);
                num++;
            }
        }
        System.out.println("Approved " + num + " platforms.");
    }
}
