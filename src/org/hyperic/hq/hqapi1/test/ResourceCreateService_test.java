package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class ResourceCreateService_test extends ResourceTestBase {

    public ResourceCreateService_test(String name) {
        super(name);
    }

    public void testServiceCreate() throws Exception {

        ResourceApi api = getApi().getResourceApi();
        Resource createdResource = createTestHTTPService();

        // Clean up
        StatusResponse deleteResponse = api.deleteResource(createdResource.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testServiceCreateInvalidDescription() throws Exception {

        Agent a = getRunningAgent();

        ResourceApi api = getApi().getResourceApi();

        // Find HTTP resource type
        ResourcePrototypeResponse protoResponse = api.getResourcePrototype("HTTP");
        hqAssertSuccess(protoResponse);
        ResourcePrototype pt = protoResponse.getResourcePrototype();

        // Find local platform
        ResourcesResponse resourcesResponse = api.getResources(a, false, false);
        hqAssertSuccess(resourcesResponse);
        assertTrue("Did not find a single platform for " + a.getAddress() + ":" +
                   a.getPort(), resourcesResponse.getResource().size() == 1);
        Resource platform = resourcesResponse.getResource().get(0);

        String longDescription = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        // Configure service
        Map<String,String> params = new HashMap<String,String>();
        params.put("hostname", "www.hyperic.com");
        params.put("port", "80");
        params.put("sotimeout", "10");
        params.put("path", "/");
        params.put("method", "GET");
        params.put("description", longDescription);

        Random r = new Random();
        String name = "My HTTP Check " + r.nextInt();

        ResourceResponse resp = api.createService(pt, platform, name, params);
        hqAssertFailureInvalidParameters(resp);
    }
}
