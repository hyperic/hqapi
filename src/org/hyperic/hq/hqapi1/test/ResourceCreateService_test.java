package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.GetResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.FindResourcesResponse;
import org.hyperic.hq.hqapi1.types.CreateResourceResponse;
import org.hyperic.hq.hqapi1.types.DeleteResourceResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ResourceCreateService_test extends ResourceTestBase {

    public ResourceCreateService_test(String name) {
        super(name);
    }

    public void testServiceCreate() throws Exception {

        Agent a = getRunningAgent();

        ResourceApi api = getApi().getResourceApi();

        // Find HTTP resource type
        GetResourcePrototypeResponse protoResponse = api.getResourcePrototype("HTTP");
        hqAssertSuccess(protoResponse);
        ResourcePrototype pt = protoResponse.getResourcePrototype();

        // Find local platform
        FindResourcesResponse resourcesResponse = api.findResources(a);
        hqAssertSuccess(resourcesResponse);
        assertTrue("Did not find a single platform for " + a.getAddress() + ":" +
                   a.getPort(), resourcesResponse.getResource().size() == 1);
        Resource platform = resourcesResponse.getResource().get(0);

        // Configure service
        Map<String,String> params = new HashMap<String,String>();
        params.put("hostname", "www.hyperic.com");
        params.put("port", "80");
        params.put("sotimeout", "10");
        params.put("path", "/");
        params.put("method", "GET");

        Random r = new Random();
        String name = "My HTTP Check " + r.nextInt();

        CreateResourceResponse resp = api.createService(pt, platform, name,
                                                        params);
        hqAssertSuccess(resp);
        Resource createdResource = resp.getResource();
        assertEquals(createdResource.getName(), name);

        // Clean up
        DeleteResourceResponse deleteResponse =
                api.deleteResource(createdResource.getId());
        hqAssertSuccess(deleteResponse);
    }
}
