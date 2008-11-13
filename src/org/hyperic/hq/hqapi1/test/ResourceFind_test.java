package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.FindResourcesResponse;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.Resource;

public class ResourceFind_test extends HQApiTestBase {

    public ResourceFind_test(String name) {
        super(name);
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
            assertNotNull(r);
            assertNotNull(r.getId());
            assertNotNull(r.getName());
        }
    }
}
