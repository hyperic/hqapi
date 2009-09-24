package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.*;

public class ApplicationCreate_test extends ApplicationTestBase {

    public ApplicationCreate_test(String name) {
        super(name);
    }

    public void testApplicationCreateNoServices() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();
        Application a = createTestApplication(null);

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

        Application a = createTestApplication(cpusResponse.getResource());

        StatusResponse deleteResponse = appApi.deleteApplication(a.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testApplicationCreateWithServers() throws Exception {
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

        Application a = generateTestApplication();
        a.getResource().addAll(agentsResponse.getResource());

        ApplicationResponse response = appApi.createApplication(a);
        hqAssertFailureInvalidParameters(response); // Invalid - cannot have servers
    }

    public void testApplicationCreateWithPlatforms() throws Exception {
        HQApi api = getApi();
        ApplicationApi appApi = api.getApplicationApi();

        Resource platform = getLocalPlatformResource(false, false);

        Application a = generateTestApplication();
        a.getResource().add(platform);

        ApplicationResponse response = appApi.createApplication(a);
        hqAssertFailureInvalidParameters(response); // Invalid - cannot have servers
    }
}
