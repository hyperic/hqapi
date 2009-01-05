package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.MetricsResponse;

public class MetricList extends ToolsBase {

    private static final String OPT_RESOURCE_ID = "id";
    private static final String OPT_ENABLED = "enabled";

    private static void listMetrics(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_RESOURCE_ID, "The resource id to query for metrics").
                withRequiredArg().ofType(Integer.class);

        p.accepts(OPT_ENABLED, "When specified, only list metrics that are " +
                  "currently enabled");

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi resourceApi = api.getResourceApi();
        MetricApi metricApi = api.getMetricApi();

        if (!options.has(OPT_RESOURCE_ID)) {
            System.out.println(OPT_RESOURCE_ID + " argument required.");
            System.exit(-1);
        }

        ResourceResponse resourceResponse =
                resourceApi.getResource((Integer)options.valueOf(OPT_RESOURCE_ID),
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

    public static void main(String[] args) throws Exception {
        try {
            listMetrics(args);
        } catch (Exception e) {
            System.err.println("Error listing metrics: " + e.getMessage());
            System.exit(-1);
        }
    }
}
