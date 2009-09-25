package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.ApplicationResponse;
import org.hyperic.hq.hqapi1.types.Application;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.Resource;

import java.util.ArrayList;
import java.util.List;

public class ApplicationUpdate_test extends ApplicationTestBase {

    private static final String UPDATE_PREFIX = "UPDATED-";

    public ApplicationUpdate_test(String name) {
        super(name);
    }

    public void testUpdateNoServices() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        Application a = createTestApplication(null);

        a.setName(UPDATE_PREFIX + a.getName());
        a.setDescription(UPDATE_PREFIX + a.getDescription());
        a.setLocation(UPDATE_PREFIX + a.getLocation());
        a.setOpsContact(UPDATE_PREFIX + a.getOpsContact());
        a.setBizContact(UPDATE_PREFIX + a.getBizContact());
        a.setEngContact(UPDATE_PREFIX + a.getEngContact());

        ApplicationResponse updateResponse = api.updateApplication(a);
        hqAssertSuccess(updateResponse);

        Application updatedApp = updateResponse.getApplication();

        assertEquals(a.getName(), updatedApp.getName());
        assertEquals(a.getDescription(), updatedApp.getDescription());
        assertEquals(a.getLocation(), updatedApp.getLocation());
        assertEquals(a.getOpsContact(), updatedApp.getOpsContact());
        assertEquals(a.getBizContact(), updatedApp.getBizContact());
        assertEquals(a.getEngContact(), updatedApp.getEngContact());

        StatusResponse deleteResponse = api.deleteApplication(updatedApp.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testUpdateAddServices() throws Exception {
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

        Application a = createTestApplication(null);

        a.getResource().addAll(cpusResponse.getResource());

        ApplicationResponse updateResponse = appApi.updateApplication(a);
        hqAssertSuccess(updateResponse);

        Application updatedApplication = updateResponse.getApplication();

        assertEquals(cpusResponse.getResource().size(),
                     updatedApplication.getResource().size());

        StatusResponse deleteResponse =
                appApi.deleteApplication(updatedApplication.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testUpdateRemoveServices() throws Exception {
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

        Application a = createTestApplication(cpusResponse.getResource());

        a.getResource().clear();

        ApplicationResponse updateResponse = appApi.updateApplication(a);
        hqAssertSuccess(updateResponse);

        Application updatedApplication = updateResponse.getApplication();

        assertEquals(0, updatedApplication.getResource().size());

        StatusResponse deleteResponse =
                appApi.deleteApplication(updatedApplication.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testUpdateAddServers() throws Exception {
        HQApi api = getApi();
        ResourceApi rApi = api.getResourceApi();
        ApplicationApi appApi = api.getApplicationApi();

        ResourcePrototypeResponse protoResponse =
                rApi.getResourcePrototype("HQ Agent");
        hqAssertSuccess(protoResponse);

        ResourcesResponse agentsResponse =
                rApi.getResources(protoResponse.getResourcePrototype(),
                                  false, false);
        hqAssertSuccess(agentsResponse);

        assertTrue("No HQ Agent resources found in the inventory!",
                   agentsResponse.getResource().size() > 0);

        Application a = createTestApplication(null);

        a.getResource().addAll(agentsResponse.getResource());

        ApplicationResponse updateResponse = appApi.updateApplication(a);
        hqAssertFailureInvalidParameters(updateResponse);

        StatusResponse deleteResponse = appApi.deleteApplication(a.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testUpdateAddPlatform() throws Exception {
        HQApi api = getApi();
        ApplicationApi appApi = api.getApplicationApi();

        Resource platform = getLocalPlatformResource(false, false);

        Application a = createTestApplication(null);

        a.getResource().add(platform);

        ApplicationResponse updateResponse = appApi.updateApplication(a);
        hqAssertFailureInvalidParameters(updateResponse);

        StatusResponse deleteResponse = appApi.deleteApplication(a.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testUpdateNonPersistedApplication() throws Exception {
        ApplicationApi appApi = getApi().getApplicationApi();

        Application a = generateTestApplication();

        ApplicationResponse response = appApi.updateApplication(a);
        hqAssertFailureInvalidParameters(response);
    }

    public void testUpdateExistingApplicationName() throws Exception {
        ApplicationApi appApi = getApi().getApplicationApi();

        Application app1 = createTestApplication(null);
        Application app2 = createTestApplication(null);

        // Try to rename app2 to app1
        app2.setName(app1.getName());

        ApplicationResponse response = appApi.updateApplication(app2);
        hqAssertFailureInvalidParameters(response);

        StatusResponse deleteResponse = appApi.deleteApplication(app1.getId());
        hqAssertSuccess(deleteResponse);

        deleteResponse = appApi.deleteApplication(app2.getId());
        hqAssertSuccess(deleteResponse);
    }
}
