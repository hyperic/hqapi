package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.*;

import java.util.List;
import java.util.ArrayList;

public class ApplicationCreate_test extends ApplicationTestBase {

    public ApplicationCreate_test(String name) {
        super(name);
    }

    public void testApplicationCreateNoServices() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();
        Application a = createTestApplication(null, null);

        StatusResponse response = api.deleteApplication(a.getId());
        hqAssertSuccess(response);
    }

    public void testApplicationCreateWithServices() throws Exception {
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

        StatusResponse deleteResponse = appApi.deleteApplication(a.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testApplicationCreateWithServiceGroups() throws Exception {
        HQApi api = getApi();
        ApplicationApi appApi = api.getApplicationApi();
        GroupApi groupApi = api.getGroupApi();

        Group compatibleServiceGroup = createTestCompatibleGroup("CPU");
        Group compatibleFileMountGroup = createTestCompatibleGroup("FileServer Mount");

        List<Group> serviceGroups = new ArrayList<Group>();
        serviceGroups.add(compatibleServiceGroup);
        serviceGroups.add(compatibleFileMountGroup);

        Application a = createTestApplication(null, serviceGroups);

        for (Group g : serviceGroups) {
            StatusResponse deleteResponse = groupApi.deleteGroup(g.getId());
            hqAssertSuccess(deleteResponse);
        }

        StatusResponse deleteResponse = appApi.deleteApplication(a.getId());
        hqAssertSuccess(deleteResponse);
    }
}
