package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertComparator;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Metric;

import java.util.List;
import java.util.ArrayList;

public class AlertDefinitionSyncRecoveryCondition_test extends AlertDefinitionTestBase {

    public AlertDefinitionSyncRecoveryCondition_test(String name) {
        super(name);
    }

    public void testValidRecoveryCondition() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();

        Resource platform = getLocalPlatformResource(false, false);

        MetricsResponse metricsResponse = metricApi.getMetrics(platform);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);

        AlertDefinition def = generateTestDefinition();
        def.setResource(platform);
        final double THRESHOLD = 0;
        def.getAlertCondition().add(
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.GREATER_THAN,
                                                                THRESHOLD));
        AlertDefinition recoveryDef = generateTestDefinition();
        recoveryDef.setResource(platform);
        AlertCondition threshold =
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.LESS_THAN,
                                                                THRESHOLD);
        AlertCondition recovery =
                AlertDefinitionBuilder.createRecoveryCondition(true, def);
        recoveryDef.getAlertCondition().add(threshold);
        recoveryDef.getAlertCondition().add(recovery);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(def);
        definitions.add(recoveryDef);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        for (AlertDefinition d : response.getAlertDefinition()) {
            validateDefinition(d);

            // TODO: Validate defs & condition ordering
        }

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testValidRecoveryConditionTypeAlert() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();

        Resource platform = getLocalPlatformResource(false, false);

        MetricsResponse metricsResponse = metricApi.getMetrics(platform);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);

        AlertDefinition def = generateTestDefinition();
        def.setResourcePrototype(platform.getResourcePrototype());
        final double THRESHOLD = 0;
        def.getAlertCondition().add(
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.GREATER_THAN,
                                                                THRESHOLD));
        AlertDefinition recoveryDef = generateTestDefinition();
        recoveryDef.setResourcePrototype(platform.getResourcePrototype());
        AlertCondition threshold =
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.LESS_THAN,
                                                                THRESHOLD);
        AlertCondition recovery =
                AlertDefinitionBuilder.createRecoveryCondition(true, def);
        recoveryDef.getAlertCondition().add(threshold);
        recoveryDef.getAlertCondition().add(recovery);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(def);
        definitions.add(recoveryDef);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        for (AlertDefinition d : response.getAlertDefinition()) {
            validateDefinition(d);

            // TODO: Validate defs & condition ordering
        }

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testSyncRecoveryWithoutProblemDef() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();

        Resource platform = getLocalPlatformResource(false, false);

        MetricsResponse metricsResponse = metricApi.getMetrics(platform);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);


        // First sync the problem definition
        AlertDefinition def = generateTestDefinition();
        def.setResource(platform);
        final double THRESHOLD = 0;
        def.getAlertCondition().add(
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.GREATER_THAN,
                                                                THRESHOLD));

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(def);
        AlertDefinitionsResponse downResponse = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(downResponse);

        // Next, sync the recovery
        AlertDefinition recoveryDef = generateTestDefinition();
        recoveryDef.setResource(platform);
        AlertCondition threshold =
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.LESS_THAN,
                                                                THRESHOLD);
        AlertCondition recovery =
                AlertDefinitionBuilder.createRecoveryCondition(true, def);
        recoveryDef.getAlertCondition().add(threshold);
        recoveryDef.getAlertCondition().add(recovery);

        definitions.clear();
        definitions.add(recoveryDef);
        AlertDefinitionsResponse recoveryResponse = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(recoveryResponse);

        // cleanup
        cleanup(downResponse.getAlertDefinition());
        cleanup(recoveryResponse.getAlertDefinition());
    }

    public void testSyncRecoveryWithoutProblemDefTypeAlert() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();

        Resource platform = getLocalPlatformResource(false, false);

        MetricsResponse metricsResponse = metricApi.getMetrics(platform);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);


        // First sync the problem definition
        AlertDefinition def = generateTestDefinition();
        def.setResourcePrototype(platform.getResourcePrototype());
        final double THRESHOLD = 0;
        def.getAlertCondition().add(
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.GREATER_THAN,
                                                                THRESHOLD));

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(def);
        AlertDefinitionsResponse downResponse = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(downResponse);

        // Next, sync the recovery
        AlertDefinition recoveryDef = generateTestDefinition();
        recoveryDef.setResourcePrototype(platform.getResourcePrototype());
        AlertCondition threshold =
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.LESS_THAN,
                                                                THRESHOLD);
        AlertCondition recovery =
                AlertDefinitionBuilder.createRecoveryCondition(true, def);
        recoveryDef.getAlertCondition().add(threshold);
        recoveryDef.getAlertCondition().add(recovery);

        definitions.clear();
        definitions.add(recoveryDef);
        AlertDefinitionsResponse recoveryResponse = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(recoveryResponse);

        // cleanup
        cleanup(downResponse.getAlertDefinition());
        cleanup(recoveryResponse.getAlertDefinition());
    }

    // TODO: Missing attributes

    // TODO: Missing condition (recovery requrires 2..)

    public void testInvalidRecoveryAlert() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        MetricApi metricApi = api.getMetricApi();
        Resource platform = getLocalPlatformResource(false, false);

        MetricsResponse metricsResponse = metricApi.getMetrics(platform);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);

        AlertDefinition problemDef = generateTestDefinition();
        AlertDefinition recoveryDef = generateTestDefinition();

        recoveryDef.setResource(platform);

        AlertCondition thresholdCond =
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.EQUALS, 1);
        AlertCondition recoveryCond =
                AlertDefinitionBuilder.createRecoveryCondition(true, problemDef);
        recoveryDef.getAlertCondition().add(thresholdCond);
        recoveryDef.getAlertCondition().add(recoveryCond);


        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(recoveryDef);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertFailureObjectNotFound(response);
    }

    // TODO: Invalid recovery alert (wrong resource type)
}
