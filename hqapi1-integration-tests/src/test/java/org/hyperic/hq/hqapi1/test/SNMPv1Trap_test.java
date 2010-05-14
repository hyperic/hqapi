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

public class SNMPv1Trap_test extends SNMPTestBase {
    
    public SNMPv1Trap_test(String name) {
        super(name);
    }

    public String getProtocolVersion() {
        return "1";
    }
    
    // TODO: ResourceType Alert Definitions
    
    public void testSendVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "v1 Trap",
                getCommunity(),
                getVariableBindings());
    }

    public void testSendVarBindsInvalidCommunity() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "v1 Trap",
                INVALID_COMMUNITY,
                getVariableBindings());
    }
    
    public void testSendNoVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "v1 Trap",
                getCommunity(),
                "");
    }

    public void testSendNoVarbindsInvalidCommunity() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "v1 Trap", 
                INVALID_COMMUNITY,
                "");
    }

    public void testSendInvalidVarbinds() throws Exception {
        testSendNotification(
                getProtocolVersion(),
                "v1 Trap",
                getCommunity(),
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
        
        assertTrue("TRAP notification was not sent",
                    logDetail.indexOf("V1TRAP sent successfully") > -1);
    
        // TODO: Need to validate variable bindings

    }
}
