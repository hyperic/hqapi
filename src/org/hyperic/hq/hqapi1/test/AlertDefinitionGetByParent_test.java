package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.Resource;

import java.util.List;
import java.util.ArrayList;

public class AlertDefinitionGetByParent_test extends AlertDefinitionTestBase {

    public AlertDefinitionGetByParent_test(String name) {
        super(name);
    }

    public void testGetByParentBadId() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        AlertDefinition d = new AlertDefinition();
        d.setId(Integer.MAX_VALUE);
        AlertDefinitionsResponse response = api.getAlertDefinitions(d);
        hqAssertFailureObjectNotFound(response);   
    }

    public void testGetByParentNotTypeAlert() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        d = response.getAlertDefinition().get(0);

        // Test get by parent
        AlertDefinitionsResponse children = api.getAlertDefinitions(d);
        hqAssertFailureInvalidParameters(children);

        // Cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testValidGetByParent() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        d = response.getAlertDefinition().get(0);

        // Test get by parent
        AlertDefinitionsResponse children = api.getAlertDefinitions(d);
        hqAssertSuccess(children);
        assertTrue("Could not find children for parent id " + d.getId(),
                   children.getAlertDefinition().size() > 0);

        // Cleanup
        cleanup(response.getAlertDefinition());    
    }
}
