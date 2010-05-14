package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.AlertResponse;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.Operation;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.AlertApi;

import java.util.Collections;
import java.util.List;

public class AlertAck_test extends AlertTestBase {

    public AlertAck_test(String name) {
        super(name);
    }

    public void testAckAlert() throws Exception {
        AlertApi api = getAlertApi();
        Resource platform = getLocalPlatformResource(false, false);
        Escalation e = createEscalation();
        Alert a = generateAlerts(platform, e);

        validateAlert(a);

        // Test ack - alert will be in Escalation
        AlertResponse ackResponse = api.ackAlert(a.getId(), "Test ACK", 60000l);
        hqAssertSuccess(ackResponse);

        assertEquals("Alert not acked by hqadmin",
                     "hqadmin",
                     ackResponse.getAlert().getEscalationState().getAckedBy());

        // Cleanup
        deleteAlertDefinitionByAlert(a);
        deleteEscalation(e);
    }

    public void testAckAlertNoPermission() throws Exception {
        Resource platform = getLocalPlatformResource(false, false);
        Escalation e = createEscalation();
        Alert a = generateAlerts(platform, e);
        validateAlert(a);

        List<User> users = createTestUsers(1);
        User unprivUser = users.get(0);
        AlertApi apiUnpriv = getApi(unprivUser.getName(), TESTUSER_PASSWORD).getAlertApi();

        // Test ack - alert will be in Escalation
        AlertResponse ackResponse = apiUnpriv.ackAlert(a.getId(), "Test ACK", 60000l);
        hqAssertFailurePermissionDenied(ackResponse);

        // Cleanup
        deleteAlertDefinitionByAlert(a);
        deleteEscalation(e);
        deleteTestUsers(users);
    }
    
    public void testAckAlertAlertingPermission() throws Exception {
        Resource platform = getLocalPlatformResource(false, false);
        Escalation e = createEscalation();
        Alert a = generateAlerts(platform, e);
        validateAlert(a);

        // Create user/group/role with insufficient permissions
        List<User> users = createTestUsers(1);
        User unprivUser = users.get(0);
        Role alertRole = createRole(Collections.singletonList(unprivUser),
                                    Collections.singletonList(Operation.MANAGE_PLATFORM_ALERTS));
        Group groupWithRole = createGroup(Collections.singletonList(platform),
                                          Collections.singletonList(alertRole));

        AlertApi apiUnpriv = getApi(unprivUser.getName(), TESTUSER_PASSWORD).getAlertApi();

        // Test ack (alert will be in Escalation) with an unprivileged user.
        // Role needs alerting and at least view resource permissions
        // in order to ack alerts
        AlertResponse ackResponse = apiUnpriv.ackAlert(a.getId(), "Test ACK", 60000l);
        hqAssertFailurePermissionDenied(ackResponse);

        // Cleanup
        deleteAlertDefinitionByAlert(a);
        deleteEscalation(e);
        deleteTestUsers(users);
        cleanupRole(alertRole);
        cleanupGroup(groupWithRole);
    }

    public void testAckUnacknowledableAlert() throws Exception {
        Resource platform = getLocalPlatformResource(false, false);
        AlertApi api = getAlertApi();
        Alert a = generateAlerts(platform);

        validateAlert(a);

        // Test ack - alert is not in escalation
        AlertResponse ackResponse = api.ackAlert(a.getId(), "Test ACK", 60000l);
        hqAssertSuccess(ackResponse);

        assertNull("Alert has escalation state, should not be ackable",
                   ackResponse.getAlert().getEscalationState());

        // Cleanup
        deleteAlertDefinitionByAlert(a);
    }

    public void testAckInvalidAlert() throws Exception {

        AlertApi api = getAlertApi();

        AlertResponse response = api.ackAlert(Integer.MAX_VALUE, "Test ACK", 60000l);
        hqAssertFailureObjectNotFound(response);
    }
}
