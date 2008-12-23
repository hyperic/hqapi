package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.MetricData;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.GroupsResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.MetricTemplate;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.DataPoint;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.MetricDataResponse;
import org.hyperic.hq.hqapi1.types.MetricTemplatesResponse;
import org.hyperic.hq.hqapi1.types.MetricsDataResponse;
import org.hyperic.hq.hqapi1.types.MetricTemplateResponse;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;

import java.util.List;

public class MetricData_test extends MetricTestBase {

    Resource _r;

    public MetricData_test(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        _r = getResource();
    }

    public void testGetEnabledMetricData() throws Exception {

        MetricApi api = getApi().getMetricApi();
        MetricsResponse resp = api.getEnabledMetrics(_r);
        hqAssertSuccess(resp);

        assertTrue("No enabled metrics found for " + _r.getName(),
                   resp.getMetric().size() > 0);
        Metric m = resp.getMetric().get(0);

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);

        MetricDataResponse dataResponse = api.getMetricData(m.getId(),
                                                               start, end);
        hqAssertSuccess(dataResponse);
        assertTrue("No metric data found for " + m.getName(),
                   dataResponse.getMetricData().getDataPoint().size() > 0);
        for (DataPoint d : dataResponse.getMetricData().getDataPoint()) {

            assertTrue("Metric point timestamp greater than end time. ts=" +
                       d.getTimestamp() + " end=" + end,
                       d.getTimestamp() <= end);
            assertTrue("Metric point timestamp less than start time ts=" +
                       d.getTimestamp() + " start=" + start,
                       d.getTimestamp() >= start);
            assertTrue("Metric value less than zero",
                       d.getValue() >= 0);
        }
    }

    public void testGetDisabledMetricData() throws Exception {

        MetricApi api = getApi().getMetricApi();
        MetricsResponse resp = api.getMetrics(_r);
        hqAssertSuccess(resp);

        assertTrue("No metrics found for " + _r.getName(),
                   resp.getMetric().size() > 0);

        Metric m = null;
        for (Metric metric : resp.getMetric()) {
            if (!metric.isEnabled()) {
                m = metric;
            }
        }
        assertNotNull("No disabled metric could be found", m);
        
        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);

        MetricDataResponse dataResponse = api.getMetricData(m.getId(),
                                                            start, end);
        hqAssertSuccess(dataResponse);

        assertTrue("Metric data found for " + m.getName() + " on " +
                   _r.getName() + " (" + dataResponse.getMetricData().getDataPoint().size() +
                   " datapoints)",
                   dataResponse.getMetricData().getDataPoint().size() == 0);
    }

    public void testGetMetricDataInvalidId() throws Exception {

        MetricApi api = getApi().getMetricApi();
        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricDataResponse dataResponse = api.getMetricData(Integer.MAX_VALUE,
                                                            start, end);
        hqAssertFailureObjectNotFound(dataResponse);
    }

    public void testGetMetricDataInvalidRange() throws Exception {

        MetricApi api = getApi().getMetricApi();
        MetricsResponse resp = api.getMetrics(_r);
        hqAssertSuccess(resp);

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);

        assertTrue("No metrics found for " + _r.getName(),
                   resp.getMetric().size() > 0);
        Metric m = resp.getMetric().get(0);

        // Test end > start.
        MetricDataResponse dataResponse = api.getMetricData(m.getId(),
                                                            end, start);
        hqAssertFailureInvalidParameters(dataResponse);
    }

    public void testGetMetricGroupData() throws Exception {

        HQApi api = getApi();

        GroupApi groupApi = api.getGroupApi();

        GroupsResponse groupsResponse = groupApi.getCompatibleGroups();
        hqAssertSuccess(groupsResponse);
        List<Group> compatGroups = groupsResponse.getGroup();
        assertTrue("No compatible groups found", compatGroups.size() > 0);
        Group g = compatGroups.get(0);

        ResourcesResponse resourcesResponse = groupApi.getResources(g.getId());
        hqAssertSuccess(resourcesResponse);

        ResourcePrototype pt = g.getResourcePrototype();
        MetricApi metricApi = api.getMetricApi();
        MetricTemplatesResponse templatesResponse = metricApi.getMetricTemplates(pt);
        hqAssertSuccess(templatesResponse);
        List<MetricTemplate> templates = templatesResponse.getMetricTemplate();
        assertTrue("No templates found for " + pt.getName(), templates.size() > 0);

        // Make sure the template we query is default-on
        MetricTemplate template = null;
        for (MetricTemplate t : templates) {
            if (t.isDefaultOn()) {
                template = t;
                break;
            }
        }

        if (template == null) {
            throw new Exception("Could not find default on template for " +
                                pt.getName());
        }

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);

        MetricsDataResponse response = metricApi.getMetricData(g.getId(),
                                                               template.getId(),
                                                               start, end);
        hqAssertSuccess(response);

        List<MetricData> metricData = response.getMetricData();
        assertTrue("Number of Resources in Group does not match the number " +
                   "of ResourceMetrics",
                   resourcesResponse.getResource().size() == metricData.size());

        for (MetricData m : metricData) {
            assertTrue(m.getMetricId() > 0);
            assertTrue(m.getMetricName().length() > 0);
            assertTrue(m.getResourceId() > 0);
            assertTrue(m.getResourceName().length() > 0);
            assertTrue(m.getDataPoint().size() > 0);
        }
    }

    public void testGetMetricGroupDataInvalidRange() throws Exception {

        HQApi api = getApi();

        GroupApi groupApi = api.getGroupApi();

        GroupsResponse groupsResponse = groupApi.getCompatibleGroups();
        hqAssertSuccess(groupsResponse);
        List<Group> compatGroups = groupsResponse.getGroup();
        assertTrue("No compatible groups found", compatGroups.size() > 0);
        Group g = compatGroups.get(0);

        ResourcesResponse resourcesResponse = groupApi.getResources(g.getId());
        hqAssertSuccess(resourcesResponse);

        ResourcePrototype pt = g.getResourcePrototype();
        MetricApi metricApi = api.getMetricApi();
        MetricTemplatesResponse templatesResponse = metricApi.getMetricTemplates(pt);
        hqAssertSuccess(templatesResponse);
        List<MetricTemplate> templates = templatesResponse.getMetricTemplate();
        assertTrue("No templates found for " + pt.getName(), templates.size() > 0);

        // Make sure the template we query is default-on
        MetricTemplate template = null;
        for (MetricTemplate t : templates) {
            if (t.isDefaultOn()) {
                template = t;
                break;
            }
        }

        if (template == null) {
            throw new Exception("Could not find default on template for " +
                                pt.getName());
        }

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        // Start < End
        MetricsDataResponse response = metricApi.getMetricData(g.getId(),
                                                               template.getId(),
                                                               end, start);
        hqAssertFailureInvalidParameters(response);      
    }

    public void testGetMetricGroupDataInvalidTemplate() throws Exception {

        HQApi api = getApi();

        GroupApi groupApi = api.getGroupApi();

        GroupsResponse groupsResponse = groupApi.getCompatibleGroups();
        hqAssertSuccess(groupsResponse);
        List<Group> compatGroups = groupsResponse.getGroup();
        assertTrue("No compatible groups found", compatGroups.size() > 0);
        Group g = compatGroups.get(0);

        MetricApi metricApi = api.getMetricApi();
        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricsDataResponse response = metricApi.getMetricData(g.getId(),
                                                               Integer.MAX_VALUE,
                                                               start, end);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetMetricGroupDataWrongTemplate() throws Exception {

        HQApi api = getApi();

        GroupApi groupApi = api.getGroupApi();

        GroupsResponse groupsResponse = groupApi.getCompatibleGroups();
        hqAssertSuccess(groupsResponse);
        List<Group> compatGroups = groupsResponse.getGroup();
        assertTrue("No compatible groups found", compatGroups.size() > 0);
        Group g = compatGroups.get(0);

        MetricApi metricApi = api.getMetricApi();

        MetricTemplateResponse getTemplateResponse =
                metricApi.getMetricTemplate(10001);
        hqAssertSuccess(getTemplateResponse);
        MetricTemplate t = getTemplateResponse.getMetricTemplate();

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricsDataResponse response = metricApi.getMetricData(g.getId(),
                                                               t.getId(),
                                                               start, end);
        hqAssertFailureInvalidParameters(response);
    }

    public void testGetMetricGroupDataMixedGroup() throws Exception {

        HQApi api = getApi();

        GroupApi groupApi = api.getGroupApi();

        GroupsResponse groupsResponse = groupApi.getMixedGroups();
        hqAssertSuccess(groupsResponse);
        List<Group> mixedGroups = groupsResponse.getGroup();
        assertTrue("No mixed groups found", mixedGroups.size() > 0);
        Group g = mixedGroups.get(0);

        MetricApi metricApi = api.getMetricApi();

        MetricTemplateResponse getTemplateResponse =
                metricApi.getMetricTemplate(10001);
        hqAssertSuccess(getTemplateResponse);
        MetricTemplate t = getTemplateResponse.getMetricTemplate();

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricsDataResponse response = metricApi.getMetricData(g.getId(),
                                                               t.getId(),
                                                               start, end);
        hqAssertFailureInvalidParameters(response);
    }


    public void testGetMetricGroupDataInvalidGroup() throws Exception {

        HQApi api = getApi();

        GroupApi groupApi = api.getGroupApi();

        GroupsResponse groupsResponse = groupApi.getCompatibleGroups();
        hqAssertSuccess(groupsResponse);
        List<Group> compatGroups = groupsResponse.getGroup();
        assertTrue("No compatible groups found", compatGroups.size() > 0);
        Group g = compatGroups.get(0);

        ResourcesResponse resourcesResponse = groupApi.getResources(g.getId());
        hqAssertSuccess(resourcesResponse);

        ResourcePrototype pt = g.getResourcePrototype();
        MetricApi metricApi = api.getMetricApi();
        MetricTemplatesResponse templatesResponse = metricApi.getMetricTemplates(pt);
        hqAssertSuccess(templatesResponse);
        List<MetricTemplate> templates = templatesResponse.getMetricTemplate();
        assertTrue("No templates found for " + pt.getName(), templates.size() > 0);

        // Make sure the template we query is default-on
        MetricTemplate template = null;
        for (MetricTemplate t : templates) {
            if (t.isDefaultOn()) {
                template = t;
                break;
            }
        }

        if (template == null) {
            throw new Exception("Could not find default on template for " +
                                pt.getName());
        }

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricsDataResponse response = metricApi.getMetricData(Integer.MAX_VALUE,
                                                               template.getId(),
                                                               start, end);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetMetricGroupDataDefaultOffTemplate() throws Exception {

        HQApi api = getApi();

        GroupApi groupApi = api.getGroupApi();

        GroupsResponse groupsResponse = groupApi.getCompatibleGroups();
        hqAssertSuccess(groupsResponse);
        List<Group> compatGroups = groupsResponse.getGroup();
        assertTrue("No compatible groups found", compatGroups.size() > 0);
        Group g = compatGroups.get(0);

        ResourcesResponse resourcesResponse = groupApi.getResources(g.getId());
        hqAssertSuccess(resourcesResponse);

        ResourcePrototype pt = g.getResourcePrototype();
        MetricApi metricApi = api.getMetricApi();
        MetricTemplatesResponse templatesResponse = metricApi.getMetricTemplates(pt);
        hqAssertSuccess(templatesResponse);
        List<MetricTemplate> templates = templatesResponse.getMetricTemplate();
        assertTrue("No templates found for " + pt.getName(), templates.size() > 0);

        // Make sure the template we query is default-on
        MetricTemplate template = null;
        for (MetricTemplate t : templates) {
            if (!t.isDefaultOn()) {
                template = t;
                break;
            }
        }

        if (template == null) {
            throw new Exception("Could not find default off template for " +
                                pt.getName());
        }

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);

        MetricsDataResponse response = metricApi.getMetricData(g.getId(),
                                                               template.getId(),
                                                               start, end);
        hqAssertSuccess(response);

        List<MetricData> metricData = response.getMetricData();
        assertTrue("Number of Resources in Group does not match the number " +
                   "of ResourceMetrics",
                   resourcesResponse.getResource().size() == metricData.size());

        for (MetricData m : metricData) {
            assertTrue(m.getMetricId() > 0);
            assertTrue(m.getMetricName().length() > 0);
            assertTrue(m.getResourceId() > 0);
            assertTrue(m.getResourceName().length() > 0);
            assertTrue(m.getDataPoint().size() == 0);
        }
    }

    public void testResourceMetrics() throws Exception {

        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();
        MetricApi metricApi = api.getMetricApi();

        // Find prototype
        ResourcePrototypeResponse prototypeResponse =
            resourceApi.getResourcePrototype("FileServer Mount");
        hqAssertSuccess(prototypeResponse);

        // Find resources to query
        ResourcesResponse findResponse =
                resourceApi.getResources(prototypeResponse.getResourcePrototype());
        hqAssertSuccess(findResponse);
        assertTrue("No resources found to query",
                   findResponse.getResource().size() > 0);

        // Get template
        MetricTemplatesResponse listTemplatesResponse =
                metricApi.getMetricTemplates(prototypeResponse.getResourcePrototype());
        hqAssertSuccess(listTemplatesResponse);

        int templateId = 0;
        for (MetricTemplate t : listTemplatesResponse.getMetricTemplate()) {
            if (t.isDefaultOn()) {
                templateId = t.getId();
            }
        }

        assertTrue("No default on templates found", templateId > 0);

        List<Resource> resources = findResponse.getResource();
        int resourceIds[] = new int[resources.size()];

        for (int i = 0; i < resources.size(); i++) {
            resourceIds[i] = resources.get(i).getId();
        }

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);

        MetricsDataResponse goodResponse = metricApi.getMetricData(resourceIds,
                                                                   templateId,
                                                                   start, end);
        hqAssertSuccess(goodResponse);

        List<MetricData> metricData = goodResponse.getMetricData();

        assertTrue("Number of Resources in Group does not match the number " +
                   "of ResourceMetrics",
                   metricData.size() == resourceIds.length);
        for (MetricData m : metricData) {
            assertTrue(m.getMetricId() > 0);
            assertTrue(m.getMetricName().length() > 0);
            assertTrue(m.getResourceId() > 0);
            assertTrue(m.getResourceName().length() > 0);
            assertTrue(m.getDataPoint().size() >= 0);
        }

        // Retry with start > end.
        MetricsDataResponse invalidIntervalResponse =
                metricApi.getMetricData(resourceIds, templateId, end, start);
        hqAssertFailureInvalidParameters(invalidIntervalResponse);

        // Retry with invalid template id.
        MetricsDataResponse invalidTemplateResponse =
                metricApi.getMetricData(resourceIds, 1, start, end);
        hqAssertFailureObjectNotFound(invalidTemplateResponse);

        // Retry with valid template, but belonging to this type
        MetricsDataResponse wrongTemplateResponse =
                metricApi.getMetricData(resourceIds, 10001, start, end);
        hqAssertFailureInvalidParameters(wrongTemplateResponse);

        // Retry with empty resources array
        MetricsDataResponse emptyResourcesResponse =
                metricApi.getMetricData(new int[] {}, templateId, start, end);
        hqAssertFailureInvalidParameters(emptyResourcesResponse);

        // Retry with invalid resource ids.
        MetricsDataResponse invalidResourceResponse =
                metricApi.getMetricData(new int[] { Integer.MAX_VALUE },
                                        templateId, start, end);
        hqAssertFailureObjectNotFound(invalidResourceResponse);
    }
}
