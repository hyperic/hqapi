package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;

import java.util.HashMap;

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

        Resource parent = new Resource();
        parent.setName("My Server");
        ResourcePrototype pt = new ResourcePrototype();
        pt.setName("HTTP");

        ResourceApi api = getApi().getResourceApi();
        api.createService(pt, parent, "My Service",
                          "A test service created by the test suite",
                          new HashMap<String,String>());
    }
}
