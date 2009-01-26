package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;

import java.util.List;
import java.util.ArrayList;

public class AlertDefinitionDelete_test extends AlertDefinitionTestBase {

    public AlertDefinitionDelete_test(String name) {
        super(name);
    }

    public void testDeleteBadId() throws Exception {

        AlertDefinitionApi alertDefApi = getApi().getAlertDefinitionApi();

        StatusResponse deleteResponse =
                alertDefApi.deleteAlertDefinition(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(deleteResponse);
    }

    public void testDeleteIndividualBasedOnTypeAlert() throws Exception {

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

        assertTrue("Couldn't find alert children for id " + d.getId(),
                   children.getAlertDefinition().size() > 0);

        // Try to delete a child
        for (AlertDefinition child : children.getAlertDefinition()) {
            StatusResponse deleteResponse = api.deleteAlertDefinition(child.getId());
            hqAssertFailureNotSupported(deleteResponse);
        }

        cleanup(response.getAlertDefinition());
    }
}
