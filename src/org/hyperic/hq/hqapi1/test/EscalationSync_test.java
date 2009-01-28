package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.EscalationActionBuilder;
import org.hyperic.hq.hqapi1.EscalationActionBuilder.EscalationActionType;
import org.hyperic.hq.hqapi1.EscalationActionBuilder.EscalationNotifyType;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.EscalationAction;
import org.hyperic.hq.hqapi1.types.EscalationResponse;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RolesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UsersResponse;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class EscalationSync_test extends EscalationTestBase {

    final String[] EMAILS = { "test@hyperic.com", "api@hyperic.com" };

    public EscalationSync_test(String name) {
        super(name);
    }

    public void testSyncCreateNoActions() throws Exception {

        EscalationApi api = getEscalationApi();

        List<Escalation> escalations = new ArrayList<Escalation>();
        Escalation e = generateEscalation();
        escalations.add(e);
        StatusResponse syncResponse = api.syncEscalations(escalations);
        hqAssertSuccess(syncResponse);

        for (Escalation esc : escalations) {
            EscalationResponse escResponse = api.getEscalation(esc.getName());
            hqAssertSuccess(escResponse);
            cleanup(escResponse.getEscalation());
        }
    }

    public void testSyncCreateManyActions() throws Exception {

        EscalationApi api = getEscalationApi();

        List<Escalation> escalations = new ArrayList<Escalation>();
        Escalation e = generateEscalation();
        int NUM_ACTIONS = 10;
        for (int i = 0; i < NUM_ACTIONS; i++) {
            EscalationAction ea = EscalationActionBuilder.
                    createEmailAction(60000, false, Arrays.asList(EMAILS));
            e.getAction().add(ea);
        }
        escalations.add(e);
        StatusResponse syncResponse = api.syncEscalations(escalations);
        hqAssertSuccess(syncResponse);

        for (Escalation esc : escalations) {
            EscalationResponse escResponse = api.getEscalation(esc.getName());
            hqAssertSuccess(escResponse);
            assertEquals("Invalid number of escalation actions.  Expected " + NUM_ACTIONS,
                         esc.getAction().size(), NUM_ACTIONS);
            for (EscalationAction ea : esc.getAction()) {
                assertEquals(ea.getActionType(),
                             EscalationActionType.EMAIL.getType());
            }
            cleanup(escResponse.getEscalation());
        }
    }

    public void testSyncCreateMultiNoActions() throws Exception {

        EscalationApi api = getEscalationApi();

        final int NUM_ESCALATIONS = 10;
        List<Escalation> escalations = new ArrayList<Escalation>();
        for (int i = 0; i < NUM_ESCALATIONS; i++) {
            Escalation e = generateEscalation();
            escalations.add(e);
        }

        StatusResponse syncResponse = api.syncEscalations(escalations);
        hqAssertSuccess(syncResponse);

        for (Escalation esc : escalations) {
            EscalationResponse escResponse = api.getEscalation(esc.getName());
            hqAssertSuccess(escResponse);
            cleanup(escResponse.getEscalation());
        }
    }

    public void testSyncCreateEmailActionValidEmails() throws Exception {

        EscalationApi api = getEscalationApi();

        List<Escalation> escalations = new ArrayList<Escalation>();
        Escalation e = generateEscalation();
        EscalationAction ea = EscalationActionBuilder.
                createEmailAction(60000, false, Arrays.asList(EMAILS));
        e.getAction().add(ea);
        escalations.add(e);
        StatusResponse syncResponse = api.syncEscalations(escalations);
        hqAssertSuccess(syncResponse);

        for (Escalation esc : escalations) {
            EscalationResponse escResponse = api.getEscalation(esc.getName());
            hqAssertSuccess(escResponse);
            assertEquals("Didn't find 1 action for escalation",
                         esc.getAction().size(), 1);
            EscalationAction action = esc.getAction().get(0);
            assertEquals("Wrong number of emails for action",
                         action.getNotify().size(), EMAILS.length);
            assertEquals(action.getActionType(),
                         EscalationActionType.EMAIL.getType());
            cleanup(escResponse.getEscalation());
        }
    }

    public void testSyncCreateEmailActionUsers() throws Exception {

        HQApi api = getApi();
        EscalationApi escApi = api.getEscalationApi();
        UserApi userApi = api.getUserApi();

        UsersResponse users = userApi.getUsers();
        hqAssertSuccess(users);

        List<Escalation> escalations = new ArrayList<Escalation>();
        Escalation e = generateEscalation();
        EscalationAction ea = EscalationActionBuilder.
                createEmailUsersAction(60000, false, users.getUser());
        e.getAction().add(ea);
        escalations.add(e);
        StatusResponse syncResponse = escApi.syncEscalations(escalations);
        hqAssertSuccess(syncResponse);

        for (Escalation esc : escalations) {
            EscalationResponse escResponse = escApi.getEscalation(esc.getName());
            hqAssertSuccess(escResponse);
            assertEquals("Wrong number of actions", esc.getAction().size(), 1);
            EscalationAction action = esc.getAction().get(0);
            assertEquals("Wrong number of users for action",
                         action.getNotify().size(), users.getUser().size());
            assertEquals(action.getActionType(),
                         EscalationActionType.EMAIL.getType());
            cleanup(escResponse.getEscalation());
        }
    }

    public void testSyncCreateEmailActionInvalidUsers() throws Exception {

        HQApi api = getApi();
        EscalationApi escApi = api.getEscalationApi();

        List<User> users = new ArrayList<User>();
        User u = new User();
        u.setName("Non-existant user");
        users.add(u);

        List<Escalation> escalations = new ArrayList<Escalation>();
        Escalation e = generateEscalation();
        EscalationAction ea = EscalationActionBuilder.
                createEmailUsersAction(60000, false, users);
        e.getAction().add(ea);
        escalations.add(e);
        StatusResponse syncResponse = escApi.syncEscalations(escalations);
        hqAssertFailureObjectNotFound(syncResponse);

        for (Escalation esc : escalations) {
            EscalationResponse escResponse = escApi.getEscalation(esc.getName());
            cleanup(escResponse.getEscalation());
        }        
    }

    public void testSyncCreateEmailActionRoles() throws Exception {

        HQApi api = getApi();
        EscalationApi escApi = api.getEscalationApi();
        RoleApi roleApi = api.getRoleApi();

        RolesResponse roles = roleApi.getRoles();
        hqAssertSuccess(roles);

        List<Escalation> escalations = new ArrayList<Escalation>();
        Escalation e = generateEscalation();
        EscalationAction ea = EscalationActionBuilder.
                createEmailRolesAction(60000, false, roles.getRole());
        e.getAction().add(ea);
        escalations.add(e);
        StatusResponse syncResponse = escApi.syncEscalations(escalations);
        hqAssertSuccess(syncResponse);

        for (Escalation esc : escalations) {
            EscalationResponse escResponse = escApi.getEscalation(esc.getName());
            hqAssertSuccess(escResponse);
            assertEquals("Wrong number of actions", esc.getAction().size(), 1);
            EscalationAction action = esc.getAction().get(0);
            assertEquals("Wrong number of roles for action",
                         action.getNotify().size(), roles.getRole().size());
            assertEquals(action.getActionType(),
                         EscalationActionType.EMAIL.getType());
            cleanup(escResponse.getEscalation());
        }
    }

    public void testSyncCreateEmailActionInvalidRoles() throws Exception {

        HQApi api = getApi();
        EscalationApi escApi = api.getEscalationApi();

        List<Role> roles = new ArrayList<Role>();
        Role r = new Role();
        r.setName("Non-existant Role");
        roles.add(r);

        List<Escalation> escalations = new ArrayList<Escalation>();
        Escalation e = generateEscalation();
        EscalationAction ea = EscalationActionBuilder.
                createEmailRolesAction(60000, false, roles);
        e.getAction().add(ea);
        escalations.add(e);
        StatusResponse syncResponse = escApi.syncEscalations(escalations);
        hqAssertFailureObjectNotFound(syncResponse);

        for (Escalation esc : escalations) {
            EscalationResponse escResponse = escApi.getEscalation(esc.getName());
            cleanup(escResponse.getEscalation());
        }
    }

    public void testSyncNoOpAction() throws Exception {

        EscalationApi api = getEscalationApi();

        List<Escalation> escalations = new ArrayList<Escalation>();
        Escalation e = generateEscalation();
        EscalationAction ea = EscalationActionBuilder.createNoOpAction(60000);
        e.getAction().add(ea);
        escalations.add(e);
        StatusResponse syncResponse = api.syncEscalations(escalations);
        hqAssertSuccess(syncResponse);

        for (Escalation esc : escalations) {
            EscalationResponse escResponse = api.getEscalation(esc.getName());
            hqAssertSuccess(escResponse);
            assertEquals("Wrong number of actions", esc.getAction().size(), 1);
            EscalationAction action = esc.getAction().get(0);
            assertEquals("Wrong number of notifications for action",
                         action.getNotify().size(), 0);
            assertEquals(action.getActionType(), EscalationActionType.NOOP.getType());
            cleanup(escResponse.getEscalation());
        }
    }

    public void testSyncSyslogAction() throws Exception {

        EscalationApi api = getEscalationApi();

        List<Escalation> escalations = new ArrayList<Escalation>();
        Escalation e = generateEscalation();
        final String META = "Meta";
        final String PRODUCT = "Product";
        final String VERSION = "Version";

        EscalationAction ea = EscalationActionBuilder.createSyslogAction(60000,
                                                                         META,
                                                                         PRODUCT,
                                                                         VERSION);
        e.getAction().add(ea);
        escalations.add(e);
        StatusResponse syncResponse = api.syncEscalations(escalations);
        hqAssertSuccess(syncResponse);

        for (Escalation esc : escalations) {
            EscalationResponse escResponse = api.getEscalation(esc.getName());
            hqAssertSuccess(escResponse);
            assertEquals("Wrong number of actions", esc.getAction().size(), 1);
            EscalationAction action = esc.getAction().get(0);
            assertEquals("Wrong number of notifications for action",
                         action.getNotify().size(), 0);
            assertEquals(action.getActionType(), EscalationActionType.SYSLOG.getType());
            assertEquals(action.getSyslogMeta(), META);
            assertEquals(action.getSyslogProduct(), PRODUCT);
            assertEquals(action.getSyslogVersion(), VERSION);
            cleanup(escResponse.getEscalation());
        }
    }
}
