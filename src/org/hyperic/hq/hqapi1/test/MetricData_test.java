package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.GetMetricDataResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.MetricData;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ListMetricsResponse;
import org.hyperic.hq.hqapi1.MetricApi;

public class MetricData_test extends MetricTestBase {

    Resource _r;

    public MetricData_test(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        _r = getResource();
    }

    public void testGetMetricData() throws Exception {

        if (_r == null) {
            getLog().error("Unable to find the local platform, skipping test");
            return;
        }

        MetricApi api = getApi().getMetricApi();
        ListMetricsResponse resp = api.listMetrics(_r);
        hqAssertSuccess(resp);

        assertTrue("No metrics found for " + _r.getName(),
                   resp.getMetric().size() > 0);
        Metric m = resp.getMetric().get(0);

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);

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

    public void testGetMetricDataInvalidId() throws Exception {

        MetricApi api = getApi().getMetricApi();
        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        Metric m = new Metric();
        m.setId(Integer.MAX_VALUE);
        GetMetricDataResponse dataResponse = api.getMetricData(m, start, end);
        hqAssertFailureObjectNotFound(dataResponse);
    }

    public void testGetMetricDataInvalidRange() throws Exception {

        if (_r == null) {
            getLog().error("Unable to find the local platform, skipping test");
            return;
        }

        MetricApi api = getApi().getMetricApi();
        ListMetricsResponse resp = api.listMetrics(_r);
        hqAssertSuccess(resp);

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);

        assertTrue("No metrics found for " + _r.getName(),
                   resp.getMetric().size() > 0);
        Metric m = resp.getMetric().get(0);

        // Test end > start.
        GetMetricDataResponse dataResponse = api.getMetricData(m, end, start);
        hqAssertFailureInvalidParameters(dataResponse);
    }
}
