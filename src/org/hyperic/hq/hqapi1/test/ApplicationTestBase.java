package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Application;
import org.hyperic.hq.hqapi1.types.ApplicationResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.ResourceApi;

import java.util.Random;
import java.util.List;

public abstract class ApplicationTestBase extends HQApiTestBase {

    protected static final String APP_NAME = "Test Application";
    protected static final String APP_LOCATION = "SFO";
    protected static final String APP_DESC = "Test Application Description";
    protected static final String APP_ENG_CONTACT = "415-555-5555";
    protected static final String APP_BIZ_CONTACT = "212-555-5555";
    protected static final String APP_OPS_CONTACT = "510-555-5555";

    static final String GROUP_NAME        = "API Test Group";
    static final String GROUP_LOCATION    = "API Test Group Location";
    static final String GROUP_DESCRIPTION = "API Test Group Description";

    public ApplicationTestBase(String name) {
        super(name);
    }

    /**
     * Create an Application with no groups or services associated.
     *
     * @param services A list of Resources to add to the Application or null
     * if no Resources should be added.
     * Groups should be added.
     * @return The created Application
     * @throws Exception If an error occurs.
     */
    protected Application createTestApplication(List<Resource> services)
            throws Exception
    {
        ApplicationApi api = getApi().getApplicationApi();

        Random r = new Random();
        Application a = new Application();

        String name = APP_NAME + r.nextInt();
        a.setName(name);
        a.setLocation(APP_LOCATION);
        a.setDescription(APP_DESC);
        a.setEngContact(APP_ENG_CONTACT);
        a.setBizContact(APP_BIZ_CONTACT);
        a.setOpsContact(APP_OPS_CONTACT);

        if (services != null) {
            a.getResource().addAll(services);
        }

        ApplicationResponse response = api.createApplication(a);
        hqAssertSuccess(response);

        Application createdApp = response.getApplication();

        assertNotNull("Application id was null!", createdApp.getId());
        assertEquals(createdApp.getName(), name);
        assertEquals(createdApp.getLocation(), APP_LOCATION);
        assertEquals(createdApp.getDescription(), APP_DESC);
        assertEquals(createdApp.getEngContact(), APP_ENG_CONTACT);
        assertEquals(createdApp.getBizContact(), APP_BIZ_CONTACT);
        assertEquals(createdApp.getOpsContact(), APP_OPS_CONTACT);

        if (services != null) {
            assertEquals(createdApp.getResource().size(), services.size());
        }

        return response.getApplication();
    }

    /**
     * Create a compabile group of the given prototype that includes all
     * resources of that type in the inventory.
     *
     * @param prototype The type of compatible group to create.
     * @return The created Group
     * @throws Exception If an error occurs creating the group
     */
    protected Group createTestCompatibleGroup(String prototype) throws Exception {
        HQApi api = getApi();
        GroupApi groupApi = api.getGroupApi();
        ResourceApi rApi = api.getResourceApi();

        ResourcePrototypeResponse protoResponse =
                rApi.getResourcePrototype(prototype);
        hqAssertSuccess(protoResponse);

        ResourcesResponse resourcesResponse =
                rApi.getResources(protoResponse.getResourcePrototype(),
                                  false, false);
        hqAssertSuccess(resourcesResponse);

        assertTrue("No resources of type " + prototype + " found!",
                   resourcesResponse.getResource().size() > 0);

        Group g = new Group();
        Random r = new Random();

        g.setName(GROUP_NAME + r.nextInt());
        g.setDescription(GROUP_DESCRIPTION);
        g.setLocation(GROUP_LOCATION);
        g.setResourcePrototype(protoResponse.getResourcePrototype());
        g.getResource().addAll(resourcesResponse.getResource());

        GroupResponse groupResponse = groupApi.createGroup(g);
        hqAssertSuccess(groupResponse);

        return groupResponse.getGroup();
    }
}
