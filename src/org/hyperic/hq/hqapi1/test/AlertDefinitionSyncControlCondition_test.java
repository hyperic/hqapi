package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertConditionType;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertControlStatus;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.Resource;

import java.util.ArrayList;
import java.util.List;

public class AlertDefinitionSyncControlCondition_test extends AlertDefinitionTestBase {

    public AlertDefinitionSyncControlCondition_test(String name) {
        super(name);
    }

    public void testValidControlConditon() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        final String ACTION = "start";
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createControlCondition(true,
                                                              ACTION,
                                                              AlertControlStatus.COMPLETED));

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(d);
            for (AlertCondition c : def.getAlertCondition()) {
                assertNotNull("Condition was null", c);
                assertEquals(c.getType(), AlertConditionType.CONTROL.getType());
                assertEquals(c.getControlAction(), ACTION);
                assertEquals(c.getControlStatus(), AlertControlStatus.COMPLETED.getControlStatus());
            }
        }

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testValidControlConditonTypeAlert() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        final String ACTION = "start";
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createControlCondition(true,
                                                              ACTION,
                                                              AlertControlStatus.COMPLETED));

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(d);
            for (AlertCondition c : def.getAlertCondition()) {
                assertNotNull("Condition was null", c);
                assertEquals(c.getType(), AlertConditionType.CONTROL.getType());
                assertEquals(c.getControlAction(), ACTION);
                assertEquals(c.getControlStatus(), AlertControlStatus.COMPLETED.getControlStatus());
            }
        }

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testControlConditionMissingAttributes() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());

        AlertCondition c = new AlertCondition();
        c.setType(AlertDefinitionBuilder.AlertConditionType.CONTROL.getType());
        d.getAlertCondition().add(c);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);

        c.setControlAction("start");
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);

        c.setControlStatus(AlertDefinitionBuilder.AlertControlStatus.COMPLETED.getControlStatus());
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testControlConditionInvalidControlStatus() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());

        AlertCondition c = new AlertCondition();
        c.setType(AlertDefinitionBuilder.AlertConditionType.CONTROL.getType());
        c.setControlAction("start");
        c.setControlStatus("Invalid Status");
        d.getAlertCondition().add(c);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);       
    }
}
