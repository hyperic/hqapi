package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.MetricsDataResponse;
import org.hyperic.hq.hqapi1.types.MetricData;
import org.hyperic.hq.hqapi1.types.Metric;

import java.util.ArrayList;
import java.util.List;

public class MetricDataGetMulti_test extends MetricDataTestBase {

    public MetricDataGetMulti_test(String name) {
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

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricsDataResponse dataResponse = dataApi.getData(metricsResponse.getMetric(),
                                                           start, end);
        hqAssertSuccess(dataResponse);

        for (MetricData metricData : dataResponse.getMetricData()) {
            validateMetricData(metricData);
        }
    }

    public void testGetInvalidMetricId() throws Exception {

        MetricDataApi dataApi = getApi().getMetricDataApi();

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);

        List<Metric> metrics = new ArrayList<Metric>();
        Metric m = new Metric();
        m.setId(Integer.MAX_VALUE);
        metrics.add(m);

        MetricsDataResponse dataResponse = dataApi.getData(metrics, start, end);
        hqAssertFailureObjectNotFound(dataResponse);
    }

    public void testGetEmptyMetricList() throws Exception {

        MetricDataApi dataApi = getApi().getMetricDataApi();

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);

        List<Metric> metrics = new ArrayList<Metric>();

        MetricsDataResponse dataResponse = dataApi.getData(metrics, start, end);
        hqAssertFailureInvalidParameters(dataResponse);
    }

    public void testGetInvalidRange() throws Exception {

        MetricApi api = getApi().getMetricApi();
        MetricDataApi dataApi = getApi().getMetricDataApi();

        Resource platform = getLocalPlatformResource(false, false);
        MetricsResponse metricsResponse = api.getMetrics(platform, true);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                   metricsResponse.getMetric().size() > 0);

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricsDataResponse dataResponse = dataApi.getData(metricsResponse.getMetric(),
                                                           end, start);
        hqAssertFailureInvalidParameters(dataResponse);
    }
}
