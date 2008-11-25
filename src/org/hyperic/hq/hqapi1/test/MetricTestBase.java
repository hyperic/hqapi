package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.FindResourcesResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.ResourceApi;

import java.util.List;

public class MetricTestBase extends HQApiTestBase {

    public MetricTestBase(String name) {
        super(name);
    }

    protected Resource getResource() throws Exception {

        Agent a = getRunningAgent();

        if (a == null) {
            getLog().warn("No local agent found.");
            return null;
        }

        ResourceApi api = getApi().getResourceApi();
        FindResourcesResponse resourceResponse =
            api.findResources(a);
        hqAssertSuccess(resourceResponse);

        List<Resource> localPlatforms = resourceResponse.getResource();
        if (localPlatforms.size() == 0) {
            getLog().warn("Unable to find the local platform for agent " +
                          a.getAddress() + ":" + a.getPort());
            return null;
        }
        return localPlatforms.get(0);
    }
}
