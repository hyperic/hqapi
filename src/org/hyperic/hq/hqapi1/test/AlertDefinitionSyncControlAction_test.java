package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.types.MetricResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertAction;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class AlertDefinitionSyncControlAction_test extends AlertDefinitionTestBase {

    public AlertDefinitionSyncControlAction_test(String name) {
        super(name);
    }

    public void testAddRemoveControlAction() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        final MetricApi metricApi = api.getMetricApi();

        final Resource controllableResource = createControllableResource(api);
       
        SpinBarrier metricsAdded = new SpinBarrier( new SpinBarrierCondition() {
            public boolean evaluate() {
                MetricsResponse metricsResponse;
                try {
                    metricsResponse = metricApi.getMetrics(controllableResource);
                } catch (IOException e) {
                    e.printStackTrace();
                   return false;
                }
                hqAssertSuccess(metricsResponse);
                return metricsResponse.getMetric().size() > 0;
            }
        });
        assertTrue("No metrics found for " + controllableResource.getName(), metricsAdded.waitFor());
       
        MetricsResponse metricsResponse = metricApi.getMetrics(controllableResource);
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
        cleanupResource(api, controllableResource);
    }

    public void testAddControlActionWrongAction() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        final MetricApi metricApi = api.getMetricApi();

        final Resource controllableResource = createControllableResource(api);

        SpinBarrier metricsAdded = new SpinBarrier( new SpinBarrierCondition() {
            public boolean evaluate() {
                MetricsResponse metricsResponse;
                try {
                    metricsResponse = metricApi.getMetrics(controllableResource);
                } catch (IOException e) {
                    e.printStackTrace();
                   return false;
                }
                hqAssertSuccess(metricsResponse);
                return metricsResponse.getMetric().size() > 0;
            }
        });
        assertTrue("No metrics found for " + controllableResource.getName(), metricsAdded.waitFor());
        
        MetricsResponse metricsResponse = metricApi.getMetrics(controllableResource);
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
        cleanupResource(api, controllableResource);
    }

    public void testAddControlActionUncontrollableResource() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        final MetricApi metricApi = api.getMetricApi();

        final Resource platform = getLocalPlatformResource(false, false);
        
        SpinBarrier metricsAdded = new SpinBarrier( new SpinBarrierCondition() {
            public boolean evaluate() {
                MetricsResponse metricsResponse;
                try {
                    metricsResponse = metricApi.getMetrics(platform);
                } catch (IOException e) {
                    e.printStackTrace();
                   return false;
                }
                hqAssertSuccess(metricsResponse);
                return metricsResponse.getMetric().size() > 0;
            }
        });
        assertTrue("No metrics found for " + platform.getName(), metricsAdded.waitFor());

        MetricsResponse metricsResponse = metricApi.getMetrics(platform);
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
        final MetricApi metricApi = api.getMetricApi();

        final Resource platform = getLocalPlatformResource(false, false);
        
        SpinBarrier metricsAdded = new SpinBarrier( new SpinBarrierCondition() {
            public boolean evaluate() {
                MetricsResponse metricsResponse;
                try {
                    metricsResponse = metricApi.getMetrics(platform);
                } catch (IOException e) {
                    e.printStackTrace();
                   return false;
                }
                hqAssertSuccess(metricsResponse);
                return metricsResponse.getMetric().size() > 0;
            }
        });
        assertTrue("No metrics found for " + platform.getName(), metricsAdded.waitFor());

        MetricsResponse metricsResponse = metricApi.getMetrics(platform);
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
