package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.MetricsDataResponse;
import org.hyperic.hq.hqapi1.types.MetricData;
import org.hyperic.hq.hqapi1.types.LastMetricsDataResponse;
import org.hyperic.hq.hqapi1.types.LastMetricData;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.LastMetricDataResponse;

import java.util.ArrayList;
import java.util.List;

public class MetricDataGetLastMulti_test extends MetricDataTestBase {

    public MetricDataGetLastMulti_test(String name) {
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

        LastMetricsDataResponse dataResponse = dataApi.getData(mids);
        hqAssertSuccess(dataResponse);

        for (LastMetricData metricData : dataResponse.getLastMetricData()) {
            validateLastMetricData(metricData);
        }
    }

    public void testGetInvalidMetricId() throws Exception {

        MetricDataApi dataApi = getApi().getMetricDataApi();

        int[] mids = { Integer.MAX_VALUE };

        LastMetricsDataResponse dataResponse = dataApi.getData(mids);
        hqAssertFailureObjectNotFound(dataResponse);
    }

    public void testGetEmptyMetricArray() throws Exception {

        MetricDataApi dataApi = getApi().getMetricDataApi();

        int[] mids = {};

        LastMetricsDataResponse dataResponse = dataApi.getData(mids);
        hqAssertFailureInvalidParameters(dataResponse);
    }

    public void testGetLastNoData() throws Exception {

        MetricApi api = getApi().getMetricApi();
        MetricDataApi dataApi = getApi().getMetricDataApi();

        Resource platform = getLocalPlatformResource(false, false);
        MetricsResponse metricsResponse = api.getMetrics(platform, false);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                   metricsResponse.getMetric().size() > 0);

        List<Metric> disabledMetrics = new ArrayList<Metric>();
        for (Metric m : metricsResponse.getMetric()) {
            if (!m.isEnabled()) {
                disabledMetrics.add(m);
            }
        }

        assertTrue("No disabled metrics could be found", disabledMetrics.size() > 0);

        int[] mids = new int[disabledMetrics.size()];
        for (int i = 0; i < disabledMetrics.size(); i++) {
            mids[i] = disabledMetrics.get(i).getId();
        }

        LastMetricsDataResponse dataResponse = dataApi.getData(mids);
        hqAssertSuccess(dataResponse);
        // TODO: What is the correct behavior of the API if the last data point
        //       could not be found? Return an error for simply null?
        for (LastMetricData d : dataResponse.getLastMetricData()) {
            assertNull("Metric datapoint not null", d.getDataPoint());
        }
    }
}
