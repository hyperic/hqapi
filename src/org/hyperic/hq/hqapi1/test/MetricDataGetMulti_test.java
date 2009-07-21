package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.MetricsDataResponse;
import org.hyperic.hq.hqapi1.types.MetricData;

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

        int[] mids = new int[metricsResponse.getMetric().size()];
        for (int i = 0; i < metricsResponse.getMetric().size(); i++) {
            mids[i] = metricsResponse.getMetric().get(i).getId();
        }

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricsDataResponse dataResponse = dataApi.getData(mids, start, end);
        hqAssertSuccess(dataResponse);

        for (MetricData metricData : dataResponse.getMetricData()) {
            validateMetricData(metricData);
        }
    }

    public void testGetInvalidMetricId() throws Exception {

        MetricDataApi dataApi = getApi().getMetricDataApi();

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);

        int[] mids = { Integer.MAX_VALUE };

        MetricsDataResponse dataResponse = dataApi.getData(mids, start, end);
        hqAssertFailureObjectNotFound(dataResponse);
    }

    public void testGetEmptyMetricArray() throws Exception {

        MetricDataApi dataApi = getApi().getMetricDataApi();

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);

        int[] mids = {};

        MetricsDataResponse dataResponse = dataApi.getData(mids, start, end);
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

        int[] mids = new int[metricsResponse.getMetric().size()];
        for (int i = 0; i < metricsResponse.getMetric().size(); i++) {
            mids[i] = metricsResponse.getMetric().get(i).getId();
        }

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricsDataResponse dataResponse = dataApi.getData(mids, end, start);
        hqAssertFailureInvalidParameters(dataResponse);
    }
}
