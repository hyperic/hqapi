package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertDefinition;

public class AlertDefinitionGet_test extends AlertDefinitionTestBase {

    public AlertDefinitionGet_test(String name) {
        super(name);
    }

    public void testGetAllDefinitions() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        AlertDefinitionsResponse response = api.getAlertDefinitions(false);
        hqAssertSuccess(response);

        for (AlertDefinition d : response.getAlertDefinition()) {
            validateDefinition(d);
            // Parent will be null or a valid id, never 0.
            assertTrue("Invalid parent id " + d.getParent() +
                       " for definition " + d.getName(),
                       (d.getParent() == null || d.getParent() != 0));
            assertTrue("No Resource found for AlertDefinition",
                       d.getResource() != null);
        }
    }

    public void testGetAllDefinitionsExcludingTypeBased() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        AlertDefinitionsResponse response = api.getAlertDefinitions(true);
        hqAssertSuccess(response);

        for (AlertDefinition d : response.getAlertDefinition()) {
            validateDefinition(d);
            // Non-resource type alerts will have parent == null.
            assertTrue("Alert definition " + d.getName() + " has parent " +
                       d.getParent(), d.getParent() == null);
            assertTrue("No Resource found for AlertDefinition",
                       d.getResource() != null);            
        }
    }
}
