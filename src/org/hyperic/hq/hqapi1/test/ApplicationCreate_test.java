package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.types.*;

import java.util.List;

public class ApplicationCreate_test extends HQApiTestBase {

    public ApplicationCreate_test(String name) {
        super(name);
    }

    public void testApplicationCreate() throws Exception {

        ApplicationApi api = getApi().getApplicationApi();

        Application a = new Application();
        a.setName("A new app");
        a.setLocation("Tahiti");
        a.setDescription("A test app created using the API");
        a.setEngContact("the Engineer");
        a.setBizContact("the Businessman");
        a.setOpsContact("the Ops Man");

        Agent agent = getRunningAgent();

        ResourceApi resourceApi = getApi().getResourceApi();
        GroupApi groupApi = getApi().getGroupApi();

        // TODO: This actually creates a mixed group for "Platforms, Servers & Service resource types."
        // It still works for adding "Application resource types." using the API but not the GUI
        Group group = new Group();
        group.setName("An Application Group");
        group.setDescription("Application Test Group");
        group.setLocation("Anywhere");
        GroupResponse newGroup = groupApi.createGroup(group);

        GroupsResponse grpResponse = groupApi.getMixedGroups();
        List<Group> groups = a.getGroup();
        for (Group g : grpResponse.getGroup()) {
            System.out.println("GROUP: " + g.getDescription() + " :: " + g.getResourcePrototype());
            if (g.getDescription().contains("Application")) {
                System.out.println("+> " + g.getId() + " " + g.getName());
                groups.add(g);
            }
        }

        ResourcesResponse findResponse = resourceApi.getResources(agent, true, true);
        hqAssertSuccess(findResponse);

        assertTrue("Found 0 platform resources for agent " + agent.getId(),
                   findResponse.getResource().size() > 0);

        Resource toAdd1 = null;
        Resource toAdd2 = null;
        for (Resource r : findResponse.getResource().get(0).getResource()) {
            System.out.println("RESOURCE: " + r.getDescription() + " :: " + r.getResourcePrototype().getName());
            for (Resource r2 : r.getResource()) {
                System.out.println("+> " + r2.getId() + " " + r2.getName());
                if (toAdd1 == null) {
                    toAdd1 = r2;
                }
                else {
                    toAdd2 = r2;
                    break;
                }
            }
            if (toAdd2 != null) {
                break;
            }
        }
        assertNotNull("Found 0 services to add", toAdd1);

        List<Resource> resources = a.getResource();
        resources.add(toAdd1);
        if (toAdd2 != null) {
            resources.add(toAdd2);
        }

        ApplicationResponse response = api.createApplication(a);

        hqAssertSuccess(response);

        api.deleteApplication(response.getApplication().getId());
        groupApi.deleteGroup(newGroup.getGroup().getId().intValue());
    }
}
