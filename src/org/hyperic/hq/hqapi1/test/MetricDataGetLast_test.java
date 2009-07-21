package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.LastMetricDataResponse;

public class MetricDataGetLast_test extends MetricDataTestBase {

    public MetricDataGetLast_test(String name) {
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

        LastMetricDataResponse dataResponse = dataApi.getData(m.getId());
        hqAssertSuccess(dataResponse);
       
        validateLastMetricData(dataResponse.getLastMetricData());
    }

    public void testGetInvalidMetricId() throws Exception {
        MetricDataApi dataApi = getApi().getMetricDataApi();
        LastMetricDataResponse dataResponse = dataApi.getData(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(dataResponse);
    }

    public void testGetLastNoData() throws Exception {

        MetricApi api = getApi().getMetricApi();
        MetricDataApi dataApi = getApi().getMetricDataApi();

        Resource platform = getLocalPlatformResource(false, false);
        MetricsResponse metricsResponse = api.getMetrics(platform, false);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                   metricsResponse.getMetric().size() > 0);

        Metric metric = null;
        for (Metric m : metricsResponse.getMetric()) {
            if (!m.isEnabled()) {
                metric = m;
            }
        }

        assertNotNull("No disabled metric could be found", metric);

        LastMetricDataResponse dataResponse = dataApi.getData(metric.getId());
        hqAssertSuccess(dataResponse);
        // TODO: What is the correct behavior of the API if the last data point
        //       could not be found? Return an error for simply null?
        assertNull("Metric datapoint not null",
                   dataResponse.getLastMetricData().getDataPoint());
    }
}
