package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.EscalationActionBuilder;
import org.hyperic.hq.hqapi1.EscalationActionBuilder.EscalationActionType;
import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.ServerConfigApi;
import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.AlertActionLog;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.EscalationAction;
import org.hyperic.hq.hqapi1.types.ServerConfigResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.ServerConfig;
import org.hyperic.hq.hqapi1.types.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SNMPTestBase extends AlertTestBase {

    protected final String INVALID_SECURITY_NAME = "invaliduser";
    protected final String INVALID_COMMUNITY = "invalidcommunity";
    protected final String INVALID_VARIABLE_BINDINGS = "[{oid:100,7}]";

    protected final String SNMP_VERSION = "SNMP_VERSION";
    protected final String SNMP_AUTH_PROTOCOL = "SNMP_AUTH_PROTOCOL";
    protected final String SNMP_AUTH_PASSPHRASE = "SNMP_AUTH_PASSPHRASE";
    protected final String SNMP_PRIVACY_PROTOCOL = "SNMP_PRIVACY_PROTOCOL";
    protected final String SNMP_PRIV_PASSPHRASE = "SNMP_PRIV_PASSPHRASE";
    protected final String SNMP_COMMUNITY = "SNMP_COMMUNITY";
    protected final String SNMP_ENGINE_ID = "SNMP_ENGINE_ID";
    protected final String SNMP_CONTEXT_NAME = "SNMP_CONTEXT_NAME";
    protected final String SNMP_SECURITY_NAME = "SNMP_SECURITY_NAME";
    protected final String SNMP_TRAP_OID = "SNMP_TRAP_OID";
    protected final String SNMP_DEFAULT_NOTIFICATION_MECHANISM = "SNMP_DEFAULT_NOTIFICATION_MECHANISM";
    
    public SNMPTestBase(String name) {
        super(name);
    }
    
    /**
     *  Validate escalation action logs
     */
    public abstract void validateEscalationActionLogs(String user, 
                                                      List<AlertActionLog> actionLogs);

    public abstract String getProtocolVersion();

    private String getIPAddress() {
        return System.getProperty("snmp-address");
    }
    
    private String getOID() {
        return System.getProperty("snmp-oid");
    }
    
    protected String getVariableBindings() {
        return System.getProperty("snmp-variable-bindings");
    }
    
    protected String getSecurityName() {
        return System.getProperty("snmp-security-name");
    }
    
    protected String getCommunity() {
        return System.getProperty("snmp-community");
    }
    
    private String getAuthProtocol() {
        return System.getProperty("snmp-auth-protocol");
    }
    
    private String getAuthPassphrase() {
        return System.getProperty("snmp-auth-passphrase");
    }
    
    private String getPrivProtocol() {
        return System.getProperty("snmp-priv-protocol");
    }
    
    private String getPrivPassphrase() {
        return System.getProperty("snmp-priv-passphrase");
    }
    
    /**
     * Send notifications using SNMPv1 or SNMPv2c
     */
    protected void testSendNotification(String protocolVersion,
                                        String notificationMechanism,
                                        String community,
                                        String varbinds) 
        throws Exception {

        testSendNotification(
                protocolVersion,
                notificationMechanism,
                community,
                null,
                false,
                false,
                varbinds);
    }

    /**
     * Send notications using SNMPv3
     */
    protected void testSendNotification(String protocolVersion,
                                        String notificationMechanism,
                                        String user,
                                        boolean auth,
                                        boolean priv,
                                        String varbinds) 
        throws Exception {
        
        testSendNotification(
                protocolVersion,
                notificationMechanism,
                null,
                user,
                auth,
                priv,
                varbinds);
    }
    
    private void testSendNotification(String protocolVersion,
                                      String notificationMechanism,
                                      String community,
                                      String user,
                                      boolean auth,
                                      boolean priv,
                                      String varbinds) 
        throws Exception {
        
        // Skip test if no SNMP IP address exists
        if (getIPAddress() == null) {
            System.out.println("SNMP IP address is null, skipping test");
            return;
        }
                
        // Create escalation with SNMP action
        Escalation e = null;
        
        EscalationAction action = 
            EscalationActionBuilder.createSnmpAction(
                    60000,
                    getIPAddress(),
                    notificationMechanism,
                    getOID(),
                    varbinds);
        
        if (INVALID_VARIABLE_BINDINGS.equals(varbinds)) {
            e = createEscalation();
            e.getAction().clear();
            e.getAction().add(action);
            
            EscalationApi escApi = getApi().getEscalationApi();           
            StatusResponse syncResponse = 
                escApi.syncEscalations(Collections.singletonList(e));
            hqAssertFailureInvalidParameters(syncResponse);
            
        } else {
            e = createEscalation(action);

            // Validate escalation
            assertTrue("SNMP escalation action is missing",
                       e.getAction().size() == 1);
            
            assertEquals("Wrong escalation action",
                         EscalationActionType.SNMP.getType(),
                         e.getAction().get(0).getActionType());

            // Update HQ SNMP server settings
            Map<String, String> oldConfig = 
                updateServerConfig(getSnmpConfig(protocolVersion,
                                                 notificationMechanism,
                                                 community,
                                                 user, 
                                                 auth, 
                                                 priv));

            // Create alert definition
            boolean willRecover = true;
            double availability = 1;
            Resource platform = getLocalPlatformResource(false, false);
            AlertDefinition alertDef = 
                createAvailabilityAlertDefinition(platform, e, false, willRecover, availability);
            
            // Fire alert
            Alert alert = fireAvailabilityAlert(alertDef, true, willRecover, availability);
            
            // Validate escalation action logs
            validateEscalationActionLogs(user, alert.getAlertActionLog());
            
            // Reset HQ server settings
            updateServerConfig(oldConfig);
            
            // Cleanup
            cleanup(Collections.singletonList(alertDef));
        }
        
        deleteEscalation(e);
    }

    private Map<String, String> getSnmpConfig(String protocolVersion,
                                              String notificationMechanism,
                                              String community,
                                              String user, 
                                              boolean auth, 
                                              boolean priv) {
        
        Map<String, String> snmpConfigs = new HashMap<String, String>();
        
        snmpConfigs.put(SNMP_VERSION, protocolVersion);
        
        if ("3".equals(protocolVersion)) {                        
            assertNotNull("Security Name is required",
                          user);
            
            snmpConfigs.put(SNMP_COMMUNITY, "");
            snmpConfigs.put(SNMP_SECURITY_NAME, user);
        } else {
            assertNotNull("Community is required",
                          community);
            
            snmpConfigs.put(SNMP_COMMUNITY, community);
            snmpConfigs.put(SNMP_SECURITY_NAME, "");
        }
        
        snmpConfigs.put(SNMP_DEFAULT_NOTIFICATION_MECHANISM, notificationMechanism);
                
        String authProtocol = getAuthProtocol();
        String authPassphrase = getAuthPassphrase();
        if (auth) {
            assertNotNull("SNMP authentication protocol is required",
                          authProtocol);
            assertNotNull("SNMP authentication passphrase is required",
                          authPassphrase);
        } else {
            authProtocol = "";
            authPassphrase = "";
        }
        snmpConfigs.put(SNMP_AUTH_PROTOCOL, authProtocol);        
        snmpConfigs.put(SNMP_AUTH_PASSPHRASE, authPassphrase);
        
        String privProtocol = getPrivProtocol();
        String privPassphrase = getPrivPassphrase();
        if (priv) {
            assertNotNull("SNMP privacy protocol is required",
                          privProtocol);
            assertNotNull("SNMP privacy passphrase is required",
                          privPassphrase);
        } else {
            privProtocol = "";
            privPassphrase = "";
        }
        snmpConfigs.put(SNMP_PRIVACY_PROTOCOL, privProtocol);
        snmpConfigs.put(SNMP_PRIV_PASSPHRASE, privPassphrase);
        
        return snmpConfigs;
    }
    
    /**
     * @param snmpConfigs The new SNMP Server Configuration Properties as a Map
     * 
     * @return Map<String, String> The previous HQ Server Settings
     */
    private Map<String, String> updateServerConfig(Map<String, String> snmpConfigs) 
        throws IOException {
                
        ServerConfigApi sApi = getApi().getServerConfigApi();
        
        // Get current HQ server settings
        ServerConfigResponse configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);
        List<ServerConfig> configs = configResponse.getServerConfig();
        Map<String, String> oldConfigs = new HashMap<String, String>(configs.size());
        
        // Update with new SNMP settings
        for (ServerConfig config : configs) {
            String key = config.getKey();
            oldConfigs.put(key, config.getValue());

            if (snmpConfigs.containsKey(key)) {
                config.setValue(snmpConfigs.get(key));
            }
        }
        
        StatusResponse response = sApi.setConfig(configs);
        hqAssertSuccess(response);

        // Validate update
        configResponse = sApi.getConfig();
        hqAssertSuccess(configResponse);
        List<ServerConfig> updatedConfigs = configResponse.getServerConfig();
                
        for (ServerConfig config : updatedConfigs) {
            String key = config.getKey();
            if (snmpConfigs.containsKey(key)) {
                assertTrue(key + " was not updated",
                           snmpConfigs.get(key).equals(config.getValue()));
            }
        }
        
        // return old HQ server settings so that HQ can be reset
        // to it's original state before the test
        return oldConfigs;
    }
}
