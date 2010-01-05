package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertResponse;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.DataPoint;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlertFireRecovery_test extends AlertTestBase {

    public AlertFireRecovery_test(String name) {
        super(name);
    }

    public void testFireRecoveryAlert() throws Exception {
        createAndFireAlerts(null, false, false, false);
    }
    
    public void testFireRecoveryAlertWithEscalation() throws Exception {
        Escalation e = createEscalation();
        createAndFireAlerts(e, false, false, false);
        
        // Cleanup
        deleteEscalation(e);
    }
    
    public void testFireResourceTypeRecoveryAlert() throws Exception {
        createAndFireAlerts(null, true, false, false);
    }
        
    public void testFireResourceTypeRecoveryAlertWithEscalation()
        throws Exception {
        
        Escalation e = createEscalation();
        createAndFireAlerts(e, true, false, false);
        
        // Cleanup
        deleteEscalation(e);
    }
    
    public void testWillRecoverAndFireRecoveryAlert() throws Exception {
        createAndFireAlerts(null, false, false, true);
    }

    public void testWillRecoverAndFireRecoveryAlertWithEscalation() 
        throws Exception {
        
        Escalation e = createEscalation();
        createAndFireAlerts(e, false, false, true);
        
        // Cleanup
        deleteEscalation(e);        
    }
    
    public void testWillRecoverAndFireResourceTypeRecoveryAlert()
        throws Exception {
        
        createAndFireAlerts(null, true, false, true);
    }

    public void testWillRecoverAndFireResourceTypeRecoveryAlertWithEscalation()
        throws Exception {
    
        Escalation e = createEscalation();
        createAndFireAlerts(e, true, false, true);
        
        // Cleanup
        deleteEscalation(e);
    }
    
    /**
     * To validate HQ-1894
     */
    public void testAddRecoveryPostCreateAndFireRecoveryAlert() 
        throws Exception {
        
        createAndFireAlerts(null, false, true, false);
    }

    /**
     * To validate HQ-1894
     */
    public void testAddRecoveryPostCreateAndFireRecoveryAlertWithEscalation() 
        throws Exception {
        
        Escalation e = createEscalation();
        createAndFireAlerts(e, false, true, false);
        
        // Cleanup
        deleteEscalation(e);
    }
    
    /**
     * To validate HQ-1894
     */
    public void testAddRecoveryPostCreateAndFireResourceTypeRecoveryAlert() 
        throws Exception {
        
        createAndFireAlerts(null, true, true, false);
    }

    /**
     * To validate HQ-1894
     */
    public void testAddRecoveryPostCreateAndFireResourceTypeRecoveryAlertWithEscalation() 
        throws Exception {
        
        Escalation e = createEscalation();
        createAndFireAlerts(e, true, true, false);
        
        // Cleanup
        deleteEscalation(e);
    }
    
    /**
     * To validate HQ-1894
     */
    public void testWillRecoverAddRecoveryPostCreateAndFireRecoveryAlert() 
        throws Exception {
        
        createAndFireAlerts(null, false, true, true);
    }

    /**
     * To validate HQ-1894
     */
    public void testWillRecoverAddRecoveryPostCreateAndFireRecoveryAlertWithEscalation() 
        throws Exception {
        
        Escalation e = createEscalation();
        createAndFireAlerts(e, false, true, true);
        
        // Cleanup
        deleteEscalation(e);
    }
    
    /**
     * To validate HQ-1894
     */
    public void testWillRecoverAddRecoveryPostCreateAndFireResourceTypeRecoveryAlert() 
        throws Exception {
        
        createAndFireAlerts(null, true, true, true);
    }

    /**
     * To validate HQ-1894
     */
    public void testWillRecoverAddRecoveryPostCreateAndFireResourceTypeRecoveryAlertWithEscalation() 
        throws Exception {
     
        Escalation e = createEscalation();
        createAndFireAlerts(e, true, true, true);
        
        // Cleanup
        deleteEscalation(e);
    }
    
    /**
     * To validate HQ-1903
     */
    public void testFireProblemAlertAndThenCreateAndFireRecoveryAlert()
        throws Exception {
        
        Resource platform = getLocalPlatformResource(false, false);

        boolean willRecover = false;
        AlertDefinition problemDef = 
            createAvailabilityAlertDefinition(platform, null, false, willRecover, 0);
        Alert problemAlert = fireAvailabilityAlert(problemDef, willRecover, 0);

        String description = "Test for HQ-1903; Recovery Alert for " + problemDef.getName();
        AlertDefinition recoveryDef = 
            createRecoveryAlertDefinition(platform, problemDef, 
                                          description, false);
        fireRecoveryAlert(recoveryDef, problemAlert, willRecover);

        // Cleanup
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(problemDef);
        definitions.add(recoveryDef);
        cleanup(definitions);
    }
    
    private void createAndFireAlerts(Escalation escalation,
                                     boolean isResourceType,
                                     boolean addRecoveryPostCreate,
                                     boolean willRecover)
        throws Exception {

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition problemDef = 
            createAvailabilityAlertDefinition(platform, escalation, isResourceType, willRecover, 0);
        AlertDefinition recoveryDef = 
            createRecoveryAlertDefinition(platform, problemDef, addRecoveryPostCreate);

        AlertDefinition problemDefToFire = null;
        if (isResourceType) {
            problemDefToFire = getChildAlertDefinition(problemDef);
        } else {
            problemDefToFire = problemDef;
        }
        
        Alert problemAlert = fireAvailabilityAlert(problemDefToFire, willRecover, 0);
        
        AlertDefinition recoveryDefToFire = null;
        if (isResourceType) {
            recoveryDefToFire = getChildAlertDefinition(recoveryDef);
        } else {
            recoveryDefToFire = recoveryDef;
        }

        fireRecoveryAlert(recoveryDefToFire, problemAlert, willRecover);

        // Cleanup
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(problemDef);
        definitions.add(recoveryDef);
        cleanup(definitions);
    }
    
    private Alert fireRecoveryAlert(AlertDefinition recoveryDef, 
                                    Alert problemAlert,
                                    boolean willRecover)
        throws Exception {
        
        long start = System.currentTimeMillis();
        
        // Insert a fake 'up' measurement so that
        // the recovery alert definition will fire.
        sendAvailabilityDataPoint(recoveryDef.getResource(), 1);

        Alert recoveryAlert = findAlert(recoveryDef, start);
        assertTrue("The recovery alert should be fixed",
                    recoveryAlert.isFixed());

        // Get the updated recovery alert definition
        AlertDefinition updatedRecoveryDef = getAlertDefinition(recoveryDef.getId());
        validateAvailabilityAlertDefinition(updatedRecoveryDef, false, true);        

        // Get the updated problem alert
        problemAlert = getAlert(problemAlert.getId());
        assertTrue("The problem alert should be fixed",
                    problemAlert.isFixed());

        // Get the updated problem alert definition
        AlertDefinition problemDef = getAlertDefinition(problemAlert.getAlertDefinitionId());
        validateAvailabilityAlertDefinition(problemDef, willRecover, true);
        
        return recoveryAlert;
    }
    
    private AlertDefinition createRecoveryAlertDefinition(Resource resource,
                                                          AlertDefinition problemDef,
                                                          boolean addRecoveryPostCreate)
        throws Exception {
        
        return createRecoveryAlertDefinition(resource, problemDef, 
                                             null, addRecoveryPostCreate);
    }
    
    private AlertDefinition createRecoveryAlertDefinition(Resource resource,
                                                          AlertDefinition problemDef,
                                                          String description,
                                                          boolean addRecoveryPostCreate)
        throws Exception {
        
        // Find availability metric for the passed in resource
        Metric availMetric = findAvailabilityMetric(resource);
       
        // Create recovery alert definition
        boolean isResourceType = (problemDef.getResourcePrototype() != null);
        String name = "Test" + (isResourceType ? " Resource Type " : " ") + "Recovery Alert";
        AlertDefinition recoveryDef = generateTestDefinition(name);
        
        if (description == null) {
            recoveryDef.setDescription("Recovery Alert for " + problemDef.getName());
        } else {
            recoveryDef.setDescription(description);
        }
        
        if (isResourceType) {
            recoveryDef.setResourcePrototype(problemDef.getResourcePrototype());
        } else {
            recoveryDef.setResource(resource);
        }
        AlertCondition threshold = 
            AlertDefinitionBuilder.createThresholdCondition(
                                        true, availMetric.getName(),
                                        AlertDefinitionBuilder.AlertComparator.EQUALS, 1);
        recoveryDef.getAlertCondition().add(threshold);

        AlertCondition recovery =
            AlertDefinitionBuilder.createRecoveryCondition(true, problemDef);
        
        AlertDefinition newDef = null;
        if (addRecoveryPostCreate) {
            AlertDefinition tempDef = syncAlertDefinition(recoveryDef);
            tempDef.getAlertCondition().add(recovery);
            newDef = syncAlertDefinition(tempDef);
        } else {
            recoveryDef.getAlertCondition().add(recovery);
            newDef = syncAlertDefinition(recoveryDef);
        }
        
        if (isResourceType) {
            validateTypeDefinition(newDef);
        }
        
        validateRecoveryAlertDefinition(newDef, problemDef);
        
        return newDef;
    }
    
    private AlertDefinition getChildAlertDefinition(AlertDefinition parent) 
        throws IOException {
        
        AlertDefinitionsResponse childAlertDefResponse =
            getApi().getAlertDefinitionApi().getAlertDefinitions(parent);
        
        hqAssertSuccess(childAlertDefResponse);
        
        assertEquals("These tests assume only one child alert definition",
                     1, childAlertDefResponse.getAlertDefinition().size());
        
        AlertDefinition child = childAlertDefResponse.getAlertDefinition().get(0);
        validateDefinition(child);
        
        return child; 
    }
}
