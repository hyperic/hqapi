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

import java.util.ArrayList;
import java.util.List;

public class ApplicationUpdate_test extends ApplicationTestBase {

    private static final String UPDATE_PREFIX = "UPDATED-";

    public ApplicationUpdate_test(String name) {
        super(name);
    }

    public void testUpdateNoServices() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        Application a = createTestApplication(null, null);

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

        Application a = createTestApplication(null, null);

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

        Application a = createTestApplication(cpusResponse.getResource(), null);

        a.getResource().clear();

        ApplicationResponse updateResponse = appApi.updateApplication(a);
        hqAssertSuccess(updateResponse);

        Application updatedApplication = updateResponse.getApplication();

        assertEquals(0, updatedApplication.getResource().size());

        StatusResponse deleteResponse =
                appApi.deleteApplication(updatedApplication.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testUpdateAddGroups() throws Exception {
        HQApi api = getApi();
        GroupApi gApi = api.getGroupApi();
        ApplicationApi appApi = api.getApplicationApi();

        Application a = createTestApplication(null, null);

        Group g = createTestCompatibleGroup("CPU");
        List<Group> groups = new ArrayList<Group>();
        groups.add(g);

        a.getGroup().addAll(groups);

        ApplicationResponse updateResponse = appApi.updateApplication(a);
        hqAssertSuccess(updateResponse);

        Application updatedApplication = updateResponse.getApplication();

        assertEquals(groups.size(), updatedApplication.getGroup().size());

        StatusResponse deleteResponse = gApi.deleteGroup(g.getId());
        hqAssertSuccess(deleteResponse);

        deleteResponse = appApi.deleteApplication(updatedApplication.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testUpdateRemoveGroups() throws Exception {
        HQApi api = getApi();
        GroupApi gApi = api.getGroupApi();
        ApplicationApi appApi = api.getApplicationApi();

        Group g = createTestCompatibleGroup("CPU");
        List<Group> groups = new ArrayList<Group>();
        groups.add(g);

        Application a = createTestApplication(null, groups);

        a.getGroup().clear();

        ApplicationResponse updateResponse = appApi.updateApplication(a);
        hqAssertSuccess(updateResponse);

        Application updatedApplication = updateResponse.getApplication();

        assertEquals(0, updatedApplication.getGroup().size());

        StatusResponse deleteResponse = gApi.deleteGroup(g.getId());
        hqAssertSuccess(deleteResponse);

        deleteResponse = appApi.deleteApplication(updatedApplication.getId());
        hqAssertSuccess(deleteResponse);
    }
}
