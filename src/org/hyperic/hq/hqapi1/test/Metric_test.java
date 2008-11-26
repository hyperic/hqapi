package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.types.DisableMetricResponse;
import org.hyperic.hq.hqapi1.types.EnableMetricResponse;
import org.hyperic.hq.hqapi1.types.GetMetricResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.SetMetricIntervalResponse;
import org.hyperic.hq.hqapi1.types.ListMetricsResponse;

public class Metric_test extends MetricTestBase {

    private Resource _r;

    public Metric_test(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        _r = getResource();
    }

    protected void validateMetric(Metric m) throws Exception {
        assertNotNull(m);
        assertTrue(m.getId() > 0);
        assertNotNull(m.getInterval());
        assertNotNull(m.isEnabled());
        assertTrue(m.getName().length() > 0);
        assertNotNull(m.isDefaultOn());
        assertNotNull(m.isIndicator());
        assertTrue(m.getCollectionType().length() > 0);
    }
        
    public void testListMetrics() throws Exception {

        MetricApi api = getApi().getMetricApi();
        ListMetricsResponse resp = api.listMetrics(_r);
        hqAssertSuccess(resp);

        int numNotDefaultOn = 0;
        for (Metric m : resp.getMetric()) {
            validateMetric(m);
            if (!m.isDefaultOn()) {
                numNotDefaultOn++;
            }
        }
        assertTrue("All metrics are defaultOn", numNotDefaultOn > 0);
    }

    public void testListEnabledMetrics() throws Exception {

        MetricApi api = getApi().getMetricApi();
        ListMetricsResponse resp = api.listEnabledMetrics(_r);
        hqAssertSuccess(resp);

        for (Metric m : resp.getMetric()) {
            validateMetric(m);
            assertTrue("Metric " + m.getName() + " is not enabled",
                       m.isEnabled());
        }
    }

    public void testMetricListBadResourceId() throws Exception {
        
        MetricApi api = getApi().getMetricApi();
        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);
        ListMetricsResponse resp = api.listMetrics(r);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testMetricById() throws Exception {

        MetricApi api = getApi().getMetricApi();
        ListMetricsResponse resp = api.listMetrics(_r);
        hqAssertSuccess(resp);

        assertFalse("Resource " + _r.getName() + " has no metrics",
                   resp.getMetric().size() == 0);

        Metric m = resp.getMetric().get(0);
        GetMetricResponse metricResponse = api.getMetric(m.getId());
        hqAssertSuccess(metricResponse);
        validateMetric(metricResponse.getMetric());  
    }

    public void testMetricByBadId() throws Exception {

        MetricApi api = getApi().getMetricApi();
        GetMetricResponse metricResponse = api.getMetric(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(metricResponse);
    }

    public void testMetricDisableEnable() throws Exception {

        MetricApi api = getApi().getMetricApi();
        ListMetricsResponse resp = api.listMetrics(_r);
        hqAssertSuccess(resp);

        assertFalse("Resource " + _r.getName() + " has no metrics",
                   resp.getMetric().size() == 0);

        Metric m = resp.getMetric().get(0);

        // Disable
        DisableMetricResponse disableMetricResponse = api.disableMetric(m);
        hqAssertSuccess(disableMetricResponse);

        // Verify
        GetMetricResponse metricResponse = api.getMetric(m.getId());
        hqAssertSuccess(metricResponse);
        assertFalse("Metric id " + m.getId() + " not disabled",
                     metricResponse.getMetric().isEnabled());

        // Enable
        EnableMetricResponse enableResponse =
                api.enableMetric(m, m.getMetricTemplate().getDefaultInterval());
        hqAssertSuccess(enableResponse);

        // Verify
        metricResponse = api.getMetric(m.getId());
        hqAssertSuccess(metricResponse);
        assertTrue("Metric id " + m.getId() + " not enabled",
                   metricResponse.getMetric().isEnabled());
    }

    public void testMetricDisableBadId() throws Exception {

        MetricApi api = getApi().getMetricApi();
        Metric m = new Metric();
        m.setId(Integer.MAX_VALUE);
        DisableMetricResponse disableMetricResponse = api.disableMetric(m);
        hqAssertFailureObjectNotFound(disableMetricResponse);
    }

    public void testMetricEnableBadId() throws Exception {

        MetricApi api = getApi().getMetricApi();
        Metric m = new Metric();
        m.setId(Integer.MAX_VALUE);
        EnableMetricResponse enableResponse = api.enableMetric(m, 60000);
        hqAssertFailureObjectNotFound(enableResponse);
    }

    public void testMetricSetInterval() throws Exception {

        MetricApi api = getApi().getMetricApi();
        ListMetricsResponse resp = api.listMetrics(_r);
        hqAssertSuccess(resp);
        assertFalse("Resource " + _r.getName() + " has no metrics",
                   resp.getMetric().size() == 0);

        Metric m = resp.getMetric().get(0);

        final long INTERVAL = 60000;

        // Set new interval
        SetMetricIntervalResponse intervalResp = api.setInterval(m, INTERVAL);
        hqAssertSuccess(intervalResp);


        // Validate
        GetMetricResponse metricResponse = api.getMetric(m.getId());
        hqAssertSuccess(metricResponse);
        assertEquals(INTERVAL, metricResponse.getMetric().getInterval());

        // Reset
        intervalResp = api.setInterval(m, m.getMetricTemplate().getDefaultInterval());
        hqAssertSuccess(intervalResp);

        // Validate
        metricResponse = api.getMetric(m.getId());
        hqAssertSuccess(metricResponse);
        assertEquals(metricResponse.getMetric().getInterval(),
                     metricResponse.getMetric().getMetricTemplate().getDefaultInterval());
    }

    public void testMetricSetIntervalBadId() throws Exception {

        MetricApi api = getApi().getMetricApi();
        Metric m = new Metric();
        m.setId(Integer.MAX_VALUE);
        SetMetricIntervalResponse intervalResp = api.setInterval(m, 60000);
        hqAssertFailureObjectNotFound(intervalResp);
    }

    public void testMetricSetInvalidInterval() throws Exception {

        MetricApi api = getApi().getMetricApi();
        ListMetricsResponse resp = api.listMetrics(_r);
        hqAssertSuccess(resp);
        assertFalse("Resource " + _r.getName() + " has no metrics",
                   resp.getMetric().size() == 0);
        
        Metric m = resp.getMetric().get(0);

        final long[] BAD_INTERVALS = { -1, 0, 1, 60, 60001 };

        for (long interval : BAD_INTERVALS) {
            SetMetricIntervalResponse intervalResponse =
                    api.setInterval(m, interval);
            hqAssertFailureInvalidParameters(intervalResponse);
        }
    }
}
