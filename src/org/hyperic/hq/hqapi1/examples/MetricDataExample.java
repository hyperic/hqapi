package org.hyperic.hq.hqapi1.examples;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.types.GetResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.FindResourcesResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.GetMetricDataResponse;
import org.hyperic.hq.hqapi1.types.MetricData;
import org.hyperic.hq.hqapi1.types.ListMetricsResponse;
import org.hyperic.hq.hqapi1.types.DataPoint;

import java.util.Date;

public class MetricDataExample {

    private static final String TYPE = "FileServer Mount";
    private static final long RANGE = 8 * 60 * 60 * 1000; // 8 hours.

    public static void main(String[] args) throws Exception {

        HQApi api = new HQApi("localhost", 7080, false, "hqadmin", "hqadmin");
        ResourceApi resourceApi = api.getResourceApi();

        // Find resource type
        GetResourcePrototypeResponse prototypeResponse = resourceApi.getResourcePrototype(TYPE);
        if (prototypeResponse.getStatus() != ResponseStatus.SUCCESS) {
            System.err.print("Error finding resource type: " +
                             prototypeResponse.getError().getReasonText());
            return;
        }

        // Find resources of that tytpe
        FindResourcesResponse resourcesResponse =
                resourceApi.findResources(prototypeResponse.getResourcePrototype());
        if (resourcesResponse.getStatus() != ResponseStatus.SUCCESS) {
            System.err.println("Error finding resources:" +
                               prototypeResponse.getError().getReasonText());
        }

        // For each resource, find all metrics & metric data.
        MetricApi metricApi = api.getMetricApi();
        long end = System.currentTimeMillis();
        long start = end - RANGE;
        for (Resource r : resourcesResponse.getResource()) {
            ListMetricsResponse metricsResponse = metricApi.listEnabledMetrics(r);

            if (metricsResponse.getStatus() != ResponseStatus.SUCCESS) {
                System.err.println("Error finding metrics for resoruce " +
                                   r.getName() + ":" +
                                   metricsResponse.getError().getReasonText());
                return;
            }

            for (Metric m : metricsResponse.getMetric()) {
                GetMetricDataResponse dataResponse =
                        metricApi.getMetricData(m.getId(), start, end);
                if (dataResponse.getStatus() != ResponseStatus.SUCCESS) {
                    System.err.println("Error getting data for metric " +
                                       m.getName() + " on resource " +
                                       r.getName() + ":" +
                                       dataResponse.getError().getReasonText());
                    return;
                }

                MetricData data = dataResponse.getMetricData();

                System.out.println("Resource: " + data.getResourceName());
                System.out.println("Metric: " + data.getMetricName());
                for (DataPoint d : dataResponse.getMetricData().getDataPoint()) {
                    System.out.println("  time=" + new Date(d.getTimestamp()).toString() + " data=" + d.getValue());
                }
            }
        }
    }
}
