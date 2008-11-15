package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.CreateResourceResponse;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;

import java.util.HashMap;

public class ResourceCreatePlatform_test extends ResourceTestBase {

    public ResourceCreatePlatform_test(String name) {
        super(name);
    }

    public void testCreatePlatform() throws Exception {

        Agent a = getLocalAgent();
        if (a == null) {
            getLog().warn("No local agent found, skipping test.");
            return;
        }

        ResourcePrototype pt = new ResourcePrototype();
        pt.setName("Network Device");

        ResourceApi api = getApi().getResourceApi();

        CreateResourceResponse resp =
                api.createPlatform(a, pt, "My Platform", "apitest.hyperic.com",
                                   new HashMap<String,String>());
        hqAssertFailureNotImplemented(resp);       
    }
}
