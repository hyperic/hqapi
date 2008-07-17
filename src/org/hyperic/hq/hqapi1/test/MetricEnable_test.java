package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.types.DisableMetricResponse;
import org.hyperic.hq.hqapi1.types.EnableMetricResponse;
import org.hyperic.hq.hqapi1.types.GetMetricResponse;
import org.hyperic.hq.hqapi1.types.Metric;

public class MetricEnable_test extends HQApiTestBase {
    
    protected Integer _metricId = 10101;

    public MetricEnable_test(String name) {
        super(name);
    }
    
    public void testEnableMetric() throws Exception {
        /**
        MetricApi api = getApi().getMetricApi();
        EnableMetricResponse eResp = api.enableMetric(_metricId, 10l);
        hqAssertSuccess(eResp);

        GetMetricResponse gResp = api.getMetric(_metricId);
        hqAssertSuccess(gResp);
        Metric metric = gResp.getMetric();
        assertTrue(metric.isEnabled());

        DisableMetricResponse dResp = api.disableMetric(_metricId);
        hqAssertSuccess(dResp);

        gResp = api.getMetric(_metricId);
        hqAssertSuccess(gResp);
        metric = gResp.getMetric();
        assertTrue(!metric.isEnabled());
        **/
    }

}
