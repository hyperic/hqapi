package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.ResourceApi;

import java.util.HashMap;

public class ResourceCreateServer_test extends ResourceTestBase {

    public ResourceCreateServer_test(String name) {
        super(name);
    }

    public void testCreateServer() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        ResourcePrototype pt = new ResourcePrototype();
        pt.setName("JBoss 4.x");
        Resource parent = new Resource();
        parent.setName("My Platform");

        ResourceResponse resp =
                api.createServer(pt, parent, "My JBoss Server",
                                 "/usr/local/jboss",
                                 new HashMap<String,String>());
        hqAssertFailureNotImplemented(resp);
    }
}
