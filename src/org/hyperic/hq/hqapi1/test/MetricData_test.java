/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.DataPoint;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.MetricData;
import org.hyperic.hq.hqapi1.types.MetricDataResponse;
import org.hyperic.hq.hqapi1.types.MetricTemplate;
import org.hyperic.hq.hqapi1.types.MetricTemplateResponse;
import org.hyperic.hq.hqapi1.types.MetricTemplatesResponse;
import org.hyperic.hq.hqapi1.types.MetricsDataResponse;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.List;
import java.util.Random;
import java.io.IOException;

public class MetricData_test extends MetricTestBase {

    public MetricData_test(String name) {
        super(name);
    }

    public void testGetEnabledMetricData() throws Exception {

        MetricApi api = getApi().getMetricApi();
        Resource r = getLocalPlatformResource(false, false);
        MetricsResponse resp = api.getMetrics(r, true);
        hqAssertSuccess(resp);

        assertTrue("No enabled metrics found for " + r.getName(),
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

    public void testGetMetricDataOldRange() throws Exception {

        MetricApi api = getApi().getMetricApi();
        Resource r = getLocalPlatformResource(false, false);
        MetricsResponse resp = api.getMetrics(r, true);
        hqAssertSuccess(resp);

        assertTrue("No enabled metrics found for " + r.getName(),
                   resp.getMetric().size() > 0);

        Metric metric = null;
        for (Metric m : resp.getMetric()) {
            if (!m.getMetricTemplate().getAlias().equals("Availability")) {
                metric = m;
                break;
            }
        }

        assertNotNull("No metric found for " + r.getName(), metric);

        long end = 100;
        long start = 0;

        MetricDataResponse dataResponse = api.getMetricData(metric.getId(),
                                                            start, end);
        hqAssertSuccess(dataResponse);

        assertTrue("Wrong number of data points, expected 0, got " +
                   dataResponse.getMetricData().getDataPoint().size(),
                   dataResponse.getMetricData().getDataPoint().size() == 0);
    }

    public void testGetDisabledMetricData() throws Exception {

        MetricApi api = getApi().getMetricApi();
        Resource r = getLocalPlatformResource(false, false);
        MetricsResponse resp = api.getMetrics(r, false);
        hqAssertSuccess(resp);

        assertTrue("No metrics found for " + r.getName(),
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
                   r.getName() + " (" + dataResponse.getMetricData().getDataPoint().size() +
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
        Resource r = getLocalPlatformResource(false, false);
        MetricsResponse resp = api.getMetrics(r, true);
        hqAssertSuccess(resp);

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);

        assertTrue("No metrics found for " + r.getName(),
                   resp.getMetric().size() > 0);
        Metric m = resp.getMetric().get(0);

        // Test end > start.
        MetricDataResponse dataResponse = api.getMetricData(m.getId(),
                                                            end, start);
        hqAssertFailureInvalidParameters(dataResponse);
    }

    // Helper method to get a MetricTemplate for the Linux ResourcePrototype
    private MetricTemplate getLinuxTemplate() throws IOException
    {
        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();
        MetricApi metricApi = api.getMetricApi();

        ResourcePrototypeResponse protoResponse =
                resourceApi.getResourcePrototype("Linux");
        hqAssertSuccess(protoResponse);

        MetricTemplatesResponse templatesResponse =
                metricApi.getMetricTemplates(protoResponse.getResourcePrototype());
        hqAssertSuccess(templatesResponse);

        assertTrue("No templates found!", templatesResponse.getMetricTemplate().size() > 0);

        return templatesResponse.getMetricTemplate().get(0);
    }

    private Group getFileServerMountCompatibleGroup() throws Exception {

        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();
        GroupApi groupApi = api.getGroupApi();

        ResourcePrototypeResponse protoResponse =
                resourceApi.getResourcePrototype("FileServer Mount");
        hqAssertSuccess(protoResponse);

        ResourcesResponse resources = resourceApi.getResources(protoResponse.getResourcePrototype(),
                                                               false, false);
        hqAssertSuccess(resources);
        assertTrue("Unable to find resources of type " +
                   protoResponse.getResourcePrototype().getName(),
                   resources.getResource().size() > 0);

        Random r = new Random();
        Group g = new Group();
        g.setName("Compatible Group for MetricData_test" + r.nextInt());
        g.setResourcePrototype(protoResponse.getResourcePrototype());
        g.getResource().addAll(resources.getResource());

        GroupResponse groupResponse = groupApi.createGroup(g);
        hqAssertSuccess(groupResponse);

        return groupResponse.getGroup();
    }

    private Group getMixedGroup() throws Exception {

        HQApi api = getApi();
        GroupApi groupApi = api.getGroupApi();

        Resource platform = getLocalPlatformResource(false, true);

        assertTrue("Unable to find child resources for " +
                   platform.getName(),
                   platform.getResource().size() > 0);

        Random r = new Random();
        Group g = new Group();
        g.setName("Mixed Group for MetricData_test" + r.nextInt());
        g.getResource().addAll(platform.getResource());

        GroupResponse groupResponse = groupApi.createGroup(g);
        hqAssertSuccess(groupResponse);

        return groupResponse.getGroup();
    }

    private void cleanupGroup(Group g) throws Exception {
        GroupApi api = getApi().getGroupApi();
        StatusResponse response = api.deleteGroup(g.getId());
        hqAssertSuccess(response);
    }

    public void testGetMetricGroupData() throws Exception {

        HQApi api = getApi();

        Group g = getFileServerMountCompatibleGroup();
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
                   g.getResource().size() == metricData.size());

        for (MetricData m : metricData) {
            assertTrue(m.getMetricId() > 0);
            assertTrue(m.getMetricName().length() > 0);
            assertTrue(m.getResourceId() > 0);
            assertTrue(m.getResourceName().length() > 0);
            assertTrue(m.getDataPoint().size() > 0);
        }

        cleanupGroup(g);
    }

    public void testGetMetricGroupDataInvalidRange() throws Exception {

        HQApi api = getApi();

        Group g = getFileServerMountCompatibleGroup();
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
        cleanupGroup(g);
    }

    public void testGetMetricGroupDataInvalidTemplate() throws Exception {

        HQApi api = getApi();

        Group g = getFileServerMountCompatibleGroup();
        MetricApi metricApi = api.getMetricApi();
        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricsDataResponse response = metricApi.getMetricData(g.getId(),
                                                               Integer.MAX_VALUE,
                                                               start, end);
        hqAssertFailureObjectNotFound(response);
        cleanupGroup(g);
    }

    public void testGetMetricGroupDataWrongTemplate() throws Exception {

        HQApi api = getApi();

        Group g = getFileServerMountCompatibleGroup();
        MetricApi metricApi = api.getMetricApi();

        MetricTemplate t = getLinuxTemplate();

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricsDataResponse response = metricApi.getMetricData(g.getId(),
                                                               t.getId(),
                                                               start, end);
        hqAssertFailureInvalidParameters(response);
        cleanupGroup(g);
    }

    public void testGetMetricGroupDataMixedGroup() throws Exception {

        HQApi api = getApi();

        Group g = getMixedGroup();
        MetricApi metricApi = api.getMetricApi();
        MetricTemplate t = getLinuxTemplate();

        long end = System.currentTimeMillis();
        long start = end - (8 * 60 * 60 * 1000);
        MetricsDataResponse response = metricApi.getMetricData(g.getId(),
                                                               t.getId(),
                                                               start, end);
        hqAssertFailureInvalidParameters(response);
        cleanupGroup(g);
    }


    public void testGetMetricGroupDataInvalidGroup() throws Exception {

        HQApi api = getApi();

        Group g = getFileServerMountCompatibleGroup();
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
        cleanupGroup(g);
    }

    public void testGetMetricGroupDataDefaultOffTemplate() throws Exception {

        HQApi api = getApi();

        Group g = getFileServerMountCompatibleGroup();
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
                   g.getResource().size() == metricData.size());

        for (MetricData m : metricData) {
            assertTrue(m.getMetricId() > 0);
            assertTrue(m.getMetricName().length() > 0);
            assertTrue(m.getResourceId() > 0);
            assertTrue(m.getResourceName().length() > 0);
            assertTrue(m.getDataPoint().size() == 0);
        }
        cleanupGroup(g);
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
                resourceApi.getResources(prototypeResponse.getResourcePrototype(),
                                         false, false);
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
        MetricTemplate linuxTemplate = getLinuxTemplate();
        MetricsDataResponse wrongTemplateResponse =
                metricApi.getMetricData(resourceIds, linuxTemplate.getId(), start, end);
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
