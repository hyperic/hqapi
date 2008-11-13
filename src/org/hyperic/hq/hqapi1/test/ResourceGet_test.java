package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.GetResourceResponse;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.FindResourcesResponse;
import org.hyperic.hq.hqapi1.types.Resource;

public class ResourceGet_test extends HQApiTestBase {

    public ResourceGet_test(String name) {
        super(name);
    }

    public void testGetInvalidResource() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        GetResourceResponse resp = api.getResource(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testGetResource() throws Exception {

        Agent a = getLocalAgent();

        if (a == null) {
            getLog().warn("No local agent found, skipping test.");
            return;
        }

        ResourceApi api = getApi().getResourceApi();

        FindResourcesResponse findResponse = api.findResources(a);
        hqAssertSuccess(findResponse);

        // This test assumes if you have a local agent that is pingable that
        // there will be at least one platform servicing it.
        assertTrue("Found 0 platform resources for agent " + a.getId(),
                   findResponse.getResource().size() > 0);
        for (Resource r : findResponse.getResource()) {
            // Now that we have valid resource ids, query each

            Integer rid = r.getId();

            GetResourceResponse getResponse = api.getResource(rid);
            hqAssertSuccess(getResponse);
            Resource resource = getResponse.getResource();
            assertNotNull(resource);
            assertTrue("Invalid resource id " + r.getId(),
                       r.getId() > 0);
            assertNotNull(resource.getName());
        }
    }
}