package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.MetricDataResponse;

public class MetricDataList extends ToolsBase {

    private static final String OPT_METRIC_ID = "metricId";

    private static void listMetricData(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_METRIC_ID, "The metric id to query for data").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        MetricApi metricApi = api.getMetricApi();

        if (!options.has(OPT_METRIC_ID)) {
            System.out.println(OPT_METRIC_ID + " argument required.");
            System.exit(-1);
        }

        long end = System.currentTimeMillis();
        long start = end - 8 * 60 * 60 * 1000;
        MetricDataResponse data =
                metricApi.getMetricData((Integer)options.valueOf(OPT_METRIC_ID),
                                        start, end);

        XmlUtil.serialize(data, System.out, Boolean.TRUE);
    }

    public static void main(String[] args) throws Exception {
        try {
            listMetricData(args);
        } catch (Exception e) {
            System.err.println("Error listing metrics: " + e.getMessage());
            System.exit(-1);
        }
    }
}
