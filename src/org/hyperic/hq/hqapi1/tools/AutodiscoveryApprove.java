package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.AutodiscoveryApi;
import org.hyperic.hq.hqapi1.types.QueueResponse;
import org.hyperic.hq.hqapi1.types.AIPlatform;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AutodiscoveryApprove extends ToolsBase {

    private static String OPT_REGEX = "regex";

    private static void approve(String[] args) throws Exception {

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

    public static void main(String[] args) throws Exception {
        try {
            approve(args);
        } catch (Exception e) {
            System.err.println("Error approving resources: " + e.getMessage());
            System.exit(-1);
        }
    }
}
