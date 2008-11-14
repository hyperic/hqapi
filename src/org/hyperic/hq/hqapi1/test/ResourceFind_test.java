package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.FindResourcesResponse;
import org.hyperic.hq.hqapi1.types.GetResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;

public class ResourceFind_test extends ResourceTestBase {

    public ResourceFind_test(String name) {
        super(name);
    }

    public void testFindByAgent() throws Exception {

        Agent a = getLocalAgent();
        if (a == null) {
            getLog().warn("No local agent found, skipping test.");
            return;
        }
        
        ResourceApi api = getApi().getResourceApi();
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

    public void testFindByInvalidAgent() throws Exception {

        Agent a = new Agent();
        a.setId(Integer.MAX_VALUE);
        a.setAddress("1.2.3.4");
        a.setPort(80);
        a.setVersion("4.0");

        ResourceApi api = getApi().getResourceApi();
        FindResourcesResponse resp = api.findResources(a);
        hqAssertFailureObjectNotFound(resp);
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

    public void testFindByInvalidPrototype() throws Exception {
        final String INVALID_TYPE = "Non-existant type";

        ResourceApi api = getApi().getResourceApi();

        GetResourcePrototypeResponse protoResponse =
            api.getResourcePrototype(INVALID_TYPE);
        hqAssertFailureObjectNotFound(protoResponse);
    }

    public void testFindChildren() throws Exception {

        Agent a = getLocalAgent();
        if (a == null) {
            getLog().warn("No local agent found, skipping test.");
            return;
        }

        ResourceApi api = getApi().getResourceApi();
        FindResourcesResponse resp = api.findResources(a);
        hqAssertSuccess(resp);

        // This test assumes if you have a local agent that is pingable that
        // there will be at least one platform servicing it.
        assertTrue("Found 0 platform resources for agent " + a.getId(),
                   resp.getResource().size() > 0);
        for (Resource r : resp.getResource()) {
            // For each platform resource, loop through it's viewable children.
            validateResource(r);

            FindResourcesResponse childrenResponse = api.findResourceChildren(r);
            hqAssertSuccess(childrenResponse);

            for (Resource child : childrenResponse.getResource()) {
                validateResource(child);
            }
        }
    }

    public void testFindChildrenInvalidResource() throws Exception {

        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);
        r.setName("Invalid resource");

        ResourceApi api = getApi().getResourceApi();
        FindResourcesResponse resp = api.findResourceChildren(r);
        hqAssertFailureObjectNotFound(resp);
    }
}
