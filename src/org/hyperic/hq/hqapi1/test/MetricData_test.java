package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.GetMetricDataResponse;
import org.hyperic.hq.hqapi1.types.ListMetricResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.MetricData;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.MetricApi;

public class MetricData_test extends MetricTestBase {

    public MetricData_test(String name) {
        super(name);
    }

    public void testGetMetricData() throws Exception {

        Resource r = getResource();
        if (r == null) {
            getLog().error("Unable to find the local platform, skipping test");
            return;
        }

        MetricApi api = getApi().getMetricApi();
        ListMetricResponse resp = api.listMetrics(r);
        hqAssertSuccess(resp);

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        for (Metric m : resp.getMetric()) {
            GetMetricDataResponse dataResponse = api.getMetricData(m, start, end);
            hqAssertSuccess(dataResponse);
            assertTrue("No metric data found for " + m.getName(),
                       dataResponse.getMetricData().size() > 0);
            for (MetricData d : dataResponse.getMetricData()) {
                assertTrue("Metric point timestamp greater than end time. ts=" +
                           d.getTimestamp() + " end=" + end,
                           d.getTimestamp() <= end);
                assertTrue("Metric point timestamp less than start time ts=" +
                           d.getTimestamp() + " start=" + start,
                           d.getTimestamp() >= start);
                assertTrue("Metric value less than zero",
                           d.getValue() >= 0);
            }
        }
    }
}
