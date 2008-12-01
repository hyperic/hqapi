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
        assertTrue(t.getCategory().length() > 0);
    }

    public void testMetricTemplate() throws Exception {

        Resource r = getResource();

        MetricApi api = getApi().getMetricApi();
        ListMetricsResponse resp = api.listEnabledMetrics(r);
        hqAssertSuccess(resp);

        Metric normalMetric = null;
        Metric availabilityMetric = null;

        for (Metric m : resp.getMetric()) {
            if (m.getMetricTemplate().getCategory().equals("AVAILABILITY")) {
                availabilityMetric = m;
            } else {
                normalMetric = m;
            }
        }

        assertNotNull("Unable to find metric for resource " + r.getId(),
                      normalMetric);
        assertNotNull("Unable to find availability metric for " +
                      "resource " + r.getId(), availabilityMetric);

        // Test setting indicator for a normal metric
        boolean isIndicator = normalMetric.isIndicator();

        SetMetricDefaultIndicatorResponse indicatorResponse =
                api.setDefaultIndicator(normalMetric.getMetricTemplate(),
                                        !isIndicator);
        hqAssertSuccess(indicatorResponse);

        GetMetricResponse getResponse = api.getMetric(normalMetric.getId());
        hqAssertSuccess(getResponse);
        Metric m = getResponse.getMetric();

        assertTrue("Indicator not set correctly expected=" + !isIndicator +
                   " was=" + m.getMetricTemplate().isIndicator(),
                   m.getMetricTemplate().isIndicator() == !isIndicator);

        // Reset back to original value
        indicatorResponse = api.setDefaultIndicator(normalMetric.getMetricTemplate(),
                                                    isIndicator);
        hqAssertSuccess(indicatorResponse);
        getResponse = api.getMetric(normalMetric.getId());
        hqAssertSuccess(getResponse);
        m = getResponse.getMetric();
        assertTrue("Indicator not set correctly expected=" + isIndicator +
                   " was=" + m.getMetricTemplate().isIndicator(),
                   m.getMetricTemplate().isIndicator() == isIndicator);

        // Test setting indicator for availability measuremnt
        SetMetricDefaultIndicatorResponse availResponse =
                api.setDefaultIndicator(availabilityMetric.getMetricTemplate(),
                                        false);
        hqAssertFailureInvalidParameters(availResponse);

        // Test setting indicator for a bad id
        MetricTemplate badTemplate = new MetricTemplate();
        badTemplate.setId(Integer.MAX_VALUE);
        SetMetricDefaultIndicatorResponse badTemplateResponse =
                api.setDefaultIndicator(badTemplate, true);
        hqAssertFailureObjectNotFound(badTemplateResponse);

        // Test setting the default interval
        long interval = normalMetric.getInterval();
        long newInterval = interval * 2;

        SetMetricDefaultIntervalResponse intervalResponse =
                api.setDefaultInterval(normalMetric.getMetricTemplate(),
                                       newInterval);       
        hqAssertSuccess(intervalResponse);

        getResponse = api.getMetric(m.getId());
        hqAssertSuccess(getResponse);
        m = getResponse.getMetric();
        assertTrue("Interval not set correctly expected=" + newInterval +
                   " was=" + m.getMetricTemplate().getDefaultInterval(),
                   m.getMetricTemplate().getDefaultInterval() == newInterval);

        // Test resetting back to original value
        intervalResponse = api.setDefaultInterval(normalMetric.getMetricTemplate(),
                                                  interval);
        hqAssertSuccess(intervalResponse);
        getResponse = api.getMetric(m.getId());
        hqAssertSuccess(getResponse);
        m = getResponse.getMetric();
        assertTrue("Interval not set correctly expected=" + interval +
                   " was=" + m.getMetricTemplate().getDefaultInterval(),
                   m.getMetricTemplate().getDefaultInterval() == interval);


        SetMetricDefaultIntervalResponse badIntervalResponse =
                api.setDefaultInterval(badTemplate, interval);
        hqAssertFailureObjectNotFound(badIntervalResponse);

        // Test setting default on

        boolean defaultOn = normalMetric.isDefaultOn();
        SetMetricDefaultOnResponse defaultOnResponse =
                api.setDefaultOn(normalMetric.getMetricTemplate(), !defaultOn);
        hqAssertSuccess(defaultOnResponse);

        getResponse = api.getMetric(m.getId());
        hqAssertSuccess(getResponse);
        m = getResponse.getMetric();

        assertTrue("Default on not set correctly expected=" + !defaultOn +
                   " was=" + m.getMetricTemplate().isDefaultOn(),
                   m.getMetricTemplate().isDefaultOn() == !defaultOn);

        // Reset default on flag
        defaultOnResponse = api.setDefaultOn(normalMetric.getMetricTemplate(),
                                             defaultOn);
        hqAssertSuccess(defaultOnResponse);
        getResponse = api.getMetric(m.getId());
        hqAssertSuccess(getResponse);
        m = getResponse.getMetric();
        assertTrue("Default on not set correctly expected=" + defaultOn +
                   " was=" + m.getMetricTemplate().isDefaultOn(),
                   m.getMetricTemplate().isDefaultOn() == defaultOn);

        // Test setting defaultOn for a bad template
        SetMetricDefaultOnResponse badDefaultOnResponse =
                api.setDefaultOn(badTemplate, true);
        hqAssertFailureObjectNotFound(badDefaultOnResponse);
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
