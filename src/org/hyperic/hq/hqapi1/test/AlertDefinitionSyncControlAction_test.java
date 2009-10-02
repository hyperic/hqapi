package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertAction;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;

import java.util.List;
import java.util.ArrayList;

public class AlertDefinitionSyncControlAction_test extends AlertDefinitionTestBase {

    public AlertDefinitionSyncControlAction_test(String name) {
        super(name);
    }

    public void testAddRemoveControlAction() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();

        Resource controllableResource = createControllableResource(api);

        MetricsResponse metricsResponse = metricApi.getMetrics(controllableResource);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + controllableResource.getName(),
                metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);

        AlertDefinition d = generateTestDefinition();
        d.setResource(controllableResource);
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createChangeCondition(true, m.getName()));

        AlertAction action = AlertDefinitionBuilder.createControlAction(controllableResource,
                                                                        "run");
        d.getAlertAction().add(action);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition def = response.getAlertDefinition().get(0);
        validateDefinition(def);
        assertEquals("Wrong number of actions found", 1, def.getAlertAction().size());
        AlertAction syncedAction = def.getAlertAction().get(0);
        assertEquals("Wrong action class", "com.hyperic.hq.bizapp.server.action.control.ControlAction",
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
        cleanupControllableResource(api, controllableResource);
    }

    public void testAddControlActionWrongAction() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();

        Resource controllableResource = createControllableResource(api);

        MetricsResponse metricsResponse = metricApi.getMetrics(controllableResource);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + controllableResource.getName(),
                metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);

        AlertDefinition d = generateTestDefinition();
        d.setResource(controllableResource);
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createChangeCondition(true, m.getName()));

        AlertAction action = AlertDefinitionBuilder.createControlAction(controllableResource,
                                                                        "nonexistantaction");
        d.getAlertAction().add(action);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition def = response.getAlertDefinition().get(0);
        validateDefinition(def);
        assertEquals("Wrong number of actions found", 0, def.getAlertAction().size());

        // Cleanup
        cleanup(response.getAlertDefinition());
        cleanupControllableResource(api, controllableResource);
    }

    public void testAddControlActionUncontrollableResource() throws Exception {
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

        AlertAction action = AlertDefinitionBuilder.createControlAction(platform,
                                                                        "nonexistantaction");
        d.getAlertAction().add(action);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition def = response.getAlertDefinition().get(0);
        validateDefinition(def);
        assertEquals("Wrong number of actions found", 0, def.getAlertAction().size());

        // Cleanup
        cleanup(response.getAlertDefinition());
    }
    
    public void testAddControlActionEmptyConfiguration() throws Exception {
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

        AlertAction action = AlertDefinitionBuilder.createControlAction(platform,
                                                                        "nonexistantaction");
        // Clear the config
        action.getAlertActionConfig().clear();
        d.getAlertAction().add(action);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition def = response.getAlertDefinition().get(0);
        validateDefinition(def);
        assertEquals("Wrong number of actions found", 0, def.getAlertAction().size());

        // Cleanup
        cleanup(response.getAlertDefinition());
    }
}
