package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.*;

public class ResourceFind_test extends HQApiTestBase {

    public ResourceFind_test(String name) {
        super(name);
    }

    private void validateResource(Resource r) {
        assertNotNull(r);
        assertNotNull(r.getId());
        assertNotNull(r.getName());
    }

    public void testFindByAgent() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        Agent a = getLocalAgent();
        if (a == null) {
            getLog().warn("No local agent found, skipping test.");
            return;
        }

        FindResourcesResponse resp = api.findResources(a);
        hqAssertSuccess(resp);

        // This test assumes if you have a local agent that is pingable that
        // there will be at least one platform servicing it.
        assertTrue("Found 0 platform resources for agent " + a.getId(),
                   resp.getResource().size() > 0);
        for (Resource r : resp.getResource()) {
            validateResource(r);
        }
    }

    public void testFindByPrototype() throws Exception {
        final String TYPE = "CPU";
        ResourceApi api = getApi().getResourceApi();

        GetResourcePrototypeResponse protoResponse =
                api.getResourcePrototype(TYPE);
        hqAssertSuccess(protoResponse);

        ResourcePrototype pt = protoResponse.getResourcePrototype();

        FindResourcesResponse resp = api.findResources(pt);
        hqAssertSuccess(resp);

        // We assume we're running against a server with valid resources
        if (resp.getResource().size() == 0) {
            getLog().warn("Warning, no resources of type " + TYPE + " found!");
        }

        for (Resource r : resp.getResource()) {
            validateResource(r);
        }
    }
}
