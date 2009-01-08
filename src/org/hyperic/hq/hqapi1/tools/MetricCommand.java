package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.Arrays;
import java.util.List;

public class MetricCommand extends Command {

    private static String CMD_LIST = "list";
    private static String CMD_SYNC = "sync";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC };

    private static final String OPT_RESOURCE_ID = "id";
    private static final String OPT_ENABLED = "enabled";

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
        } else if (args[0].equals(CMD_SYNC)) {
            sync(trim(args));
        } else {
            printUsage();
            System.exit(-1);
        }
    }

    private void list(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_RESOURCE_ID, "The resource id to query for metrics").
                withRequiredArg().ofType(Integer.class);

        p.accepts(OPT_ENABLED, "When specified, only list metrics that are " +
                  "currently enabled");

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi resourceApi = api.getResourceApi();
        MetricApi metricApi = api.getMetricApi();

        ResourceResponse resourceResponse =
                resourceApi.getResource((Integer)getRequired(options, OPT_RESOURCE_ID),
                                        false, false);
        checkSuccess(resourceResponse);

        MetricsResponse metrics;
        if (options.has(OPT_ENABLED)) {
            metrics = metricApi.getEnabledMetrics(resourceResponse.getResource());
        } else {
            metrics = metricApi.getMetrics(resourceResponse.getResource());
        }

        XmlUtil.serialize(metrics, System.out, Boolean.TRUE);
    }

    private void sync(String[] args) throws Exception {

        OptionParser p = getOptionParser();
        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        MetricApi metricApi = api.getMetricApi();

        MetricsResponse resp = XmlUtil.deserialize(MetricsResponse.class,
                                                   System.in);

        List<Metric> metrics = resp.getMetric();

        StatusResponse syncResponse = metricApi.syncMetrics(metrics);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + metrics.size() + " metrics.");        
    }
}
