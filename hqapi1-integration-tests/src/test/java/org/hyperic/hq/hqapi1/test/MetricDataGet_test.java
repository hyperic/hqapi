package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.MetricDataResponse;

public class MetricDataGet_test extends MetricDataTestBase {

    public MetricDataGet_test(String name) {
        super(name);
    }

    public void testValidGet() throws Exception {

        MetricApi api = getApi().getMetricApi();
        MetricDataApi dataApi = getApi().getMetricDataApi();

        Resource platform = getLocalPlatformResource(false, false);
        MetricsResponse metricsResponse = api.getMetrics(platform, true);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                   metricsResponse.getMetric().size() > 0);

        Metric m = metricsResponse.getMetric().get(0);

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricDataResponse dataResponse = dataApi.getData(m, start, end);
        hqAssertSuccess(dataResponse);

        validateMetricData(dataResponse.getMetricData());
    }

    public void testGetInvalidMetricId() throws Exception {

        MetricDataApi dataApi = getApi().getMetricDataApi();

        Metric m = new Metric();
        m.setId(Integer.MAX_VALUE);

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricDataResponse dataResponse = dataApi.getData(m, start, end);
        hqAssertFailureObjectNotFound(dataResponse);
    }

    public void testGetInvalidRange() throws Exception {
        
        MetricApi api = getApi().getMetricApi();
        MetricDataApi dataApi = getApi().getMetricDataApi();

        Resource platform = getLocalPlatformResource(false, false);
        MetricsResponse metricsResponse = api.getMetrics(platform, true);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                   metricsResponse.getMetric().size() > 0);

        Metric m = metricsResponse.getMetric().get(0);

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricDataResponse dataResponse = dataApi.getData(m, end, start);
        hqAssertFailureInvalidParameters(dataResponse);
    }   
}
