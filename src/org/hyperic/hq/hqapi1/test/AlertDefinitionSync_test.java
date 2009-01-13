package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertConditionBuilder;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class AlertDefinitionSync_test extends AlertDefinitionTestBase {

    public AlertDefinitionSync_test(String name) {
        super(name);
    }

    private AlertDefinition createTestDefinition() {
        AlertDefinition d = new AlertDefinition();

        Random r = new Random();
        d.setName("Test Alert Definition" + r.nextInt());
        d.setDescription("Test Alert Description");
        d.setEnabled(true);
        return d;
    }

    public void testSyncNoConditions() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = createTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        StatusResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);
    }

    public void testSyncNoResource() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        AlertDefinition d = createTestDefinition();
        d.getAlertCondition().add(AlertConditionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        StatusResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);
    }

    public void testSyncResourceAndResourcePrototype() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = createTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        d.setResource(platform); // Can't have Resource & ResourcePrototype
        d.getAlertCondition().add(AlertConditionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        StatusResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);
    }

    public void testSyncInvalidResource() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = new Resource();
        platform.setId(Integer.MAX_VALUE);
        platform.setName("Invalid Platform Resource");

        AlertDefinition d = createTestDefinition();
        d.setResource(platform); // Invalid Resource
        d.getAlertCondition().add(AlertConditionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        StatusResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureObjectNotFound(response);
    }

    public void testSyncInvalidResourcePrototype() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        ResourcePrototype proto = new ResourcePrototype();
        proto.setName("Invalid Prototype");

        AlertDefinition d = createTestDefinition();
        d.setResourcePrototype(proto);
        d.getAlertCondition().add(AlertConditionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        StatusResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureObjectNotFound(response);
    }
}
