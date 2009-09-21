package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class ControlTestBase extends HQApiTestBase {

    public ControlTestBase(String name) {
        super(name);
    }

    public Resource createControllableResource(HQApi api)
        throws Exception
    {
        ResourceApi rApi = api.getResourceApi();

        ResourcePrototypeResponse protoResponse =
                rApi.getResourcePrototype("FileServer File");
        hqAssertSuccess(protoResponse);

        Resource localPlatform = getLocalPlatformResource(false, false);

        Map<String,String> config = new HashMap<String,String>();
        // TODO: Fix for windows
        config.put("path", "/usr/bin/true");

        Random r = new Random();
        String name = "Controllable-Resource-" + r.nextInt();

        ResourceResponse resourceCreateResponse =
                rApi.createService(protoResponse.getResourcePrototype(),
                                   localPlatform, name, config);

        hqAssertSuccess(resourceCreateResponse);

        return resourceCreateResponse.getResource();
    }

    public void cleanupControllableResource(HQApi api, Resource r)
        throws Exception
    {
        pauseTest();
        
        ResourceApi rApi = api.getResourceApi();

        StatusResponse response = rApi.deleteResource(r.getId());
        hqAssertSuccess(response);
    }
}
