package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Application;
import org.hyperic.hq.hqapi1.types.ApplicationResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.ApplicationApi;

import java.util.Random;
import java.util.List;

public abstract class ApplicationTestBase extends HQApiTestBase {

    static final String UPDATE_PREFIX = "UPDATED-";

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

    protected Application generateTestApplication()
        throws Exception {

        Random r = new Random();
        Application a = new Application();

        String name = APP_NAME + r.nextInt();
        a.setName(name);
        a.setLocation(APP_LOCATION);
        a.setDescription(APP_DESC);
        a.setEngContact(APP_ENG_CONTACT);
        a.setBizContact(APP_BIZ_CONTACT);
        a.setOpsContact(APP_OPS_CONTACT);

        return a;
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
        Application a = generateTestApplication();

        if (services != null) {
            a.getResource().addAll(services);
        }

        ApplicationResponse response = api.createApplication(a);
        hqAssertSuccess(response);

        Application createdApp = response.getApplication();

        assertNotNull("Application id was null!", createdApp.getId());
        assertEquals(createdApp.getName(), a.getName());
        assertEquals(createdApp.getLocation(), APP_LOCATION);
        assertEquals(createdApp.getDescription(), APP_DESC);
        assertEquals(createdApp.getEngContact(), APP_ENG_CONTACT);
        assertEquals(createdApp.getBizContact(), APP_BIZ_CONTACT);
        assertEquals(createdApp.getOpsContact(), APP_OPS_CONTACT);

        return response.getApplication();
    }
}
