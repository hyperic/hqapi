package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.types.*;

public class Metric_test extends MetricTestBase {

    public Metric_test(String name) {
        super(name);
    }

    public void testMetricList() throws Exception {

        Resource r = getResource();
        if (r == null) {
            getLog().error("Unable to find the local platform, skipping test");
            return;
        }

        MetricApi api = getApi().getMetricApi();
        ListMetricResponse resp = api.listMetrics(r);
        hqAssertSuccess(resp);

        for (Metric m : resp.getMetric()) {
            validateMetric(m);
        }
    }

    public void testMetricById() throws Exception {

        Resource r = getResource();
        if (r == null) {
            getLog().error("Unable to find the local platform, skipping test");
            return;
        }

        MetricApi api = getApi().getMetricApi();
        ListMetricResponse resp = api.listMetrics(r);
        hqAssertSuccess(resp);

        for (Metric m : resp.getMetric()) {
            GetMetricResponse metricResponse = api.getMetric(m.getId());
            hqAssertSuccess(metricResponse);
            validateMetric(metricResponse.getMetric());  
        }
    }

    public void testMetricDisableEnable() throws Exception {
        Resource r = getResource();
        if (r == null) {
            getLog().error("Unable to find the local platform, skipping test");
            return;
        }

        MetricApi api = getApi().getMetricApi();
        ListMetricResponse resp = api.listMetrics(r);
        hqAssertSuccess(resp);

        // Disable all
        for (Metric m : resp.getMetric()) {
            DisableMetricResponse disableMetricResponse = api.disableMetric(m);
            hqAssertSuccess(disableMetricResponse);
        }

        // Verify
        for (Metric m : resp.getMetric()) {
            GetMetricResponse metricResponse = api.getMetric(m.getId());
            hqAssertSuccess(metricResponse);
            assertFalse("Metric id " + m.getId() + " not disabled",
                        metricResponse.getMetric().isEnabled());
        }

        // Enable all
        for (Metric m : resp.getMetric()) {
            EnableMetricResponse enableResponse =
                    api.enableMetric(m, m.getMetricTemplate().getDefaultInterval());
            hqAssertSuccess(enableResponse);
        }

        for (Metric m : resp.getMetric()) {
            GetMetricResponse metricResponse = api.getMetric(m.getId());
            hqAssertSuccess(metricResponse);
            assertTrue("Metric id " + m.getId() + " not enabled",
                       metricResponse.getMetric().isEnabled());
        }
    }

    public void testMetricSetInterval() throws Exception {

        Resource r = getResource();
        if (r == null) {
            getLog().error("Unable to find the local platform, skipping test");
            return;
        }

        MetricApi api = getApi().getMetricApi();
        ListMetricResponse resp = api.listMetrics(r);
        hqAssertSuccess(resp);

        final long INTERVAL = 60000;
        // Set new interval
        for (Metric m : resp.getMetric()) {
            SetMetricIntervalResponse intervalResp = api.setInterval(m, INTERVAL);
            hqAssertSuccess(intervalResp);
        }

        // Validate
        for (Metric m : resp.getMetric()) {
            GetMetricResponse metricResponse = api.getMetric(m.getId());
            hqAssertSuccess(metricResponse);
            assertEquals(INTERVAL, metricResponse.getMetric().getInterval());
        }

        // Reset
        for (Metric m : resp.getMetric()) {
            SetMetricIntervalResponse intervalResponse =
                    api.setInterval(m, m.getMetricTemplate().getDefaultInterval());
            hqAssertSuccess(intervalResponse);
        }

        // Validate
        for (Metric m : resp.getMetric()) {
            GetMetricResponse metricResponse = api.getMetric(m.getId());
            hqAssertSuccess(metricResponse);
            assertEquals(metricResponse.getMetric().getInterval(),
                         metricResponse.getMetric().getMetricTemplate().getDefaultInterval());
        }
    }

    public void testMetricSetInvalidInterval() throws Exception {

        Resource r = getResource();
        if (r == null) {
            getLog().error("Unable to find the local platform, skipping test");
            return;
        }

        MetricApi api = getApi().getMetricApi();
        ListMetricResponse resp = api.listMetrics(r);
        hqAssertSuccess(resp);
        assertFalse("Resource " + r.getName() + " has no metrics",
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
