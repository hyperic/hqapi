package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.AlertAction;
import org.hyperic.hq.hqapi1.types.AlertActionConfig;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlertDefinitionSyncEmailAction_test extends AlertDefinitionTestBase {

    public AlertDefinitionSyncEmailAction_test(String name) {
        super(name);
    }

    private AlertDefinition generateDefinition(HQApi api) throws Exception {

        Resource platform = getLocalPlatformResource(false, false);

        MetricsResponse metricsResponse = api.getMetricApi().getMetrics(platform);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + platform.getName(),
                metricsResponse.getMetric().size() > 0);
        Metric m = metricsResponse.getMetric().get(0);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createChangeCondition(true, m.getName()));

        return d;
    }

    public void testAddUserNotification() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        AlertDefinition d = generateDefinition(api);

        List<User> users = createTestUsers(1);
        User u = users.get(0);

        AlertDefinitionBuilder.addEmailAction(d, users.toArray(new User[users.size()]));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Wrong number of actions", 1, syncedDef.getAlertAction().size());
        assertTrue("Wrong action class",
                   "com.hyperic.hq.bizapp.server.action.email.EmailAction".
                           equals(syncedDef.getAlertAction().get(0).getClassName()));

        for (AlertActionConfig c : syncedDef.getAlertAction().get(0).getAlertActionConfig()) {
            if (c.getKey().equals("names")) {
                assertEquals("Wrong user name", u.getName(), c.getValue());
            }
        }

        // Clear the action's and resync
        syncedDef.getAlertAction().clear();

        definitions.clear();
        definitions.add(syncedDef);
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Alert actions not cleared!", 0, syncedDef.getAlertAction().size());

        cleanup(response.getAlertDefinition());
        deleteTestUsers(users);
    }

    public void testAddMultiUserNotification() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        AlertDefinition d = generateDefinition(api);

        List<User> users = createTestUsers(10);

        AlertDefinitionBuilder.addEmailAction(d, users.toArray(new User[users.size()]));

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Wrong number of actions", 1, syncedDef.getAlertAction().size());
        assertTrue("Wrong action class",
                   "com.hyperic.hq.bizapp.server.action.email.EmailAction".
                           equals(syncedDef.getAlertAction().get(0).getClassName()));

        for (AlertActionConfig c : syncedDef.getAlertAction().get(0).getAlertActionConfig()) {
            if (c.getKey().equals("names")) {
                for (User u : users) {
                    assertTrue("User " + u.getName() + " not found in notify list!",
                               c.getValue().indexOf(u.getName()) != -1);
                }
            }
        }

        // Clear the action's and resync
        syncedDef.getAlertAction().clear();

        definitions.clear();
        definitions.add(syncedDef);
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Alert actions not cleared!", 0, syncedDef.getAlertAction().size());

        cleanup(response.getAlertDefinition());
        deleteTestUsers(users);
    }

    public void testAddNonExistantUserNotification() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        AlertDefinition d = generateDefinition(api);

        User u = new User();
        u.setName("Non-existant user!");

        AlertDefinitionBuilder.addEmailAction(d, new User[] { u });

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        cleanup(response.getAlertDefinition());
    }

    public void testAddNonExistantUsersNotification() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        AlertDefinition d = generateDefinition(api);

        User u = new User();
        u.setName("Non-existant user!");
        User u2 = new User();
        u2.setName("Non-existant user!");

        AlertDefinitionBuilder.addEmailAction(d, new User[] { u, u2 });

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        cleanup(response.getAlertDefinition());
    }

    public void testAddMultiUserSomeInvalidNotification() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        AlertDefinition d = generateDefinition(api);

        List<User> users = createTestUsers(10);

        User invalidUser = new User();
        invalidUser.setName("Invalid User!");

        User[] userList = users.toArray(new User[users.size()+1]);
        userList[users.size()] = invalidUser;

        AlertDefinitionBuilder.addEmailAction(d, userList);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Wrong number of actions", 1, syncedDef.getAlertAction().size());
        assertTrue("Wrong action class",
                   "com.hyperic.hq.bizapp.server.action.email.EmailAction".
                           equals(syncedDef.getAlertAction().get(0).getClassName()));

        for (AlertActionConfig c : syncedDef.getAlertAction().get(0).getAlertActionConfig()) {
            if (c.getKey().equals("names")) {
                for (User u : users) {
                    assertTrue("User " + u.getName() + " not found in notify list!",
                               c.getValue().indexOf(u.getName()) != -1);
                }
            }
        }

        // Clear the action's and resync
        syncedDef.getAlertAction().clear();

        definitions.clear();
        definitions.add(syncedDef);
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Alert actions not cleared!", 0, syncedDef.getAlertAction().size());

        cleanup(response.getAlertDefinition());
        deleteTestUsers(users);
    }

    public void testAddRoleNotification() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        AlertDefinition d = generateDefinition(api);

        Role r = createRole(Collections.EMPTY_LIST, Collections.EMPTY_LIST);

        AlertDefinitionBuilder.addEmailAction(d, new Role[] {r});

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Wrong number of actions", 1, syncedDef.getAlertAction().size());
        assertTrue("Wrong action class",
                   "com.hyperic.hq.bizapp.server.action.email.EmailAction".
                           equals(syncedDef.getAlertAction().get(0).getClassName()));

        for (AlertActionConfig c : syncedDef.getAlertAction().get(0).getAlertActionConfig()) {
            if (c.getKey().equals("names")) {
                assertEquals("Wrong role name", r.getName(), c.getValue());
            }
        }

        // Clear the action's and resync
        syncedDef.getAlertAction().clear();

        definitions.clear();
        definitions.add(syncedDef);
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Alert actions not cleared!", 0, syncedDef.getAlertAction().size());

        cleanup(response.getAlertDefinition());
        cleanupRole(r);
    }

    public void testAddMultiRoleNotification() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        AlertDefinition d = generateDefinition(api);

        Role r = createRole(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        Role r2 = createRole(Collections.EMPTY_LIST, Collections.EMPTY_LIST);

        AlertDefinitionBuilder.addEmailAction(d, new Role[] {r, r2});

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Wrong number of actions", 1, syncedDef.getAlertAction().size());
        assertTrue("Wrong action class",
                   "com.hyperic.hq.bizapp.server.action.email.EmailAction".
                           equals(syncedDef.getAlertAction().get(0).getClassName()));

        for (AlertActionConfig c : syncedDef.getAlertAction().get(0).getAlertActionConfig()) {
            if (c.getKey().equals("names")) {
                assertTrue("Role name not found in list", c.getValue().indexOf(r.getName()) != -1);
                assertTrue("Role name not found in list", c.getValue().indexOf(r2.getName()) != -1);
            }
        }

        // Clear the action's and resync
        syncedDef.getAlertAction().clear();

        definitions.clear();
        definitions.add(syncedDef);
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Alert actions not cleared!", 0, syncedDef.getAlertAction().size());

        cleanup(response.getAlertDefinition());
        cleanupRole(r);
        cleanupRole(r2);
    }

    public void testAddNonExistantRoleNotification() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        AlertDefinition d = generateDefinition(api);

        Role r = new Role();
        r.setName("Non-existant role!");

        AlertDefinitionBuilder.addEmailAction(d, new Role[] { r });

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        cleanup(response.getAlertDefinition());
    }

    public void testAddNonExistantRolesNotification() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        AlertDefinition d = generateDefinition(api);

        Role r = new Role();
        r.setName("Non-existant role!");
        Role r2 = new Role();
        r2.setName("Non-existant role!");

        AlertDefinitionBuilder.addEmailAction(d, new Role[] { r, r2 });

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        cleanup(response.getAlertDefinition());
    }

    public void testAddMultiRoleSomeInvalidNotification() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        AlertDefinition d = generateDefinition(api);

        Role r = createRole(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        Role r2 = createRole(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        Role r3 = new Role();
        r3.setName("Non-existant role!");

        AlertDefinitionBuilder.addEmailAction(d, new Role[] {r, r2, r3});

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Wrong number of actions", 1, syncedDef.getAlertAction().size());
        assertTrue("Wrong action class",
                   "com.hyperic.hq.bizapp.server.action.email.EmailAction".
                           equals(syncedDef.getAlertAction().get(0).getClassName()));

        for (AlertActionConfig c : syncedDef.getAlertAction().get(0).getAlertActionConfig()) {
            if (c.getKey().equals("names")) {
                assertTrue("Role name not found in list", c.getValue().indexOf(r.getName()) != -1);
                assertTrue("Role name not found in list", c.getValue().indexOf(r2.getName()) != -1);
            }
        }

        // Clear the action's and resync
        syncedDef.getAlertAction().clear();

        definitions.clear();
        definitions.add(syncedDef);
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Alert actions not cleared!", 0, syncedDef.getAlertAction().size());

        cleanup(response.getAlertDefinition());
        cleanupRole(r);
        cleanupRole(r2);
    }

    public void testAddOtherRecipientNotification() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        AlertDefinition d = generateDefinition(api);

        final String EMAIL = "hqapi@vmware.com";
        AlertDefinitionBuilder.addEmailAction(d, new String[] {EMAIL});

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Wrong number of actions", 1, syncedDef.getAlertAction().size());
        assertTrue("Wrong action class",
                   "com.hyperic.hq.bizapp.server.action.email.EmailAction".
                           equals(syncedDef.getAlertAction().get(0).getClassName()));

        for (AlertActionConfig c : syncedDef.getAlertAction().get(0).getAlertActionConfig()) {
            if (c.getKey().equals("names")) {
                assertEquals("Wrong email", EMAIL, c.getValue());
            }
        }

        // Clear the action's and resync
        syncedDef.getAlertAction().clear();

        definitions.clear();
        definitions.add(syncedDef);
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Alert actions not cleared!", 0, syncedDef.getAlertAction().size());

        cleanup(response.getAlertDefinition());
    }

    public void testAddOtherRecipientsNotification() throws Exception {
        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        AlertDefinition d = generateDefinition(api);

        final String EMAIL = "hqapi@vmware.com";
        final String EMAIL2 = "test@vmare.com";

        AlertDefinitionBuilder.addEmailAction(d, new String[] {EMAIL,EMAIL2});

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        AlertDefinition syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Wrong number of actions", 1, syncedDef.getAlertAction().size());
        assertTrue("Wrong action class",
                   "com.hyperic.hq.bizapp.server.action.email.EmailAction".
                           equals(syncedDef.getAlertAction().get(0).getClassName()));

        for (AlertActionConfig c : syncedDef.getAlertAction().get(0).getAlertActionConfig()) {
            if (c.getKey().equals("names")) {
                assertTrue("Email not found in list", c.getValue().indexOf(EMAIL) != -1);
                assertTrue("Email not found in list", c.getValue().indexOf(EMAIL2) != -1);
            }
        }

        // Clear the action's and resync
        syncedDef.getAlertAction().clear();

        definitions.clear();
        definitions.add(syncedDef);
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        syncedDef = response.getAlertDefinition().get(0);
        assertEquals("Alert actions not cleared!", 0, syncedDef.getAlertAction().size());

        cleanup(response.getAlertDefinition());
    }
}
