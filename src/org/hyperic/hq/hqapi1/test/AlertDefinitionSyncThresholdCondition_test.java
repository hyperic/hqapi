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

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertConditionType;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertComparator;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Resource;

import java.util.ArrayList;
import java.util.List;

public class AlertDefinitionSyncThresholdCondition_test extends AlertDefinitionTestBase {

    public AlertDefinitionSyncThresholdCondition_test(String name) {
        super(name);
    }

    public void testValidThresholdConditon() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();

        Resource platform = getLocalPlatformResource(false, false);

        MetricsResponse metricsResponse = metricApi.getMetrics(platform, true);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        final double THRESHOLD = 0;
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.GREATER_THAN,
                                                                THRESHOLD));

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(d);
            for (AlertCondition c : def.getAlertCondition()) {
                assertNotNull("Condition was null", c);
                assertEquals(c.getType(), AlertConditionType.THRESHOLD.getType());
                assertEquals(c.getThresholdComparator(),
                             AlertComparator.GREATER_THAN.getComparator());
                assertEquals(c.getThresholdMetric(), m.getName());
                assertEquals(c.getThresholdValue(), THRESHOLD);
            }
        }

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testValidThresholdConditonTypeAlert() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();

        Resource platform = getLocalPlatformResource(false, false);

        MetricsResponse metricsResponse = metricApi.getMetrics(platform, true);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        final double THRESHOLD = 0;
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.GREATER_THAN,
                                                                THRESHOLD));

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(d);
            for (AlertCondition c : def.getAlertCondition()) {
                assertNotNull("Condition was null", c);
                assertEquals(c.getType(), AlertConditionType.THRESHOLD.getType());
                assertEquals(c.getThresholdComparator(),
                             AlertComparator.GREATER_THAN.getComparator());
                assertEquals(c.getThresholdMetric(), m.getName());
                assertEquals(c.getThresholdValue(), THRESHOLD);
            }
        }

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testCreateMissingAttributes() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();

        Resource platform = getLocalPlatformResource(false, false);

        MetricsResponse metricsResponse = metricApi.getMetrics(platform, true);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        AlertCondition c = new AlertCondition();
        c.setType(AlertConditionType.THRESHOLD.getType());
        d.getAlertCondition().add(c);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);

        c.setThresholdComparator(AlertComparator.EQUALS.getComparator());
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);

        c.setThresholdMetric(m.getName());
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);

        final double THRESHOLD = 0;
        c.setThresholdValue(THRESHOLD);
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testCreateInvalidMetric() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        final double THRESHOLD = 0;
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createThresholdCondition(true, "Invalid Metric",
                                                                AlertComparator.GREATER_THAN,
                                                                THRESHOLD));

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertFailureObjectNotFound(response);
    }
}
