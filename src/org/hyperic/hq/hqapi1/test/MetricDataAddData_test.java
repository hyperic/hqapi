package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.DataPoint;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class MetricDataAddData_test extends MetricDataTestBase {

    public MetricDataAddData_test(String name) {
        super(name);
    }

    public void testAddData() throws Exception {

        MetricApi api = getApi().getMetricApi();
        MetricDataApi dataApi = getApi().getMetricDataApi();

        Resource platform = getLocalPlatformResource(false, false);
        MetricsResponse metricsResponse = api.getMetrics(platform, true);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                   metricsResponse.getMetric().size() > 0);

        Metric m = metricsResponse.getMetric().get(0);

        List<DataPoint> dps = new ArrayList<DataPoint>();
        Random r = new Random();
        for (int i = 1; i < 100; i++) {
            DataPoint dp = new DataPoint();
            dp.setTimestamp(i);
            dp.setValue(r.nextDouble());
            dps.add(dp);
        }

        StatusResponse response = dataApi.addData(m, dps);
        hqAssertFailureNotImplemented(response);
    }
}
