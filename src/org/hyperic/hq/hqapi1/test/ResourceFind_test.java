package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;

public class ResourceFind_test extends ResourceTestBase {

    public ResourceFind_test(String name) {
        super(name);
    }

    public void testFindByAgent() throws Exception {

        Agent a = getRunningAgent();
        
        ResourceApi api = getApi().getResourceApi();
        ResourcesResponse resp = api.getResources(a, false, false);
        hqAssertSuccess(resp);

        // This test assumes if you have a local agent that is pingable that
        // there will be at least one platform servicing it.
        assertTrue("Found 0 platform resources for agent " + a.getId(),
                   resp.getResource().size() > 0);
        for (Resource r : resp.getResource()) {
            validateResource(r);
        }
    }

    public void testFindByInvalidAgent() throws Exception {

        Agent a = new Agent();
        a.setId(Integer.MAX_VALUE);
        a.setAddress("1.2.3.4");
        a.setPort(80);
        a.setVersion("4.0");

        ResourceApi api = getApi().getResourceApi();
        ResourcesResponse resp = api.getResources(a, false, false);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testFindByPrototype() throws Exception {
        final String TYPE = "CPU";
        ResourceApi api = getApi().getResourceApi();

        ResourcePrototypeResponse protoResponse =
                api.getResourcePrototype(TYPE);
        hqAssertSuccess(protoResponse);

        ResourcePrototype pt = protoResponse.getResourcePrototype();

        ResourcesResponse resp = api.getResources(pt, false, false);
        hqAssertSuccess(resp);

        // We assume we're running against a server with valid resources
        if (resp.getResource().size() == 0) {
            getLog().warn("Warning, no resources of type " + TYPE + " found!");
        }

        for (Resource r : resp.getResource()) {
            validateResource(r);
        }
    }

    public void testFindByInvalidPrototype() throws Exception {
        final String INVALID_TYPE = "Non-existant type";

        ResourceApi api = getApi().getResourceApi();

        ResourcePrototypeResponse protoResponse =
            api.getResourcePrototype(INVALID_TYPE);
        hqAssertFailureObjectNotFound(protoResponse);
    }
}
