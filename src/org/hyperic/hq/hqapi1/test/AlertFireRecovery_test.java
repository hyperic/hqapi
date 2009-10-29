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

    public void testWillRecoverAndFireRecoveryAlert() throws Exception {
        
        Resource platform = getLocalPlatformResource(false, false);
        
        AlertDefinition problemDef = createProblemAlertDefinition(true, platform, null, true);
        validateWillRecover(problemDef, true);
        
        AlertDefinition recoveryDef = createRecoveryAlertDefinition(true, problemDef);
        validateRecoveryAlertDefinition(recoveryDef, problemDef);
 
        long start = System.currentTimeMillis();

        // Insert a fake 'down' measurement so that
        // the problem alert definition will fire.
        sendAvailabilityDataPoint(platform, 0.0);

        Alert problemAlert = findAlert(problemDef, start);
        assertFalse("The problem alert should not be fixed",
                     problemAlert.isFixed());
        
        // Get the updated problem alert definition
        problemDef = getAlertDefinition(problemAlert.getAlertDefinitionId());
        validateWillRecover(problemDef, false);
        
        start = System.currentTimeMillis();
        
        // Insert a fake 'up' measurement so that
        // the recovery alert definition will fire.
        sendAvailabilityDataPoint(platform, 1);

        Alert recoveryAlert = findAlert(recoveryDef, start);
        assertTrue("The recovery alert should be fixed",
                    recoveryAlert.isFixed());
        
        // Get the updated problem alert
        problemAlert = getAlert(problemAlert.getId());
        assertTrue("The problem alert should be fixed",
                    problemAlert.isFixed());
        
        // Get the updated problem alert definition
        problemDef = getAlertDefinition(problemAlert.getAlertDefinitionId());
        validateWillRecover(problemDef, true);
        
        // Cleanup
        deleteAlertDefinitionByAlert(problemAlert);
        deleteAlertDefinitionByAlert(recoveryAlert);
    }
    
    // TODO testWillRecoverAndFireRecoveryTypeAlert
    // TODO testFireRecoveryAlertWithEscalation
    // TODO testFireRecoveryTypeAlertWithEscalation
    
    private AlertDefinition createProblemAlertDefinition(boolean sync,
                                                         Resource resource, 
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
        d.getAlertCondition().add(AlertDefinitionBuilder.
                createThresholdCondition(true, availMetric.getName(),
                                         AlertDefinitionBuilder.AlertComparator.EQUALS, 0));

        return (sync ? createAlertDefinition(d) : d);        
    }
      
    private AlertDefinition createRecoveryAlertDefinition(boolean sync,
                                                          AlertDefinition problemDef) 
        throws IOException {
        
        // Find availability metric for the passed in resource
        Metric availMetric = findAvailabilityMetric(problemDef.getResource());
       
        // Create recovery alert definition
        AlertDefinition recoveryDef = generateTestDefinition("Test Recovery Alert");
        recoveryDef.setResource(problemDef.getResource());
        AlertCondition threshold = 
            AlertDefinitionBuilder.createThresholdCondition(
                                        true, availMetric.getName(),
                                        AlertDefinitionBuilder.AlertComparator.EQUALS, 1);
        AlertCondition recovery =
            AlertDefinitionBuilder.createRecoveryCondition(true, problemDef);
        recoveryDef.getAlertCondition().add(threshold);
        recoveryDef.getAlertCondition().add(recovery);
        
        return (sync ? createAlertDefinition(recoveryDef) : recoveryDef);        
    }
    
    private void validateWillRecover(AlertDefinition def, boolean enabled) {
        assertTrue("The problem alert definition should be active",
                    def.isActive());
        assertTrue("The problem alert definition should be set to "
                        + "generate one alert and then disable until fixed",
                    def.isWillRecover());
        assertTrue("The problem alert definition's internal enabled flag should be " + enabled,
                    enabled == def.isEnabled());
    }
}
