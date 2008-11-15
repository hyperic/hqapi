package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.GetResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.FindResourcesResponse;
import org.hyperic.hq.hqapi1.types.CreateResourceResponse;

import java.util.HashMap;
import java.util.Map;

public class ResourceCreateService_test extends ResourceTestBase {

    public ResourceCreateService_test(String name) {
        super(name);
    }

    public void testServiceCreate() throws Exception {

        Agent a = getLocalAgent();
        if (a == null) {
            getLog().warn("No local agent found, skipping test.");
            return;
        }

        ResourceApi api = getApi().getResourceApi();

        GetResourcePrototypeResponse protoResponse = api.getResourcePrototype("HTTP");
        hqAssertSuccess(protoResponse);
        ResourcePrototype pt = protoResponse.getResourcePrototype();

        FindResourcesResponse resourcesResponse = api.findResources(a);
        hqAssertSuccess(resourcesResponse);
        String PARENT_TYPE = "Net Services";
        Resource parent = null;
        for (Resource r : resourcesResponse.getResource()) {
            if (r.getResourcePrototype().getName().equals("Net Services")) {
                parent = r;
                break;
            }
        }

        if (parent == null) {
            getLog().error("Unable to find server of type " + PARENT_TYPE +
                           " skipping test.");
            return;
        }

        Map<String,String> params = new HashMap<String,String>();
        params.put("hostname", "www.hyperic.com");
        params.put("port", "80");
        params.put("sotimeout", "10");
        params.put("path", "/");
        params.put("method", "GET");

        CreateResourceResponse resp =
                api.createService(pt, parent, "My HTTP Check",
                                  "A test service created by the test suite",
                                  params);
        hqAssertFailureNotImplemented(resp);
    }
}
