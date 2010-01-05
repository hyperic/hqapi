/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertPriority;
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
            validateTypeDefinition(def);
        }

        // Cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testSyncCountAndRangeTypeAlert() throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        final int count = 3;
        final int range = 1800;
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        d.setCount(count);
        d.setRange(range);
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), 1);
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateTypeDefinition(def);
            assertEquals(count, def.getCount());
            assertEquals(range, def.getRange());
            
            // TODO: validate child alert definitions
        }

        // Cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testSyncNofityFiltered() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setNotifyFiltered(false);
        d.setResource(platform);
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        assertEquals(response.getAlertDefinition().size(), 1);
        d = response.getAlertDefinition().get(0);
        validateDefinition(d);
        assertTrue("Notify filtered is not false", !d.isNotifyFiltered());

        d.setNotifyFiltered(true);
        definitions.clear();
        definitions.add(d);
        response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        assertEquals(response.getAlertDefinition().size(), 1);
        d = response.getAlertDefinition().get(0);
        validateDefinition(d);
        assertTrue("Notify filtered is not true", d.isNotifyFiltered());

        // Cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testSyncControlFiltered() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setControlFiltered(false);
        d.setResource(platform);
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        assertEquals(response.getAlertDefinition().size(), 1);
        d = response.getAlertDefinition().get(0);
        validateDefinition(d);
        assertTrue("Control filtered is not false", !d.isControlFiltered());

        d.setControlFiltered(true);
        definitions.clear();
        definitions.add(d);
        response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        assertEquals(response.getAlertDefinition().size(), 1);
        d = response.getAlertDefinition().get(0);
        validateDefinition(d);
        assertTrue("Control filtered is not true", d.isControlFiltered());

        // Cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testSyncActive() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setActive(false);
        d.setResource(platform);
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        assertEquals(response.getAlertDefinition().size(), 1);
        d = response.getAlertDefinition().get(0);
        validateDefinition(d);
        assertTrue("Enabled is not false", !d.isActive());

        d.setActive(true);
        definitions.clear();
        definitions.add(d);
        response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        assertEquals(response.getAlertDefinition().size(), 1);
        d = response.getAlertDefinition().get(0);
        validateDefinition(d);
        assertTrue("Enabled is not true", d.isActive());

        // Cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testSyncManyConditions()  throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);

        final int NUM_CONDITIONS = 10;
        for (int i = 0; i < NUM_CONDITIONS; i++) {
            AlertCondition c = AlertDefinitionBuilder.createPropertyCondition(false, "prop" + i);
            d.getAlertCondition().add(c);
        }
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), 1);
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(def);
            assertEquals(def.getAlertCondition().size(), NUM_CONDITIONS);
            // Ordering here is important
            for (int i = 0; i < NUM_CONDITIONS; i++) {
                assertEquals(def.getAlertCondition().get(i).getProperty(),
                             "prop" + i);
            }
        }

        // Cleanup
        cleanup(response.getAlertDefinition());
    }

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

    public void testSyncMultiTypeAlert() throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        final int INITIAL_PRIORITY = AlertPriority.LOW.getPriority();
        final boolean INITIAL_ACTIVE = true;
        final int NUM_DEFS = 10;
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        for (int i = 0; i < NUM_DEFS; i++) {
            AlertDefinition d = generateTestDefinition();
            d.setPriority(INITIAL_PRIORITY);
            d.setActive(INITIAL_ACTIVE);
            d.setResourcePrototype(platform.getResourcePrototype());
            d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
            definitions.add(d);
        }

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), NUM_DEFS);
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateTypeDefinition(def);
            assertEquals(INITIAL_PRIORITY, def.getPriority());
            assertEquals(INITIAL_ACTIVE, def.isActive());
            
            // validate child alert definitions
            AlertDefinitionsResponse childrenResponse = api.getAlertDefinitions(def);
            hqAssertSuccess(response);
            List<AlertDefinition> childrenDefinitions = childrenResponse.getAlertDefinition();
            assertTrue("No child alert definition exists for " + def.getName(),
                        childrenDefinitions.size() > 0);
            for (AlertDefinition child : childrenDefinitions) {
                validateDefinition(child);
            }            
        }

        // Re-sync for update
        definitions = response.getAlertDefinition();
        final int UPDATED_PRIORITY = AlertPriority.HIGH.getPriority();
        final boolean UPDATED_ACTIVE = false;
        final String UPDATED_NAME = "Updated Alert Definition Name";
        final String UPDATED_DESCRIPTION = "Updated Alert Definition Description";
        final AlertCondition newCond =
                AlertDefinitionBuilder.createPropertyCondition(true, "otherProp");
        for (AlertDefinition d : definitions) {
            d.setPriority(UPDATED_PRIORITY);
            d.setActive(UPDATED_ACTIVE);
            d.setName(UPDATED_NAME);
            d.setDescription(UPDATED_DESCRIPTION);
            d.getAlertCondition().clear();
            d.getAlertCondition().add(newCond);
        }

        response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        for (AlertDefinition d: response.getAlertDefinition()) {
            validateTypeDefinition(d);

            // validate updated properties
            assertEquals(UPDATED_PRIORITY, d.getPriority());
            assertEquals(UPDATED_ACTIVE, d.isActive());
            assertEquals(UPDATED_NAME, d.getName());
            assertEquals(UPDATED_DESCRIPTION, d.getDescription());
            for (AlertCondition c : d.getAlertCondition()) {
                assertTrue(c.getProperty().equals("otherProp"));
            }
            
            // validate child alert definitions
            AlertDefinitionsResponse childrenResponse = api.getAlertDefinitions(d);
            hqAssertSuccess(response);
            List<AlertDefinition> childrenDefinitions = childrenResponse.getAlertDefinition();
            assertTrue("No child alert definition exists for " + d.getName(),
                        childrenDefinitions.size() > 0);
            for (AlertDefinition child : childrenDefinitions) {
                validateDefinition(child);
                
                // TODO: uncomment when HHQ-3624 is fixed
                /*
                assertEquals(UPDATED_PRIORITY, child.getPriority());
                assertEquals(UPDATED_ACTIVE, child.isActive());
                assertEquals(UPDATED_NAME, child.getName());
                assertEquals(UPDATED_DESCRIPTION, child.getDescription());
                */
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

    public void testSyncEscalationTypeAlert() throws Exception {
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
            validateTypeDefinition(def);
            assertEquals(def.getEscalation().getId(),
                         escalationResponse.getEscalation().getId());
        }

        // Cleanup
        cleanup(response.getAlertDefinition());
        escApi.deleteEscalation(escalationResponse.getEscalation().getId());
    }

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
            assertNotNull("Escalation was null", def.getEscalation());
            assertEquals(def.getEscalation().getName(), e.getName());
        }
        
        // Cleanup
        cleanup(response.getAlertDefinition());
        escApi.deleteEscalation(escalationResponse.getEscalation().getId());
    }

    public void testSyncUpdateWithEscalationTypeAlert() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        EscalationApi escApi = api.getEscalationApi();

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), 1);
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateTypeDefinition(def);
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
            validateTypeDefinition(def);
            assertNotNull("Escalation was null", def.getEscalation());
            assertEquals(def.getEscalation().getName(), e.getName());
        }

        // Cleanup
        cleanup(response.getAlertDefinition());
        escApi.deleteEscalation(escalationResponse.getEscalation().getId());
    }

    public void testSyncUpdateRemoveEscalation() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        EscalationApi escApi = api.getEscalationApi();

        Resource platform = getLocalPlatformResource(false, false);

        // Re-sync with escalation
        Random r = new Random();
        Escalation e = new Escalation();
        e.setName("Test Escalation" + r.nextInt());
        EscalationResponse escalationResponse =
                escApi.createEscalation(e);
        hqAssertSuccess(escalationResponse);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        d.setEscalation(e);
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), 1);
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(def);
            assertNotNull("Resource was null", def.getResource());
            assertNotNull("Escalation was null", def.getEscalation());
        }


        definitions = response.getAlertDefinition();
        for (AlertDefinition def : definitions) {
            def.setEscalation(null);
        }

        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), 1);
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(def);
            assertNull("Escalation was not null", def.getEscalation());
        }

        // Cleanup
        cleanup(response.getAlertDefinition());
        escApi.deleteEscalation(escalationResponse.getEscalation().getId());
    }

    public void testSyncUpdateRemoveEscalationTypeAlert() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        EscalationApi escApi = api.getEscalationApi();

        Resource platform = getLocalPlatformResource(false, false);

        // Re-sync with escalation
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
            validateTypeDefinition(def);
            assertNotNull("Escalation was null", def.getEscalation());
        }


        definitions = response.getAlertDefinition();
        for (AlertDefinition def : definitions) {
            def.setEscalation(null);
        }

        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), 1);
        for (AlertDefinition def : response.getAlertDefinition()) {
            validateTypeDefinition(def);
            assertNull("Escalation was not null", def.getEscalation());
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
