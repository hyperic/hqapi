package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertAction;
import org.hyperic.hq.hqapi1.types.AlertActionConfig;

import java.util.List;
import java.util.ArrayList;

public class AlertDefinitionSyncScriptAction_test extends AlertDefinitionTestBase {

    public AlertDefinitionSyncScriptAction_test(String name) {
        super(name);
    }

    public void testSyncScriptAction() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();

        final String SCRIPT = "/usr/bin/true";

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

        AlertAction action = AlertDefinitionBuilder.createScriptAction(SCRIPT);
        d.getAlertAction().add(action);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition def = response.getAlertDefinition().get(0);
        validateDefinition(def);
        assertEquals("Wrong number of actions found", 1, def.getAlertAction().size());
        AlertAction syncedAction = def.getAlertAction().get(0);
        assertEquals("Wrong action class", "com.hyperic.hq.bizapp.server.action.control.ScriptAction",
                     syncedAction.getClassName());
        assertEquals("Wrong number of configuration options",
                     1, syncedAction.getAlertActionConfig().size());
        AlertActionConfig cfg = syncedAction.getAlertActionConfig().get(0);
        assertEquals("Wrong path to script", SCRIPT, cfg.getValue());

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testSyncScriptActionRemove() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();

        final String SCRIPT = "/usr/bin/true";

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

        AlertAction action = AlertDefinitionBuilder.createScriptAction(SCRIPT);
        d.getAlertAction().add(action);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
                
        AlertDefinition syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Wrong number of actions", 1, syncedDef.getAlertAction().size());

        // Clear the action's and resync

        syncedDef.getAlertAction().clear();

        definitions.clear();
        definitions.add(syncedDef);
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Alert actions not cleared!", 0, syncedDef.getAlertAction().size());

        cleanup(response.getAlertDefinition());
    }
}
