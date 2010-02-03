package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.EscalationActionBuilder;
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

public class SNMPv3InformAuthNoPriv_test extends SNMPv3InformTestBase {
        
    public SNMPv3InformAuthNoPriv_test(String name) {
        super(name);
    }
    
    // TODO: ResourceType Alert Definitions
    
    public void testSendVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "Inform",
                getSecurityName(),
                true, false, 
                getVariableBindings());
    }
    
    public void testSendVarBindsNoUser() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "Inform", 
                "", 
                true, false, 
                getVariableBindings());
    }

    public void testSendVarBindsInvalidUser() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "Inform", 
                INVALID_SECURITY_NAME, 
                true, false, 
                getVariableBindings());
    }
    
    public void testSendNoVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "Inform",
                getSecurityName(),
                true, false, 
                "");
    }
    
    public void testSendNoVarbindsNoUser() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "Inform", 
                "", 
                true, false, 
                "");
    }

    public void testSendNoVarbindsInvalidUser() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "Inform", 
                INVALID_SECURITY_NAME, 
                true, false, 
                "");
    }
    
    public void testSendInvalidVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "Inform",
                getSecurityName(),
                true, false, 
                INVALID_VARIABLE_BINDINGS);
    }
}
