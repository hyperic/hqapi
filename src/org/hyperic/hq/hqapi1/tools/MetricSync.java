package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Metric;

import java.util.List;

public class MetricSync extends ToolsBase {

    private static void syncMetrics(String[] args) throws Exception {

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

    public static void main(String[] args) throws Exception {
        try {
            syncMetrics(args);
        } catch (Exception e) {
            System.err.println("Error syncing metrics: " + e.getMessage());
            System.exit(-1);
        }
    }    
}
