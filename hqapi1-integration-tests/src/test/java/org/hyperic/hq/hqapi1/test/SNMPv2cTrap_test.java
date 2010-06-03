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

public class SNMPv2cTrap_test extends SNMPTestBase {
    
    public SNMPv2cTrap_test(String name) {
        super(name);
    }

    public String getProtocolVersion() {
        return "2c";
    }
    
    // TODO: ResourceType Alert Definitions
    
    public void testSendV2CTrapVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "v2c Trap",
                getCommunity(),
                getVariableBindings());
    }

    public void testSendV2CTrapVarBindsInvalidCommunity() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "v2c Trap",
                INVALID_COMMUNITY,
                getVariableBindings());
    }
    
    public void testSendV2CTrapNoVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "v2c Trap",
                getCommunity(),
                "");
    }

    public void testSendV2CTrapNoVarbindsInvalidCommunity() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "v2c Trap",
                INVALID_COMMUNITY,
                "");
    }
    
    public void testSendV2CTrapInvalidVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "v2c Trap",
                getCommunity(),
                INVALID_VARIABLE_BINDINGS);
    }

    public void testSendV1TrapVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "v1 Trap",
                getCommunity(),
                getVariableBindings());
    }

    public void testSendV1TrapNoVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "v1 Trap",
                getCommunity(),
                "");
    }

    public void testSendV1TrapInvalidVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "v1 Trap",
                getCommunity(),
                INVALID_VARIABLE_BINDINGS);
    }
    
    /**
     *  Validate alert action logs
     */
    public void validateAlertActionLogs(String user, 
                                        List<AlertActionLog> actionLogs) {
        
        assertTrue("At least one alert escalation log is needed",
                   !actionLogs.isEmpty());
        
        String logDetail = actionLogs.get(0).getDetail();
        System.out.println(logDetail);
        
        assertTrue("TRAP notification was not sent",
                    logDetail.indexOf("TRAP sent successfully") > -1);
    }
}
