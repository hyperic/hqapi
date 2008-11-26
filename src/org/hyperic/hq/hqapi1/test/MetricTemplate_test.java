package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ListMetricsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.MetricTemplate;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultIndicatorResponse;
import org.hyperic.hq.hqapi1.types.GetMetricResponse;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultIntervalResponse;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultOnResponse;
import org.hyperic.hq.hqapi1.types.GetResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ListMetricTemplatesResponse;
import org.hyperic.hq.hqapi1.types.GetMetricTemplateResponse;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.HQApi;

import java.util.List;

public class MetricTemplate_test extends MetricTestBase {

    Metric _m;
    
    public MetricTemplate_test(String name) {
        super(name);
    }

    private void validateTemplate(MetricTemplate t) {
        assertTrue(t.getId() > 0);
        assertTrue(t.getName().length() > 0);
        assertTrue(t.getAlias().length() > 0);
        assertTrue(t.getCollectionType().length() > 0);
        assertTrue(t.getPlugin().length() > 0);
        assertTrue(t.getDefaultInterval() > 0);
        assertTrue(t.getUnits().length() > 0);
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

        MetricTemplate t = _m.getMetricTemplate();
        boolean isIndicator = t.isIndicator();

        MetricApi api = getApi().getMetricApi();
        SetMetricDefaultIndicatorResponse indicatorResponse =
                api.setDefaultIndicator(t, !isIndicator);
        hqAssertSuccess(indicatorResponse);

        GetMetricResponse getResponse = api.getMetric(_m.getId());
        hqAssertSuccess(getResponse);
        Metric m = getResponse.getMetric();

        assertTrue("Indicator not set correctly expected=" + !isIndicator +
                   " was=" + m.getMetricTemplate().isIndicator(),
                   m.getMetricTemplate().isIndicator() == !isIndicator);

        // Reset back to original value
        indicatorResponse = api.setDefaultIndicator(t, isIndicator);
        hqAssertSuccess(indicatorResponse);
        getResponse = api.getMetric(_m.getId());
        hqAssertSuccess(getResponse);
        m = getResponse.getMetric();
        assertTrue("Indicator not set correctly expected=" + isIndicator +
                   " was=" + m.getMetricTemplate().isIndicator(),
                   m.getMetricTemplate().isIndicator() == isIndicator);
    }

    public void testSetDefaultIndicatorBadId() throws Exception {

        MetricTemplate t = new MetricTemplate();
        t.setId(Integer.MAX_VALUE);
        
        MetricApi api = getApi().getMetricApi();
        SetMetricDefaultIndicatorResponse indicatorResponse =
                api.setDefaultIndicator(t, true);
        hqAssertFailureObjectNotFound(indicatorResponse);
    }

    public void testSetDefaultInterval() throws Exception {

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

    public void testSetDefaultIntervalBadId() throws Exception {

        MetricTemplate t = new MetricTemplate();
        t.setId(Integer.MAX_VALUE);

        MetricApi api = getApi().getMetricApi();
        SetMetricDefaultIntervalResponse intervalResponse =
                api.setDefaultInterval(t, 60000);
        hqAssertFailureObjectNotFound(intervalResponse);
    }

    public void testSetDefaultOn() throws Exception {

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

    public void testSetDefaultOnBadId() throws Exception {

        MetricTemplate t = new MetricTemplate();
        t.setId(Integer.MAX_VALUE);

        MetricApi api = getApi().getMetricApi();
        SetMetricDefaultOnResponse defaultOnResponse =
                api.setDefaultOn(t, true);
        hqAssertFailureObjectNotFound(defaultOnResponse);
    }

    public void testListTemplates() throws Exception {

        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();

        final String TYPE = "Linux";
        GetResourcePrototypeResponse response = resourceApi.getResourcePrototype(TYPE);
        hqAssertSuccess(response);

        ResourcePrototype pt = response.getResourcePrototype();

        MetricApi metricApi = api.getMetricApi();
        ListMetricTemplatesResponse metricTemplates =
                metricApi.listMetricTemplates(pt);
        hqAssertSuccess(metricTemplates);

        List<MetricTemplate> templates = metricTemplates.getMetricTemplate();
        assertTrue("No metrics found for type " + pt.getName(),
                   templates.size() > 0);
        for (MetricTemplate t : metricTemplates.getMetricTemplate()) {
            validateTemplate(t);
        }
    }

    public void testListTemplatesEmptyPrototype() throws Exception {

        MetricApi api = getApi().getMetricApi();

        ResourcePrototype pt = new ResourcePrototype();

        ListMetricTemplatesResponse response = api.listMetricTemplates(pt);
        hqAssertFailureInvalidParameters(response);
    }

    public void testListTemplatesInvalidPrototype() throws Exception {

        MetricApi api = getApi().getMetricApi();

        ResourcePrototype pt = new ResourcePrototype();
        pt.setName("Non-existant resource prototype");
        ListMetricTemplatesResponse response = api.listMetricTemplates(pt);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetTemplate() throws Exception {

        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();

        final String TYPE = "Linux";
        GetResourcePrototypeResponse response = resourceApi.getResourcePrototype(TYPE);
        hqAssertSuccess(response);

        ResourcePrototype pt = response.getResourcePrototype();

        MetricApi metricApi = api.getMetricApi();
        ListMetricTemplatesResponse metricTemplates =
                metricApi.listMetricTemplates(pt);
        hqAssertSuccess(metricTemplates);

        List<MetricTemplate> templates = metricTemplates.getMetricTemplate();
        assertTrue("No metrics found for type " + pt.getName(),
                   templates.size() > 0);

        MetricTemplate t = templates.get(0);

        GetMetricTemplateResponse getResponse = metricApi.getMetricTemplate(t.getId());
        hqAssertSuccess(getResponse);
        validateTemplate(getResponse.getMetricTemplate());
    }

    public void testGetTemplateInvalidId() throws Exception {

        MetricApi api = getApi().getMetricApi();

        GetMetricTemplateResponse getResponse = api.getMetricTemplate(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(getResponse);
    }
}
