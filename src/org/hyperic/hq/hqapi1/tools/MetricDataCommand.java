package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.Arrays;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.MetricDataResponse;

public class MetricDataCommand extends Command {

    private static String CMD_LIST = "list";

    private static String[] COMMANDS = { CMD_LIST };

    private static final String OPT_METRIC_ID = "metricId";

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

        p.accepts(OPT_METRIC_ID, "The metric id to query for data").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        MetricApi metricApi = api.getMetricApi();

        long end = System.currentTimeMillis();
        long start = end - 8 * 60 * 60 * 1000;
        MetricDataResponse data =
                metricApi.getMetricData((Integer)getRequired(options, OPT_METRIC_ID),
                                        start, end);

        XmlUtil.serialize(data, System.out, Boolean.TRUE);
    }
}
