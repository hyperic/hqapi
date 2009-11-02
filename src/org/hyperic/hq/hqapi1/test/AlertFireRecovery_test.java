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
    
    public void testFireResourceTypeRecoveryAlert() throws Exception {
        createAndFireAlerts(null, true, false, false);
    }

    public void testFireRecoveryAlertWithEscalation() throws Exception {
        Escalation e = createEscalation();
        createAndFireAlerts(e, false, false, false);
        
        // Cleanup
        deleteEscalation(e);
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
    
    public void testWillRecoverAndFireResourceTypeRecoveryAlert()
        throws Exception {
        
        createAndFireAlerts(null, true, false, true);
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
    public void testAddRecoveryPostCreateAndFireResourceTypeRecoveryAlert() 
        throws Exception {
        
        createAndFireAlerts(null, true, true, false);
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
    public void testWillRecoverAddRecoveryPostCreateAndFireResourceTypeRecoveryAlert() 
        throws Exception {
        
        createAndFireAlerts(null, true, true, true);
    }
    
    /**
     * To validate HQ-1903
     */
    public void testFireProblemAlertAndThenCreateAndFireRecoveryAlert()
        throws Exception {

        // TODO: Uncomment the test code when HQ-1903 is fixed
        
        /*
        Resource platform = getLocalPlatformResource(false, false);

        boolean willRecover = false;
        AlertDefinition problemDef = createProblemAlertDefinition(platform, null, false, willRecover);
        Alert problemAlert = fireProblemAlert(problemDef, willRecover);

        AlertDefinition recoveryDef = createRecoveryAlertDefinition(platform, problemDef, false);
        fireRecoveryAlert(recoveryDef, problemAlert, willRecover);

        // Cleanup
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(problemDef);
        definitions.add(recoveryDef);
        cleanup(definitions);
        */
    }
    
    private void createAndFireAlerts(Escalation escalation,
                                     boolean isResourceType,
                                     boolean addRecoveryPostCreate,
                                     boolean willRecover)
        throws Exception {

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition problemDef = 
            createProblemAlertDefinition(platform, escalation, isResourceType, willRecover);
        AlertDefinition recoveryDef = 
            createRecoveryAlertDefinition(platform, problemDef, addRecoveryPostCreate);

        AlertDefinition problemDefToFire = null;
        if (isResourceType) {
            problemDefToFire = getChildAlertDefinition(problemDef);
        } else {
            problemDefToFire = problemDef;
        }
        
        Alert problemAlert = fireProblemAlert(problemDefToFire, willRecover);
        
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
    
    private Alert fireProblemAlert(AlertDefinition problemDef,
                                   boolean willRecover)
        throws Exception {
        
        long start = System.currentTimeMillis();
        
        // Insert a fake 'down' measurement so that
        // the problem alert definition will fire.
        sendAvailabilityDataPoint(problemDef.getResource(), 0.0);

        Alert problemAlert = findAlert(problemDef, start);
        assertFalse("The problem alert should not be fixed",
                     problemAlert.isFixed());

        // Get the updated problem alert definition
        AlertDefinition updatedDef = getAlertDefinition(problemAlert.getAlertDefinitionId());
        validateProblemAlertDefinitionAttributes(updatedDef, 
                                                 willRecover, 
                                                 willRecover ? false : true);
        
        return problemAlert;
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
        
        // Get the updated problem alert
        problemAlert = getAlert(problemAlert.getId());
        assertTrue("The problem alert should be fixed",
                    problemAlert.isFixed());

        // Get the updated problem alert definition
        AlertDefinition problemDef = getAlertDefinition(problemAlert.getAlertDefinitionId());
        validateProblemAlertDefinitionAttributes(problemDef, willRecover, true);
        
        return recoveryAlert;
    }
    
    private AlertDefinition createProblemAlertDefinition(Resource resource, 
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
        
        validateProblemAlertDefinitionAttributes(newDef, willRecover, true);
        
        assertTrue("The problem alert definition should have " 
                        + ((e == null) ? "no" : "an") + " escalation",
                    (e == null) ? newDef.getEscalation() == null 
                                : newDef.getEscalation() != null);
        
        return newDef;        
    }
      
    private AlertDefinition createRecoveryAlertDefinition(Resource resource,
                                                          AlertDefinition problemDef,
                                                          boolean addRecoveryPostCreate)
        throws Exception {
        
        // Find availability metric for the passed in resource
        Metric availMetric = findAvailabilityMetric(resource);
       
        // Create recovery alert definition
        boolean isResourceType = (problemDef.getResourcePrototype() != null);
        String name = "Test" + (isResourceType ? " Resource Type " : " ") + "Recovery Alert";
        AlertDefinition recoveryDef = generateTestDefinition(name);
        recoveryDef.setDescription("Recovery Alert for " + problemDef.getName());
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
    
    private void validateProblemAlertDefinitionAttributes(AlertDefinition def,
                                                          boolean willRecover,
                                                          boolean enabled) {
        assertTrue("The problem alert definition should be active",
                    def.isActive());
        assertTrue("The problem alert definition's willRecover flag should be " + willRecover,
                    willRecover == def.isWillRecover());
        assertTrue("The problem alert definition's internal enabled flag should be " + enabled,
                    enabled == def.isEnabled());
    }
}
