package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.ControlHistory;
import org.hyperic.hq.hqapi1.types.ControlHistoryResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.ControlApi;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;

public class ControlTestBase extends GroupTestBase {

    protected static final long SECOND = 1000;
    protected static final String STATUS_COMPLETED = "Completed";

    public ControlTestBase(String name) {
        super(name);
    }
    
    public Group createControllableGroup(HQApi api) 
        throws Exception
    {
        return createControllableGroup(api, 1);
    }
    
    public Group createControllableGroup(HQApi api, int numOfResources) 
        throws Exception
    {
        GroupApi groupApi = api.getGroupApi();
        
        Group g = generateTestGroup();
        
        for (int i=0; i< numOfResources; i++) {
            Resource controllableResource = createControllableResource(api);        
            g.setResourcePrototype(controllableResource.getResourcePrototype());
            g.getResource().add(controllableResource);
        }

        GroupResponse createGroupResponse = groupApi.createGroup(g);
        hqAssertSuccess(createGroupResponse);
        
        assertEquals(numOfResources, 
                     createGroupResponse.getGroup().getResource().size());
                
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
    
    protected ControlHistory findControlHistory(Object resource, int timeout)
        throws Exception {

        ControlApi cApi = getApi().getControlApi();
        ControlHistory controlHistory = null;
        long start = System.currentTimeMillis();
        
        while (System.currentTimeMillis() < (start + (timeout*SECOND))) {

            ControlHistoryResponse historyResponse = null;
            if (resource instanceof Resource) {
                historyResponse = cApi.getHistory((Resource) resource);
            } else {
                historyResponse = cApi.getHistory((Group) resource);
            }
            hqAssertSuccess(historyResponse);
            
            if (historyResponse.getControlHistory().size() > 0) {
                assertEquals("Wrong number of items in control history",
                             1, historyResponse.getControlHistory().size());

                ControlHistory log = historyResponse.getControlHistory().get(0);
                
                if (STATUS_COMPLETED.equals(log.getStatus())) {
                    controlHistory = log;
                    break;
                }

            }
            Thread.sleep(SECOND);
        }

        if (controlHistory == null) {
            throw new Exception (
                    "Unable to find a completed control history in timeout of "
                            + timeout + " seconds");
        }
        
        return controlHistory;
    }
}
