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
import java.util.Iterator;

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

        LastMetricsDataResponse dataResponse =
                dataApi.getData(metricsResponse.getMetric());
        hqAssertSuccess(dataResponse);

        for (LastMetricData metricData : dataResponse.getLastMetricData()) {
            validateLastMetricData(metricData);
        }
    }

    public void testGetInvalidMetricId() throws Exception {

        MetricDataApi dataApi = getApi().getMetricDataApi();

        List<Metric> metrics = new ArrayList<Metric>();
        Metric m = new Metric();
        m.setId(Integer.MAX_VALUE);
        metrics.add(m);

        LastMetricsDataResponse dataResponse = dataApi.getData(metrics);
        hqAssertFailureObjectNotFound(dataResponse);
    }

    public void testGetEmptyMetricList() throws Exception {

        MetricDataApi dataApi = getApi().getMetricDataApi();

        List<Metric> metrics = new ArrayList<Metric>();

        LastMetricsDataResponse dataResponse = dataApi.getData(metrics);
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

        List<Metric> disabledMetrics = metricsResponse.getMetric();
        for (Iterator<Metric> i = disabledMetrics.iterator(); i.hasNext();) {
            Metric m = i.next();
            if (m.isEnabled()) {
                i.remove();
            }
        }

        assertTrue("No disabled metrics could be found", disabledMetrics.size() > 0);

        LastMetricsDataResponse dataResponse = dataApi.getData(disabledMetrics);
        hqAssertSuccess(dataResponse);
        
        // disabled metrics could have data if it was previously enabled
        int lastMetricNoDataCount = 0;
        
        // TODO: What is the correct behavior of the API if the last data point
        //       could not be found? Return an error for simply null?
        for (LastMetricData d : dataResponse.getLastMetricData()) {
            if (d.getDataPoint() == null) {
                lastMetricNoDataCount++;
            }
        }
        
        assertTrue("No disabled metrics with no data could be found",
                    lastMetricNoDataCount > 0);
    }
}
