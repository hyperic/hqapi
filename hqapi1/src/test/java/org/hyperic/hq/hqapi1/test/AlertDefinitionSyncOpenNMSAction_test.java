package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertAction;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.List;
import java.util.ArrayList;

public class AlertDefinitionSyncOpenNMSAction_test extends AlertDefinitionTestBase {

    public AlertDefinitionSyncOpenNMSAction_test(String name) {
        super(name);
    }

    public void testAddRemoveOpenNMSAction() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();

        Resource platform = getLocalPlatformResource(false, false);

        MetricsResponse metricsResponse = metricApi.getMetrics(platform);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createChangeCondition(true, m.getName()));

        AlertAction action = AlertDefinitionBuilder.createOpenNMSAction("127.0.0.1",
                                                                        9999);
        d.getAlertAction().add(action);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition def = response.getAlertDefinition().get(0);
        validateDefinition(def);
        assertEquals("Wrong number of actions found", 1, def.getAlertAction().size());
        AlertAction syncedAction = def.getAlertAction().get(0);
        assertEquals("Wrong action class", "org.hyperic.hq.bizapp.server.action.integrate.OpenNMSAction",
                     syncedAction.getClassName());
        assertEquals("Wrong number of configuration options",
                     2, syncedAction.getAlertActionConfig().size());

        // Clear
        def.getAlertAction().clear();
        definitions.clear();
        definitions.add(def);

        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        def = response.getAlertDefinition().get(0);
        validateDefinition(def);
        assertEquals("Wrong number of actions found", 0, def.getAlertAction().size());

        // Cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testAddOpenNMSActionEmptyConfiguration() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();

        Resource platform = getLocalPlatformResource(false, false);

        MetricsResponse metricsResponse = metricApi.getMetrics(platform);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createChangeCondition(true, m.getName()));

        AlertAction action = AlertDefinitionBuilder.createOpenNMSAction("127.0.0.1",
                                                                        9999);
        // Clear the configuration
        action.getAlertActionConfig().clear();
        d.getAlertAction().add(action);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition def = response.getAlertDefinition().get(0);
        validateDefinition(def);
        assertEquals("Wrong number of actions found", 1, def.getAlertAction().size());
        AlertAction syncedAction = def.getAlertAction().get(0);
        assertEquals("Wrong action class", "org.hyperic.hq.bizapp.server.action.integrate.OpenNMSAction",
                     syncedAction.getClassName());
        assertEquals("Wrong number of configuration options",
                     0, syncedAction.getAlertActionConfig().size());


        // Cleanup
        cleanup(response.getAlertDefinition());
    }
}
