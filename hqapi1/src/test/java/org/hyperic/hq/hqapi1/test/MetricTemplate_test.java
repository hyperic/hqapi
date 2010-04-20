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

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.MetricTemplate;
import org.hyperic.hq.hqapi1.types.MetricTemplateResponse;
import org.hyperic.hq.hqapi1.types.MetricTemplatesResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.ArrayList;
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

    public void testListTemplates() throws Exception {

        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();

        final String TYPE = "Linux";
        ResourcePrototypeResponse response = resourceApi.getResourcePrototype(TYPE);
        hqAssertSuccess(response);

        ResourcePrototype pt = response.getResourcePrototype();

        MetricApi metricApi = api.getMetricApi();
        MetricTemplatesResponse metricTemplates =
                metricApi.getMetricTemplates(pt);
        hqAssertSuccess(metricTemplates);

        List<MetricTemplate> templates = metricTemplates.getMetricTemplate();
        assertTrue("No metrics found for type " + pt.getName(),
                   templates.size() > 0);
        for (MetricTemplate t : metricTemplates.getMetricTemplate()) {
            validateTemplate(t);
        }
    }

    public void testSyncTemplates() throws Exception {

        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();
        MetricApi metricApi = api.getMetricApi();

        final String TYPE = "Linux";
        ResourcePrototypeResponse prototype = resourceApi.getResourcePrototype(TYPE);
        hqAssertSuccess(prototype);

        // Keep a list of the original templates.
        MetricTemplatesResponse originalTemplates =
                metricApi.getMetricTemplates(prototype.getResourcePrototype());
        hqAssertSuccess(originalTemplates);

        // A copy of the templates to modify.
        MetricTemplatesResponse templatesResponse =
                metricApi.getMetricTemplates(prototype.getResourcePrototype());
        hqAssertSuccess(templatesResponse);

        final boolean ON = true;
        final boolean INDICATOR = true;
        final long    INTERVAL = 60000;

        for (MetricTemplate t : templatesResponse.getMetricTemplate()) {
            t.setDefaultOn(ON);
            t.setIndicator(INDICATOR);
            t.setDefaultInterval(INTERVAL);
        }

        // Sync
        StatusResponse syncResponse =
                metricApi.syncMetricTemplates(templatesResponse.getMetricTemplate());
        hqAssertSuccess(syncResponse);

        // Re-pull templates and check updated fields.
        templatesResponse = metricApi.getMetricTemplates(prototype.getResourcePrototype());
        hqAssertSuccess(templatesResponse);

        for (MetricTemplate t : templatesResponse.getMetricTemplate()) {
            assertTrue("Default interval for " + t.getName() + " not set",
                       t.getDefaultInterval() == INTERVAL);
            assertTrue("Default indicator is false for " + t.getName(),
                       t.isIndicator());
            assertTrue("Default on is false for " + t.getName(),
                       t.isDefaultOn());
        }

        // Reset to original
        syncResponse = metricApi.syncMetricTemplates(originalTemplates.getMetricTemplate());
        hqAssertSuccess(syncResponse);
    }

    public void testSyncTemplatesBadInterval() throws Exception {
        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();
        MetricApi metricApi = api.getMetricApi();

        final String TYPE = "Linux";
        ResourcePrototypeResponse prototype = resourceApi.getResourcePrototype(TYPE);
        hqAssertSuccess(prototype);

        // A copy of the temlates to modify.
        MetricTemplatesResponse templatesResponse =
                metricApi.getMetricTemplates(prototype.getResourcePrototype());
        hqAssertSuccess(templatesResponse);

        MetricTemplate template = templatesResponse.getMetricTemplate().get(0);

        final long BAD_INTERVALS[] = {-1, 0, 1, 1000, 59999};
        for (long BAD_INTERVAL : BAD_INTERVALS) {
            for (MetricTemplate t : templatesResponse.getMetricTemplate()) {
                t.setDefaultInterval(BAD_INTERVAL);
            }

            // Sync
            List<MetricTemplate> syncTemplates = new ArrayList<MetricTemplate>();
            syncTemplates.add(template);
            StatusResponse syncResponse = metricApi.syncMetricTemplates(syncTemplates);
            hqAssertFailureInvalidParameters(syncResponse);
        }
    }

    public void testSyncTemplatesDisableAvailIndicator() throws Exception {
        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();
        MetricApi metricApi = api.getMetricApi();

        final String TYPE = "Linux";
        ResourcePrototypeResponse prototype = resourceApi.getResourcePrototype(TYPE);
        hqAssertSuccess(prototype);

        MetricTemplatesResponse templatesResponse =
                metricApi.getMetricTemplates(prototype.getResourcePrototype());
        hqAssertSuccess(templatesResponse);

        MetricTemplate template = null;
        for (MetricTemplate t : templatesResponse.getMetricTemplate()) {
            if (t.getAlias().equals("Availability")) {
                template = t;
                break;
            }
        }

        assertNotNull("Unable to find availability template for " +
                      prototype.getResourcePrototype().getName(), template);

        template.setIndicator(false);
        List<MetricTemplate> templates = new ArrayList<MetricTemplate>();
        templates.add(template);

        // Sync
        StatusResponse syncResponse =
                metricApi.syncMetricTemplates(templates);
        hqAssertFailureInvalidParameters(syncResponse);      
    }

    public void testSyncTemplatesBadTemplate() throws Exception {

        HQApi api = getApi();
        MetricApi metricApi = api.getMetricApi();


        MetricTemplate template = new MetricTemplate();
        template.setId(Integer.MAX_VALUE);

        List<MetricTemplate> templates = new ArrayList<MetricTemplate>();
        templates.add(template);

        // Sync
        StatusResponse syncResponse =
                metricApi.syncMetricTemplates(templates);
        hqAssertFailureObjectNotFound(syncResponse);             
    }

    public void testListTemplatesEmptyPrototype() throws Exception {

        MetricApi api = getApi().getMetricApi();

        ResourcePrototype pt = new ResourcePrototype();

        MetricTemplatesResponse response = api.getMetricTemplates(pt);
        hqAssertFailureInvalidParameters(response);
    }

    public void testListTemplatesInvalidPrototype() throws Exception {

        MetricApi api = getApi().getMetricApi();

        ResourcePrototype pt = new ResourcePrototype();
        pt.setName("Non-existant resource prototype");
        MetricTemplatesResponse response = api.getMetricTemplates(pt);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetTemplate() throws Exception {

        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();

        final String TYPE = "Linux";
        ResourcePrototypeResponse response = resourceApi.getResourcePrototype(TYPE);
        hqAssertSuccess(response);

        ResourcePrototype pt = response.getResourcePrototype();

        MetricApi metricApi = api.getMetricApi();
        MetricTemplatesResponse metricTemplates =
                metricApi.getMetricTemplates(pt);
        hqAssertSuccess(metricTemplates);

        List<MetricTemplate> templates = metricTemplates.getMetricTemplate();
        assertTrue("No metrics found for type " + pt.getName(),
                   templates.size() > 0);

        MetricTemplate t = templates.get(0);

        MetricTemplateResponse getResponse = metricApi.getMetricTemplate(t.getId());
        hqAssertSuccess(getResponse);
        validateTemplate(getResponse.getMetricTemplate());
    }

    public void testGetTemplateInvalidId() throws Exception {

        MetricApi api = getApi().getMetricApi();

        MetricTemplateResponse getResponse = api.getMetricTemplate(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(getResponse);
    }
}
