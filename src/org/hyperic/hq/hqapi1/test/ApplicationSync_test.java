package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Application;
import org.hyperic.hq.hqapi1.types.ApplicationsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;

import java.util.ArrayList;
import java.util.List;

public class ApplicationSync_test extends ApplicationTestBase {

    public ApplicationSync_test(String name) {
        super(name);
    }

    public void testSyncCreate() throws Exception {
        HQApi api = getApi();
        ApplicationApi appApi = api.getApplicationApi();

        Application a = generateTestApplication();
        List<Application> apps = new ArrayList<Application>();
        apps.add(a);

        ApplicationsResponse response = appApi.syncApplications(apps);
        hqAssertSuccess(response);

        assertEquals("Wrong number of applications returned by sync", 1,
                     response.getApplication().size());

        Application syncedApp = response.getApplication().get(0);

        StatusResponse deleteResponse = appApi.deleteApplication(syncedApp.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testSyncCreateMulti() throws Exception {
        HQApi api = getApi();
        ApplicationApi appApi = api.getApplicationApi();

        final int NUM = 10;
        List<Application> apps = new ArrayList<Application>();
        for (int i = 0; i < NUM; i++) {
            apps.add(generateTestApplication());
        }

        ApplicationsResponse response = appApi.syncApplications(apps);
        hqAssertSuccess(response);

        assertEquals("Wrong number of applications returned by sync", NUM,
                     response.getApplication().size());

        for (Application a : response.getApplication()) {
            StatusResponse deleteResponse = appApi.deleteApplication(a.getId());
            hqAssertSuccess(deleteResponse);
        }
    }

    public void testSyncCreateMultiWithServices() throws Exception {
        HQApi api = getApi();
        ResourceApi rApi = api.getResourceApi();
        ApplicationApi appApi = api.getApplicationApi();

        ResourcePrototypeResponse protoResponse =
                rApi.getResourcePrototype("CPU");
        hqAssertSuccess(protoResponse);

        ResourcesResponse cpusResponse =
                rApi.getResources(protoResponse.getResourcePrototype(),
                                  false, false);
        hqAssertSuccess(cpusResponse);

        final int NUM = 10;
        List<Application> apps = new ArrayList<Application>();
        for (int i = 0; i < NUM; i++) {
            Application a = generateTestApplication();
            a.getResource().addAll(cpusResponse.getResource());
            apps.add(a);
        }

        ApplicationsResponse response = appApi.syncApplications(apps);
        hqAssertSuccess(response);

        assertEquals("Wrong number of applications returned by sync", NUM,
                     response.getApplication().size());

        for (Application a : response.getApplication()) {
            // Validate number of app services
            assertEquals("Incorrect number of app services",
                         cpusResponse.getResource().size(),
                         a.getResource().size());

            StatusResponse deleteResponse = appApi.deleteApplication(a.getId());
            hqAssertSuccess(deleteResponse);
        }
    }

    public void testSyncUpdate() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        Application a = createTestApplication(null);

        a.setName(UPDATE_PREFIX + a.getName());
        a.setDescription(UPDATE_PREFIX + a.getDescription());
        a.setLocation(UPDATE_PREFIX + a.getLocation());
        a.setOpsContact(UPDATE_PREFIX + a.getOpsContact());
        a.setBizContact(UPDATE_PREFIX + a.getBizContact());
        a.setEngContact(UPDATE_PREFIX + a.getEngContact());

        List<Application> apps = new ArrayList<Application>();
        apps.add(a);

        ApplicationsResponse syncResponse = api.syncApplications(apps);
        hqAssertSuccess(syncResponse);

        assertEquals("Wrong number of applications returned by sync", 1,
                     syncResponse.getApplication().size());

        Application updatedApp = syncResponse.getApplication().get(0);

        assertEquals(a.getName(), updatedApp.getName());
        assertEquals(a.getDescription(), updatedApp.getDescription());
        assertEquals(a.getLocation(), updatedApp.getLocation());
        assertEquals(a.getOpsContact(), updatedApp.getOpsContact());
        assertEquals(a.getBizContact(), updatedApp.getBizContact());
        assertEquals(a.getEngContact(), updatedApp.getEngContact());

        StatusResponse deleteResponse = api.deleteApplication(updatedApp.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testSyncUpdateMulti() throws Exception {
        HQApi api = getApi();
        ResourceApi rApi = api.getResourceApi();
        ApplicationApi appApi = api.getApplicationApi();

        ResourcePrototypeResponse protoResponse =
                rApi.getResourcePrototype("CPU");
        hqAssertSuccess(protoResponse);

        ResourcesResponse cpusResponse =
                rApi.getResources(protoResponse.getResourcePrototype(),
                                  false, false);
        hqAssertSuccess(cpusResponse);

        final int NUM = 10;
        List<Application> apps = new ArrayList<Application>();

        for (int i = 0; i < NUM; i++) {
            Application a = createTestApplication(null);
            a.setName(UPDATE_PREFIX + a.getName());
            a.setDescription(UPDATE_PREFIX + a.getDescription());
            a.setLocation(UPDATE_PREFIX + a.getLocation());
            a.setOpsContact(UPDATE_PREFIX + a.getOpsContact());
            a.setBizContact(UPDATE_PREFIX + a.getBizContact());
            a.setEngContact(UPDATE_PREFIX + a.getEngContact());
            a.getResource().addAll(cpusResponse.getResource());
            apps.add(a);
        }

        ApplicationsResponse syncResponse = appApi.syncApplications(apps);
        hqAssertSuccess(syncResponse);

        assertEquals("Wrong number of applications returned by sync", NUM,
                     syncResponse.getApplication().size());

        for (Application a : syncResponse.getApplication()) {
            assertTrue(a.getName().startsWith(UPDATE_PREFIX));
            assertTrue(a.getDescription().startsWith(UPDATE_PREFIX));
            assertTrue(a.getLocation().startsWith(UPDATE_PREFIX));
            assertTrue(a.getOpsContact().startsWith(UPDATE_PREFIX));
            assertTrue(a.getBizContact().startsWith(UPDATE_PREFIX));
            assertTrue(a.getEngContact().startsWith(UPDATE_PREFIX));

            assertEquals("Invalid number of application services!",
                         cpusResponse.getResource().size(),
                         a.getResource().size());

            StatusResponse deleteResponse = appApi.deleteApplication(a.getId());
            hqAssertSuccess(deleteResponse);
        }
    }

    public void testSyncEmpty() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        List<Application> a = new ArrayList<Application>();
        ApplicationsResponse response = api.syncApplications(a);
        hqAssertSuccess(response);

        assertEquals("Invalid number of Applications returned from sync",
                     0, response.getApplication().size());
    }
}
