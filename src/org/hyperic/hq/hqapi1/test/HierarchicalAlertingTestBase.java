package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ServerConfigApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.ServerConfigResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.ServerConfig;
import org.hyperic.hq.hqapi1.types.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class HierarchicalAlertingTestBase extends AlertTestBase {

    public HierarchicalAlertingTestBase(String name) {
        super(name);
    }
    
    protected void simulatePlatformDownServersDown(boolean enabled) 
        throws Exception {

        boolean previousState = enableHierarchicalAlerting(enabled);
        
        Resource platform = getLocalPlatformResource(false, true);
        AlertDefinition platformAlertDef =
            createProblemAlertDefinition(platform, null, false, true);
        
        List<AlertDefinition> problemAlertDefs = new ArrayList<AlertDefinition>();
        for (Resource server : platform.getResource()) {
            String serverPrototype = server.getResourcePrototype().getName();
            
            if (serverPrototype.equals("JBoss 4.2")
                    || serverPrototype.equals("Tomcat 6.0")
                    || serverPrototype.equals("HQ Agent")) {
                
                // set alert definition to willRecover=true
                // so that it will fire only once
                problemAlertDefs.add(createProblemAlertDefinition(server, null, false, true));
            }
        }
        
        assertTrue("This test needs at least 1 server problem alert definition",
                    problemAlertDefs.size() > 0);

        long start = System.currentTimeMillis();
        
        // Insert a fake 'down' measurement so that
        // the problem alert definitions will fire.
        sendAvailabilityDataPoint(platform, 0.0);
        
        for (AlertDefinition ad : problemAlertDefs) {
            sendAvailabilityDataPoint(ad.getResource(), 0.0);
        }
                
        // check that the platform problem alert fired
        Alert problemAlert = findAlert(platformAlertDef, start);
        assertFalse("The problem alert should not be fixed",
                     problemAlert.isFixed());
        
        // check the server problem alerts
        boolean childAlertsFired = didProblemAlertsFire(problemAlertDefs, start);
        assertTrue("Hierarchical alerting enabled status is " + enabled
                        + ". The child alerts should " + (enabled ? " not " : "") + " fire.",
                    enabled != childAlertsFired);
        
        // reset HQ back to normal state
        if (previousState != enabled) {
            enableHierarchicalAlerting(previousState);            
        }
        problemAlertDefs.add(platformAlertDef);
        for (AlertDefinition ad : problemAlertDefs) {
            sendAvailabilityDataPoint(ad.getResource(), 1);
        }
        
        // cleanup
        cleanup(problemAlertDefs);
    }
    
    /**
     * @param enable The boolean value to set HQ_HIERARCHICAL_ALERTING_ENABLED
     * 
     * @return boolean The previous state of HQ_HIERARCHICAL_ALERTING_ENABLED
     */
    private boolean enableHierarchicalAlerting(boolean enable) 
        throws IOException {
        
        String alertingConfigKey = "HQ_HIERARCHICAL_ALERTING_ENABLED";
        
        ServerConfigApi sApi = getApi().getServerConfigApi();

        ServerConfigResponse configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);

        Boolean previousState = null;
        boolean updateConfig = false;
        List<ServerConfig> configs = configResponse.getServerConfig();
        
        for (ServerConfig config : configs) {
            if (alertingConfigKey.equals(config.getKey())) {
                previousState = Boolean.valueOf(config.getValue());
                if (previousState.booleanValue() != enable) {
                    config.setValue(Boolean.valueOf(enable).toString());
                    updateConfig = true;
                }
                break;
            }
        }
        
        assertNotNull("No server configuration found for " + alertingConfigKey,
                      previousState);

        if (updateConfig) {
            StatusResponse response = sApi.setConfig(configs);
            hqAssertSuccess(response);

            // Validate update
            configResponse = sApi.getConfig();
            hqAssertSuccess(configResponse);

            configs = configResponse.getServerConfig();
            for (ServerConfig config : configs) {
                if (alertingConfigKey.equals(config.getKey())) {
                    assertTrue(alertingConfigKey + " was not " + enable,
                               config.getValue().equals(Boolean.valueOf(enable).toString()));
                }
            }
        }
        
        return previousState.booleanValue();
    }
    
    private boolean didProblemAlertsFire(List<AlertDefinition> defs, long start) 
        throws IOException {
        
        final int TIMEOUT = 30;
        for (int i = 0; i < TIMEOUT; i++) {
            // Wait for alerts
            List<Alert> allAlerts = new ArrayList<Alert>();
            List<Integer> alertDefIds = new ArrayList<Integer>();
            for (AlertDefinition def : defs) {
                AlertsResponse alertsResponse = 
                    getAlertApi().findAlerts(def.getResource(), start,
                                             System.currentTimeMillis(),
                                             10, 1, 
                                             (def.getEscalation() != null), 
                                             false);
                hqAssertSuccess(alertsResponse);
                allAlerts.addAll(alertsResponse.getAlert());
                alertDefIds.add(def.getId());
            }
            
            List<Alert> problemAlerts = new ArrayList<Alert>();
            for (Alert a : allAlerts) {
                // Verify this alert comes from the definition we just created
                Integer adId = Integer.valueOf(a.getAlertDefinitionId());
                if (alertDefIds.contains(adId)) {
                    validateAlert(a);
                    assertFalse("The problem alert should not be fixed",
                                 a.isFixed());
                    
                    problemAlerts.add(a);
                    
                    // remove and search for the others
                    alertDefIds.remove(adId);                    
                }
            }
            
            if (defs.size() == problemAlerts.size()) {
                // all alerts for the alert definitions fired
                return true;
            }

            pauseTest(500);
        }
        return false;
    }
}
