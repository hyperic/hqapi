package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;

public class ControlTestBase extends GroupTestBase {

    public ControlTestBase(String name) {
        super(name);
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
