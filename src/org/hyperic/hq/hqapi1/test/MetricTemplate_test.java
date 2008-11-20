package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ListMetricsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.MetricTemplate;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultIndicatorResponse;
import org.hyperic.hq.hqapi1.types.GetMetricResponse;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultIntervalResponse;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultOnResponse;
import org.hyperic.hq.hqapi1.MetricApi;

public class MetricTemplate_test extends MetricTestBase {

    Metric _m;
    
    public MetricTemplate_test(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        
        Resource r = getResource();

        MetricApi api = getApi().getMetricApi();
        ListMetricsResponse resp = api.listMetrics(r);
        hqAssertSuccess(resp);

        assertTrue("No metrics found for " + r.getName(),
                   resp.getMetric().size() > 0);
        _m = resp.getMetric().get(0);
    }

    public void testSetDefaultIndicator() throws Exception {

        if (_m == null) {
            getLog().error("Unable to find local platform Metric, skipping test");
            return;
        }
        MetricTemplate t = _m.getMetricTemplate();
        boolean isIndicator = t.isIndicator();

        MetricApi api = getApi().getMetricApi();
        SetMetricDefaultIndicatorResponse indicatorResponse =
                api.setDefaultIndicator(t, !isIndicator);
        hqAssertSuccess(indicatorResponse);

        GetMetricResponse getResponse = api.getMetric(_m.getId());
        hqAssertSuccess(getResponse);
        Metric m = getResponse.getMetric();

        // TODO: Broken!
        //assertTrue("Indicator not set correctly expected=" + !isIndicator +
        //           " was=" + m.getMetricTemplate().isIndicator(),
        //           m.getMetricTemplate().isIndicator() == !isIndicator);

        // Reset back to original value
        indicatorResponse = api.setDefaultIndicator(t, isIndicator);
        hqAssertSuccess(indicatorResponse);
        getResponse = api.getMetric(_m.getId());
        hqAssertSuccess(getResponse);
        m = getResponse.getMetric();
        // TODO: Broken!
        //assertTrue("Indicator not set correctly expected=" + isIndicator +
        //           " was=" + m.getMetricTemplate().isIndicator(),
        //           m.getMetricTemplate().isIndicator() == isIndicator);
    }

    public void testSetDefaultInterval() throws Exception {

        if (_m == null) {
            getLog().error("Unable to find local platform Metric, skipping test");
            return;
        }

        MetricTemplate t = _m.getMetricTemplate();
        long interval = t.getDefaultInterval();
        long newInterval = interval * 2;

        MetricApi api = getApi().getMetricApi();
        SetMetricDefaultIntervalResponse intervalResponse =
                api.setDefaultInterval(t, newInterval);
        hqAssertSuccess(intervalResponse);

        GetMetricResponse getResponse = api.getMetric(_m.getId());
        hqAssertSuccess(getResponse);
        Metric m = getResponse.getMetric();

        assertTrue("Interval not set correctly expected=" + newInterval +
                   " was=" + m.getMetricTemplate().getDefaultInterval(),
                   m.getMetricTemplate().getDefaultInterval() == newInterval);

        // Reset back to original value
        intervalResponse = api.setDefaultInterval(t, interval);
        hqAssertSuccess(intervalResponse);
        getResponse = api.getMetric(_m.getId());
        hqAssertSuccess(getResponse);
        m = getResponse.getMetric();
        assertTrue("Interval not set correctly expected=" + interval +
                   " was=" + m.getMetricTemplate().getDefaultInterval(),
                   m.getMetricTemplate().getDefaultInterval() == interval);
    }

    public void testSetDefaultOn() throws Exception {


        if (_m == null) {
            getLog().error("Unable to find local platform Metric, skipping test");
            return;
        }

        MetricTemplate t = _m.getMetricTemplate();
        boolean defaultOn = t.isDefaultOn();

        MetricApi api = getApi().getMetricApi();
        SetMetricDefaultOnResponse defaultOnResponse =
                api.setDefaultOn(t, !defaultOn);
        hqAssertSuccess(defaultOnResponse);

        GetMetricResponse getResponse = api.getMetric(_m.getId());
        hqAssertSuccess(getResponse);
        Metric m = getResponse.getMetric();

        assertTrue("Default on not set correctly expected=" + !defaultOn +
                   " was=" + m.getMetricTemplate().isDefaultOn(),
                   m.getMetricTemplate().isDefaultOn() == !defaultOn);

        // Reset back to original value
        defaultOnResponse = api.setDefaultOn(t, defaultOn);
        hqAssertSuccess(defaultOnResponse);
        getResponse = api.getMetric(_m.getId());
        hqAssertSuccess(getResponse);
        m = getResponse.getMetric();
        assertTrue("Default on not set correctly expected=" + defaultOn +
                   " was=" + m.getMetricTemplate().isDefaultOn(),
                   m.getMetricTemplate().isDefaultOn() == defaultOn);
    }
}
