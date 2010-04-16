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

import java.util.Collections;

public class AlertDefinitionSyncSNMPAction_test extends AlertDefinitionTestBase {

    public AlertDefinitionSyncSNMPAction_test(String name) {
        super(name);
    }

    public void testAddSNMPAction() throws Exception {
        HQApi api = getApi();
        MetricApi metricApi = api.getMetricApi();

        Resource platform = getLocalPlatformResource(false, false);

        MetricsResponse metricsResponse = metricApi.getMetrics(platform, true);
        hqAssertSuccess(metricsResponse);
        assertTrue("No enabled metrics found for " + platform.getName(),
                    metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createChangeCondition(true, m.getName()));

        AlertAction action = 
            AlertDefinitionBuilder.createSnmpAction("127.0.0.1",
                                                    "v2c Trap",
                                                    "1.2.4.5.6.7",
                                                    "");
        d.getAlertAction().add(action);

        AlertDefinition def = syncAlertDefinition(d);
        
        assertEquals("Wrong number of actions found", 
                     1, def.getAlertAction().size());
        AlertAction syncedAction = def.getAlertAction().get(0);
        assertEquals("Wrong action class", 
                     "com.hyperic.hq.bizapp.server.action.alert.SnmpAction",
                     syncedAction.getClassName());
        assertEquals("Wrong number of configuration options",
                     4, syncedAction.getAlertActionConfig().size());

        // Cleanup
        cleanup(Collections.singletonList(def));
    }

    public void testRemoveSNMPAction() throws Exception {
        HQApi api = getApi();
        MetricApi metricApi = api.getMetricApi();

        Resource platform = getLocalPlatformResource(false, false);

        MetricsResponse metricsResponse = metricApi.getMetrics(platform, true);
        hqAssertSuccess(metricsResponse);
        assertTrue("No enabled metrics found for " + platform.getName(),
                    metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createChangeCondition(true, m.getName()));

        AlertAction action = 
            AlertDefinitionBuilder.createSnmpAction("127.0.0.1",
                                                    "v2c Trap",
                                                    "1.2.4.5.6.7",
                                                    null);
        d.getAlertAction().add(action);

        AlertDefinition def = syncAlertDefinition(d);

        assertEquals("Wrong number of actions found", 
                     1, def.getAlertAction().size());
        AlertAction syncedAction = def.getAlertAction().get(0);
        assertEquals("Wrong action class", 
                     "com.hyperic.hq.bizapp.server.action.alert.SnmpAction",
                     syncedAction.getClassName());
        assertEquals("Wrong number of configuration options",
                     4, syncedAction.getAlertActionConfig().size());

        // Remove
        def.getAlertAction().clear();

        AlertDefinition updatedDef = syncAlertDefinition(def);

        assertEquals("Wrong number of actions found", 
                     0, updatedDef.getAlertAction().size());

        // Cleanup
        cleanup(Collections.singletonList(updatedDef));
    }
}
