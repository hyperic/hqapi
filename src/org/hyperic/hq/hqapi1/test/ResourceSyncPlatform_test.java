package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.*;

import java.util.HashMap;

public class ResourceSyncPlatform_test extends HQApiTestBase {

    public ResourceSyncPlatform_test(String name) {
        super(name);
    }

    public void testSync() throws Exception {
        Agent a = getLocalAgent();

        if (a == null) {
            getLog().warn("No local agent found, skipping test.");
            return;
        }

        ResourceApi api = getApi().getResourceApi();

        GetResourcePrototypeResponse proto = api.getResourcePrototype("Network Device");
        hqAssertSuccess(proto);
        ResourcePrototype pt = proto.getResourcePrototype();

        SyncPlatformResponse resp = api.createPlatform(a, pt,
                                                       "Test Network Device",
                                                       "device.hyperic.com",
                                                       new HashMap());
        hqAssertFailureNotImplemented(resp);
    }
}
