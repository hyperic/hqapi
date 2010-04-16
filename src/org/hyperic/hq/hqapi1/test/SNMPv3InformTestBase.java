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

public class SNMPv3InformTestBase extends SNMPTestBase {
        
    public SNMPv3InformTestBase(String name) {
        super(name);
    }

    public String getProtocolVersion() {
        return "3";
    }

    /**
     *  Validate alert action logs
     */
    public void validateAlertActionLogs(String user, 
                                        List<AlertActionLog> actionLogs) {
        
        assertTrue("At least one alert action log is needed",
                    !actionLogs.isEmpty());

        String logDetail = actionLogs.get(0).getDetail();
        System.out.println(logDetail);
            
        if (user.length() == 0) {
            // empty string user means a "no security name" test case
            assertTrue("The HQ server settings should have no SNMP Security Name",
                       logDetail.indexOf("Security Name required") > -1);
        } else if (user.equals(INVALID_SECURITY_NAME)) {
            assertTrue("The HQ server settings should have an invalid SNMP Security Name",
                       logDetail.indexOf("Unknown user name") > -1);
        } else {
            // non-empty string user means a "valid security name" test case
            // and the value should be a valid user who can send INFORM notifications
            assertTrue("INFORM notification was not sent",
                       logDetail.indexOf("INFORM sent") > -1);
            
            // validate variable bindings
            assertTrue("sysUpTime is missing",
                       logDetail.indexOf("1.3.6.1.2.1.1.3") > 0);
            
            assertTrue("snmpTrapOID is missing",
                       logDetail.indexOf("1.3.6.1.6.3.1.1.4.1") > 0);
            
            // TODO: Need to validate user defined variable bindings
            
        }
    }
}
