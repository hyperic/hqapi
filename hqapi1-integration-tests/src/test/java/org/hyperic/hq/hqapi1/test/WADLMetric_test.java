package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

import java.util.ArrayList;

/**
MetricGetDataHqu
MetricGetGroupDataHqu
MetricGetMetricHqu
MetricGetMetricsHqu
MetricGetMetricTemplateHqu
MetricGetResourceDataHqu
MetricGetTemplatesHqu
MetricSyncMetricsHqu
MetricSyncTemplatesHqu
 */

public class WADLMetric_test extends WADLTestBase {

    public void testGetMetricTemplates() throws Exception {

        HttpLocalhost8080HquHqapi1.MetricGetTemplatesHqu getTemplates =
                new HttpLocalhost8080HquHqapi1.MetricGetTemplatesHqu();
        HttpLocalhost8080HquHqapi1.MetricGetMetricTemplateHqu getTemplate =
                new HttpLocalhost8080HquHqapi1.MetricGetMetricTemplateHqu();

        MetricTemplatesResponse response = getTemplates.getAsMetricTemplatesResponse("CPU");
        hqAssertSuccess(response);
        assertTrue("No templates found!", response.getMetricTemplate().size() > 0);

        MetricTemplate t = response.getMetricTemplate().get(0);
        MetricTemplateResponse templateResponse =
                getTemplate.getAsMetricTemplateResponse(t.getId());
        hqAssertSuccess(templateResponse);
    }

    public void testGetMetrics() throws Exception {

        HttpLocalhost8080HquHqapi1.MetricGetMetricHqu getMetric =
                new HttpLocalhost8080HquHqapi1.MetricGetMetricHqu();
        HttpLocalhost8080HquHqapi1.MetricGetMetricsHqu getMetrics =
                new HttpLocalhost8080HquHqapi1.MetricGetMetricsHqu();
        HttpLocalhost8080HquHqapi1.ResourceFindHqu resourceFind =
                new HttpLocalhost8080HquHqapi1.ResourceFindHqu();
        HttpLocalhost8080HquHqapi1.MetricGetResourceDataHqu getResourceData =
                new HttpLocalhost8080HquHqapi1.MetricGetResourceDataHqu();
        HttpLocalhost8080HquHqapi1.MetricGetDataHqu getMetricData =
                new HttpLocalhost8080HquHqapi1.MetricGetDataHqu();

        ResourcesResponse resources =
                resourceFind.getAsResourcesResponse(null, "CPU", null, false, false);
        hqAssertSuccess(resources);
        assertTrue("No resources found", resources.getResource().size() > 0);

        Resource r = resources.getResource().get(0);

        MetricsResponse allMetrics = getMetrics.getAsMetricsResponse(r.getId());
        hqAssertSuccess(allMetrics);
        assertTrue("No metrics found for " + r.getName(),
                   allMetrics.getMetric().size() > 0);

        MetricsResponse enabledMetrics = getMetrics.getAsMetricsResponse(r.getId(),
                                                                         true);
        hqAssertSuccess(enabledMetrics);
        assertTrue("All metrics size not >= enabled metrics size",
                   allMetrics.getMetric().size() >= enabledMetrics.getMetric().size());


        long end = System.currentTimeMillis();
        long start = end - (60000 * 60); // 1 hour
        Metric m = enabledMetrics.getMetric().get(0);
        MetricDataResponse metricData =
                getMetricData.getAsMetricDataResponse(m.getId(), start, end);
        hqAssertSuccess(metricData);
        assertNotNull("No metric data found", metricData.getMetricData());

        ArrayList<Integer> resourceIds = new ArrayList<Integer>();
        resourceIds.add(r.getId());
        MetricTemplate t = m.getMetricTemplate();

        MetricsDataResponse metricsData =
                getResourceData.getAsMetricsDataResponse(resourceIds, t.getId(),
                                                         start, end);
        hqAssertSuccess(metricsData);
        assertTrue("Didn't find a single MetricData for resource data query",
                   metricsData.getMetricData().size() == 1);

        MetricResponse metricResponse = getMetric.getAsMetricResponse(m.getId());
        hqAssertSuccess(metricResponse);
    }
}
