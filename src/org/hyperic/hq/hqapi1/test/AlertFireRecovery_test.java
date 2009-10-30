package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
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
        createAndFireAlerts(false, false);
    }
    
    public void testWillRecoverAndFireRecoveryAlert() throws Exception {
        createAndFireAlerts(false, true);
    }
    
    /**
     * To validate HQ-1894
     */
    public void testAddRecoveryPostCreateAndFireRecoveryAlert() 
        throws Exception {
        
        createAndFireAlerts(true, false);
    }
    
    /**
     * To validate HQ-1894
     */
    public void testWillRecoverAddRecoveryPostCreateAndFireRecoveryAlert() 
        throws Exception {
        
        createAndFireAlerts(true, true);
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
        AlertDefinition problemDef = createProblemAlertDefinition(platform, null, willRecover);        
        Alert problemAlert = fireProblemAlert(problemDef, willRecover);

        AlertDefinition recoveryDef = createRecoveryAlertDefinition(problemDef, false);
        fireRecoveryAlert(recoveryDef, problemAlert, willRecover);

        // Cleanup
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(problemDef);
        definitions.add(recoveryDef);
        cleanup(definitions);
        */
    }
    
    private void createAndFireAlerts(boolean addRecoveryPostCreate,
                                     boolean willRecover)
        throws Exception {

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition problemDef = createProblemAlertDefinition(platform, null, willRecover);        
        AlertDefinition recoveryDef = createRecoveryAlertDefinition(problemDef, addRecoveryPostCreate);

        Alert problemAlert = fireProblemAlert(problemDef, willRecover);
        fireRecoveryAlert(recoveryDef, problemAlert, willRecover);

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
        problemDef = getAlertDefinition(problemAlert.getAlertDefinitionId());
        validateProblemAlertDefinitionAttributes(problemDef, 
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
    
    // TODO testWillRecoverAndFireRecoveryTypeAlert
    // TODO testFireRecoveryAlertWithEscalation
    // TODO testFireRecoveryTypeAlertWithEscalation
    
    private AlertDefinition createProblemAlertDefinition(Resource resource, 
                                                         Escalation e,
                                                         boolean willRecover) 
        throws IOException {
        
        // Find availability metric for the passed in resource
        Metric availMetric = findAvailabilityMetric(resource);

        // Create alert definition
        AlertDefinition d = generateTestDefinition("Test Problem Alert");
        d.setWillRecover(willRecover);
        d.setResource(resource);
        if (e != null) {
            d.setEscalation(e);
        }
        AlertCondition threshold =
            AlertDefinitionBuilder.createThresholdCondition(
                                        true, availMetric.getName(),
                                        AlertDefinitionBuilder.AlertComparator.EQUALS, 0);                                
        d.getAlertCondition().add(threshold);
        AlertDefinition newDef = syncAlertDefinition(d);
        
        validateProblemAlertDefinitionAttributes(newDef, willRecover, true);
        
        return newDef;        
    }
      
    private AlertDefinition createRecoveryAlertDefinition(AlertDefinition problemDef,
                                                          boolean addRecoveryPostCreate)
        throws Exception {
        
        // Find availability metric for the passed in resource
        Metric availMetric = findAvailabilityMetric(problemDef.getResource());
       
        // Create recovery alert definition
        AlertDefinition recoveryDef = generateTestDefinition("Test Recovery Alert");
        recoveryDef.setDescription("Recovery Alert for " + problemDef.getName());
        recoveryDef.setResource(problemDef.getResource());
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
        
        validateRecoveryAlertDefinition(newDef, problemDef);
        
        return newDef;
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
