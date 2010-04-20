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

public class SNMPv3TrapAuthPriv_test extends SNMPTestBase {
   
    public SNMPv3TrapAuthPriv_test(String name) {
        super(name);
    }

    public String getProtocolVersion() {
        return "3";
    }
    
    // TODO: ResourceType Alert Definitions

    public void testSendv1TrapVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(), 
                "v1 Trap",
                getSecurityName(),
                true, true, 
                getVariableBindings());
    }

    public void testSendv1TrapInvalidVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(), 
                "v1 Trap",
                getSecurityName(),
                true, true, 
                INVALID_VARIABLE_BINDINGS);
    }
    
    public void testSendv2cTrapVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(), 
                "v2c Trap",
                getSecurityName(),
                true, true, 
                getVariableBindings());
    }
    
    public void testSendv2cTrapVarBindsNoUser() throws Exception {
        testSendNotification(
                getProtocolVersion(), 
                "v2c Trap", 
                "", 
                true, true, 
                getVariableBindings());
    }

    public void testSendv2cTrapVarBindsInvalidUser() throws Exception {
        testSendNotification(
                getProtocolVersion(), 
                "v2c Trap", 
                INVALID_SECURITY_NAME, 
                true, true, 
                getVariableBindings());
    }
    
    public void testSendv2cTrapNoVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(), 
                "v2c Trap",
                getSecurityName(),
                true, true, 
                "");
    }
    
    public void testSendv2cTrapNoVarbindsNoUser() throws Exception {
        testSendNotification(
                getProtocolVersion(), 
                "v2c Trap", 
                "", 
                true, true, 
                "");
    }

    public void testSendv2cTrapNoVarbindsInvalidUser() throws Exception {
        testSendNotification(
                getProtocolVersion(), 
                "v2c Trap", 
                INVALID_SECURITY_NAME, 
                true, true, 
                "");
    }
    
    public void testSendv2cTrapInvalidVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(), 
                "v2c Trap",
                getSecurityName(),
                true, true, 
                INVALID_VARIABLE_BINDINGS);
    }

    /**
     *  Validate escalation action logs
     */
    public void validateEscalationActionLogs(String user, 
                                             List<AlertActionLog> actionLogs) {
        
        assertTrue("At least one alert escalation log is needed",
                   !actionLogs.isEmpty());
        
        String logDetail = actionLogs.get(0).getDetail();
        System.out.println(logDetail);

        if (user.length() == 0) {
            // empty string user means a "no security name" test case
            assertTrue("The HQ server settings should have no SNMP Security Name",
                       logDetail.indexOf("Security Name required") > -1);
        } else {
            assertTrue("TRAP notification was not sent",
                       logDetail.indexOf("TRAP sent successfully") > -1);
        }
    }
}
