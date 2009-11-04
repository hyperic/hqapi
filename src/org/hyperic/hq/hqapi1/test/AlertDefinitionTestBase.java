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
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertConditionType;
import org.hyperic.hq.hqapi1.types.AlertAction;
import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionResponse;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class AlertDefinitionTestBase extends HQApiTestBase {

    public AlertDefinitionTestBase(String name) {
        super(name);
    }

    protected AlertDefinition generateTestDefinition() {
        return generateTestDefinition("Test Alert");
    }
    
    protected AlertDefinition generateTestDefinition(String name) {
        AlertDefinition d = new AlertDefinition();

        Random r = new Random();
        d.setName(name + " Definition" + r.nextInt());
        d.setDescription(name + " Description");
        d.setPriority(AlertPriority.MEDIUM.getPriority());
        d.setActive(true);
        return d;
    }
    
    protected AlertDefinition syncAlertDefinition(AlertDefinition d) 
        throws IOException {
                
        return syncAlertDefinitions(Collections.singletonList(d)).get(0);        
    }
    
    protected List<AlertDefinition> syncAlertDefinitions(List<AlertDefinition> definitions) 
        throws IOException {

        AlertDefinitionApi defApi = getApi().getAlertDefinitionApi();

        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        int expectedSize = definitions.size();
        assertEquals("Should have found " + expectedSize + " alert definitions from sync",
                      expectedSize, response.getAlertDefinition().size());
        
        for (AlertDefinition d : response.getAlertDefinition()) {
            validateDefinition(d);
        }
        
        return response.getAlertDefinition();
    }
    
    protected AlertDefinition createProblemAlertDefinition(Resource resource, 
                                                           Escalation e,
                                                           boolean isResourceType,
                                                           boolean willRecover) 
        throws IOException {

        // Find availability metric for the passed in resource
        Metric availMetric = findAvailabilityMetric(resource);

        // Create alert definition
        String name = "Test" + (isResourceType ? " Resource Type " : " ") + "Problem Alert";
        AlertDefinition d = generateTestDefinition(name);
        d.setWillRecover(willRecover);
        if (isResourceType) {
            d.setResourcePrototype(resource.getResourcePrototype());
        } else {
            d.setResource(resource);
        }
        if (e != null) {
            d.setEscalation(e);
        }
        AlertCondition threshold =
            AlertDefinitionBuilder.createThresholdCondition(
                                        true, availMetric.getName(),
                                        AlertDefinitionBuilder.AlertComparator.EQUALS, 0);                                
        d.getAlertCondition().add(threshold);
        AlertDefinition newDef = syncAlertDefinition(d);
        
        if (isResourceType) {
            validateTypeDefinition(newDef);
        }
        
        validateProblemAlertDefinition(newDef, willRecover, true);
        
        assertTrue("The problem alert definition should have " 
                        + ((e == null) ? "no" : "an") + " escalation",
                    (e == null) ? newDef.getEscalation() == null 
                                : newDef.getEscalation() != null);
        
        return newDef;
    }
    
    protected AlertDefinition getAlertDefinition(Integer id) 
        throws IOException {
        
        AlertDefinitionApi defApi = getApi().getAlertDefinitionApi();

        AlertDefinitionResponse response = defApi.getAlertDefinition(id);
        hqAssertSuccess(response);
        AlertDefinition d = response.getAlertDefinition();
        validateDefinition(d);
        
        return d;
    }
    
    protected void validateDefinition(AlertDefinition d) {
        assertNotNull("Alert definition id is null",
                      d.getId());
        assertNotNull("Alert definition name is null",
                      d.getName());
        assertTrue("Invalid frequency " + d.getFrequency(),
                   d.getFrequency() >= 0 && d.getFrequency() <= 4);
        assertTrue("Invalid priority " + d.getPriority(),
                   d.getPriority() >= 1 & d.getPriority() <= 3);
    }
    
    protected void validateTypeDefinition(AlertDefinition d) {
        validateDefinition(d);

        // Type alerts have parent == 0
        assertTrue("Invalid parent id " + d.getParent() +
                   " for type definition " + d.getName(),
                   d.getParent() == 0);
        assertTrue("No ResourcePrototype found for type based alert",
                   d.getResourcePrototype() != null);
    }
    
    protected void validateProblemAlertDefinition(AlertDefinition def,
                                                  boolean willRecover,
                                                  boolean enabled) {
        assertTrue("The problem alert definition should be active",
                    def.isActive());
        assertTrue("The problem alert definition's willRecover flag should be " + willRecover,
                    willRecover == def.isWillRecover());
        assertTrue("The problem alert definition's internal enabled flag should be " + enabled,
                    enabled == def.isEnabled());
    }
    
    protected void validateRecoveryAlertDefinition(List<AlertDefinition> alertDefinitions)
        throws Exception {

        assertEquals("Cannot perform validation. Invalid number of alert definitions.",
                      2, alertDefinitions.size());

        AlertDefinition recoveryDef = null;
        
        // find recovery alert definition
        for (AlertDefinition d : alertDefinitions) {            
            for (AlertCondition c : d.getAlertCondition()) {
                if (c.getType() == AlertConditionType.RECOVERY.getType()) {
                    assertNotNull("Recovery alert definition does not have a valid recovery condition",
                                  c.getRecover());
                    recoveryDef = d;
                    break;
                }
            }
        }
        
        assertNotNull("A recovery alert definition could not be found",
                      recoveryDef);
        
        AlertDefinition problemDef = null;
        
        // find problem alert definition
        for (AlertDefinition d : alertDefinitions) {
            if (!d.getId().equals(recoveryDef.getId())) {
                problemDef = d;
                break;
            }
        }        

        assertNotNull("A problem alert definition could not be found",
                       problemDef);
        
        validateRecoveryAlertDefinition(recoveryDef, problemDef);
    }
    
    protected void validateRecoveryAlertDefinition(AlertDefinition recoveryDef,
                                                   AlertDefinition problemDef) 
        throws Exception {
        
        AlertDefinitionApi defApi = getApi().getAlertDefinitionApi();

        validateDefinition(recoveryDef);
        validateDefinition(problemDef);        

        // get alert definitions with internal alert actions
        AlertDefinitionResponse getRecoveryDefResponse = defApi.getAlertDefinition(recoveryDef.getId());
        hqAssertSuccess(getRecoveryDefResponse);
        AlertDefinition recovery = getRecoveryDefResponse.getAlertDefinition();
        
        AlertDefinitionResponse getProblemDefResponse = defApi.getAlertDefinition(problemDef.getId());
        hqAssertSuccess(getProblemDefResponse);
        AlertDefinition problem = getProblemDefResponse.getAlertDefinition();
        
        // validate escalation
        assertNull("Recovery alert definition should not have an escalation",
                    recovery.getEscalation());
        
        // validate conditions
        assertEquals("Recovery alert definition has an invalid number of alert conditions",
                     2, recovery.getAlertCondition().size());

        AlertCondition recoveryCondition = null;
        
        for (AlertCondition c : recovery.getAlertCondition()) {
            if (c.getType() == AlertConditionType.RECOVERY.getType()) {
                // TODO: Need to match on the alert definition id
                // since resource type alert definitions will have
                // the same alert definition name
                assertEquals("Recovery alert definition does not have a valid recovery condition",
                             problem.getName(), c.getRecover());
                recoveryCondition = c;
            }
        }

        assertNotNull("No recovery alert condition could be found for the recovery alert definition",
                      recoveryCondition);

        // validate actions
        assertTrue("Recovery alert definition has an invalid number of alert actions",
                    recovery.getAlertAction().size() >= 1);

        Set expectedAlertActions = new HashSet();
        expectedAlertActions.add("com.hyperic.hq.bizapp.server.action.alert.EnableAlertDefAction"); 
        
        for (AlertCondition c : recovery.getAlertCondition()) {
            if (c.getType() == AlertConditionType.THRESHOLD.getType()
                    || c.getType() == AlertConditionType.BASELINE.getType()
                    || c.getType() == AlertConditionType.METRIC_CHANGE.getType()) {
                
                expectedAlertActions.add("org.hyperic.hq.measurement.action.MetricAlertAction");                
            }
        }

        Set actualAlertActions = new HashSet();
        
        for (AlertAction a : recovery.getAlertAction()) {
            assertTrue("There was a duplicate alert action: " + a.getClassName(),
                        actualAlertActions.add(a.getClassName()));
        }
        
        assertEquals("Recovery alert definition has missing or unexpected alert actions.",
                     expectedAlertActions, actualAlertActions);
        
        // if it is a resource type alert definition
        // then validate the child recovery alert definition 
        if (recoveryDef.getResourcePrototype() != null) {
            validateChildRecoveryAlertDefinition(recoveryDef, problemDef);
        }
    }
    
    private void validateChildRecoveryAlertDefinition(AlertDefinition parentRecoveryDef,
                                                      AlertDefinition parentProblemDef)
        throws Exception {
        
        AlertDefinitionApi defApi = getApi().getAlertDefinitionApi();

        validateTypeDefinition(parentRecoveryDef);
        validateTypeDefinition(parentProblemDef);

        // get child alert definitions
        AlertDefinitionsResponse childRecoveryResponse = defApi.getAlertDefinitions(parentRecoveryDef);
        hqAssertSuccess(childRecoveryResponse);
        assertEquals("These tests assume only one child alert definition",
                     1, childRecoveryResponse.getAlertDefinition().size());
        AlertDefinition childRecoveryDef = childRecoveryResponse.getAlertDefinition().get(0);

        AlertDefinitionsResponse childProblemResponse = defApi.getAlertDefinitions(parentProblemDef);
        hqAssertSuccess(childProblemResponse);
        assertEquals("These tests assume only one child alert definition",
                     1, childProblemResponse.getAlertDefinition().size());
        AlertDefinition childProblemDef = childProblemResponse.getAlertDefinition().get(0);

        // validate child recovery alert definition
        validateRecoveryAlertDefinition(childRecoveryDef, childProblemDef);
    }
    
    protected void cleanup(List<AlertDefinition> definitions) throws IOException {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        for (AlertDefinition d : definitions) {
            StatusResponse deleteResponse = api.deleteAlertDefinition(d.getId());
            hqAssertSuccess(deleteResponse);
        }
    }
}
