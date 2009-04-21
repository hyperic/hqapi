package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Resource;

import java.util.List;

public class ResourceMove_test extends ResourceTestBase {

    public ResourceMove_test(String name) {
        super(name);
    }

    public void testResourceMoveInvalidResources() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        Resource target = new Resource();
        target.setId(Integer.MAX_VALUE);

        Resource destination = new Resource();
        destination.setId(Integer.MAX_VALUE);                

        StatusResponse response = api.moveResource(target, destination);
        hqAssertFailureObjectNotFound(response);

    }

    public void testResourceMoveIncompatibleResources() throws Exception {

        ResourceApi api = getApi().getResourceApi();
        Resource platform = getLocalPlatformResource(false, true);

        List<Resource> servers = platform.getResource();

        assertTrue("More than 2 servers not found for platform " + platform.getName(),
                   servers.size() >= 2);

        Resource r1 = servers.get(0);
        Resource r2 = servers.get(1);

        // Attempt move of one server into another

        StatusResponse response = api.moveResource(r1, r2);
        hqAssertFailureInvalidParameters(response);  
    }
}
