package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.EscalationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlertDefinitionSync_test extends AlertDefinitionTestBase {

    public AlertDefinitionSync_test(String name) {
        super(name);
    }

    // Generic AlertDefinition tests

    public void testSyncNoResource() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        AlertDefinition d = generateTestDefinition();
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);
    }

    public void testSyncInvalidAlertId() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setId(Integer.MAX_VALUE);
        d.setResourcePrototype(platform.getResourcePrototype());
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureObjectNotFound(response);
    }

    public void testSyncResourceAndResourcePrototype() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        d.setResource(platform); // Can't have Resource & ResourcePrototype
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);
    }

    public void testSyncInvalidResource() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = new Resource();
        platform.setId(Integer.MAX_VALUE);
        platform.setName("Invalid Platform Resource");

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform); // Invalid Resource
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureObjectNotFound(response);
    }

    public void testSyncInvalidResourcePrototype() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        ResourcePrototype proto = new ResourcePrototype();
        proto.setName("Invalid Prototype");

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(proto);
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureObjectNotFound(response);
    }

    public void testSyncInvalidPriority() throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        d.setPriority(4);
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);
    }

    public void testSyncCreateDefinition() throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), 1);
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(def);
        }

        // Cleanup
        cleanup(response.getAlertDefinition());
    }    

    public void testSyncCreateTypeDefinition() throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), 1);        
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(def);
        }

        // Cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testSyncCountAndRange() throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        d.setCount(3);
        d.setRange(1800);
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), 1);
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(def);
        }

        // Cleanup
        cleanup(response.getAlertDefinition());
    }

    // TODO: test sync multi broken with type alerts

    public void testSyncMulti() throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        final int NUM_DEFS = 10;
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        for (int i = 0; i < NUM_DEFS; i++) {
            AlertDefinition d = generateTestDefinition();
            d.setResource(platform);
            d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
            definitions.add(d);
        }

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), NUM_DEFS);        
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(def);
        }

        // Re-sync for update
        definitions = response.getAlertDefinition();
        final String UPDATED_DESCRIPTION = "Updated Alert Description";
        final AlertCondition newCond =
                AlertDefinitionBuilder.createPropertyCondition(true, "otherProp");
        for (AlertDefinition d : definitions) {
            d.setDescription(UPDATED_DESCRIPTION);
            d.getAlertCondition().clear();
            d.getAlertCondition().add(newCond);
        }

        response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        for (AlertDefinition d: definitions) {
            assertEquals(d.getDescription(), UPDATED_DESCRIPTION);
            for (AlertCondition c : d.getAlertCondition()) {
                assertTrue(c.getProperty().equals("otherProp"));
            }
        }

        // Cleanup
        cleanup(response.getAlertDefinition());
    }

    // Escalation tests

    public void testSyncInvalidEscalation() throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        Escalation e = new Escalation();
        e.setName("Invalid Escalation");
        d.setEscalation(e);
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureObjectNotFound(response);
    }

    public void testSyncEmptyEscalation() throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        Escalation e = new Escalation();
        d.setEscalation(e);
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureObjectNotFound(response);
    }

    public void testSyncEscalation() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        EscalationApi escApi = api.getEscalationApi();

        Resource platform = getLocalPlatformResource(false, false);

        Random r = new Random();
        Escalation e = new Escalation();
        e.setName("Test Escalation" + r.nextInt());
        EscalationResponse escalationResponse =
                escApi.createEscalation(e);
        hqAssertSuccess(escalationResponse);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        d.setEscalation(e);
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), 1);
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(def);
            assertEquals(def.getEscalation().getId(),
                         escalationResponse.getEscalation().getId());
        }

        // Cleanup
        cleanup(response.getAlertDefinition());
        escApi.deleteEscalation(escalationResponse.getEscalation().getId());
    }

    // TODO: sync update with esclation broken with type alerts

    public void testSyncUpdateWithEscalation() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        EscalationApi escApi = api.getEscalationApi();

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), 1);
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(def);
            assertNotNull("Resource was null", def.getResource());
            assertNull("Escalation was not null", def.getEscalation());
        }

        // Re-sync with escalation
        Random r = new Random();
        Escalation e = new Escalation();
        e.setName("Test Escalation" + r.nextInt());
        EscalationResponse escalationResponse =
                escApi.createEscalation(e);
        hqAssertSuccess(escalationResponse);

        definitions = response.getAlertDefinition();
        for (AlertDefinition def : definitions) {
            def.setEscalation(e);
        }

        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), 1);
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(def);
            // TODO: Fix me
            //assertNotNull("Escalation was null", def.getEscalation());
            //assertEquals(d.getEscalation().getName(), e.getName());
        }
        
        // Cleanup
        cleanup(response.getAlertDefinition());
        escApi.deleteEscalation(escalationResponse.getEscalation().getId());
    }

    // AlertCondition tests

    public void testSyncNoConditions() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);
    }

    public void testInvalidAlertConditionType() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());

        AlertCondition cond = new AlertCondition();
        cond.setRequired(true);
        cond.setType(10); // Types range from 1-8.. See EventsConstants

        d.getAlertCondition().add(cond);
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);
    }
}
