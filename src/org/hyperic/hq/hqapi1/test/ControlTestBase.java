package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class ControlTestBase extends GroupTestBase {

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
    
    public Group createControllableGroup(HQApi api) 
        throws Exception
    {
        GroupApi groupApi = api.getGroupApi();
        
        Resource controllableResource = createControllableResource(api);        
        Group g = generateTestGroup();
        g.setResourcePrototype(controllableResource.getResourcePrototype());
        g.getResource().add(controllableResource);

        GroupResponse createGroupResponse = groupApi.createGroup(g);
        hqAssertSuccess(createGroupResponse);
                
        return createGroupResponse.getGroup();
    }

    public void cleanupControllableResource(HQApi api, Resource r)
        throws Exception
    {
        pauseTest();
        
        ResourceApi rApi = api.getResourceApi();

        StatusResponse response = rApi.deleteResource(r.getId());
        hqAssertSuccess(response);
    }
    
    public void cleanupControllableGroup(HQApi api, Group g)
        throws Exception
    {
        pauseTest();
        
        GroupApi groupApi = api.getGroupApi();
        ResourceApi rApi = api.getResourceApi();
        
        for (Resource r : g.getResource()) {
            StatusResponse response = rApi.deleteResource(r.getId());
            hqAssertSuccess(response);    
        }

        StatusResponse response = groupApi.deleteGroup(g.getId());
        hqAssertSuccess(response);
    }
}
