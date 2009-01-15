package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertDefinition;

public class AlertDefinitionGetTypeBased_test extends AlertDefinitionTestBase {

    public AlertDefinitionGetTypeBased_test(String name) {
        super(name);
    }

    public void testGetTypeDefinitionsWithIds() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        AlertDefinitionsResponse response = api.getTypeAlertDefinitions(false);
        hqAssertSuccess(response);

        for (AlertDefinition d : response.getAlertDefinition()) {
            validateDefinition(d);
            // Type alerts have parent == 0
            assertTrue("Invalid parent id " + d.getParent() +
                       " for type definition " + d.getName(),
                       d.getParent() == 0);
            assertTrue("No ResourcePrototype found for type based alert",
                       d.getResourcePrototype() != null);
            // Should always have ids
            assertNotNull("Alert id is null", d.getId());
        }
    }

    public void testGetTypeDefinitionsWithoutIds() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        AlertDefinitionsResponse response = api.getTypeAlertDefinitions(true);
        hqAssertSuccess(response);

        for (AlertDefinition d : response.getAlertDefinition()) {
            validateDefinition(d);
            // Type alerts have parent == 0
            assertTrue("Invalid parent id " + d.getParent() +
                       " for type definition " + d.getName(),
                       d.getParent() == 0);
            assertTrue("No ResourcePrototype found for type based alert",
                       d.getResourcePrototype() != null);
            // Should not have ids
            assertNull("Alert id is not null", d.getId());
        }
    }
}
