package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLGroup_test extends WADLTestBase  {

    public void testGroupList() throws Exception {
        Endpoint.GroupListHqu groupList = new Endpoint.GroupListHqu();

        GroupsResponse response = groupList.getAsGroupsResponse();
        hqAssertSuccess(response);
    }

    public void testGroupGet() throws Exception {
        Endpoint.GroupGetHqu groupGet = new Endpoint.GroupGetHqu();

        GroupResponse response = groupGet.getAsGroupResponse("Some group");
        hqAssertFailure(response);
    }

    public void testGroupDelete() throws Exception {
        Endpoint.GroupDeleteHqu groupDelete = new Endpoint.GroupDeleteHqu();

        StatusResponse response = groupDelete.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(response);
    }

    public void testGroupSync() throws Exception {
        Endpoint.GroupSyncHqu groupSync = new Endpoint.GroupSyncHqu();
        GroupsRequest request = new GroupsRequest();

        GroupsResponse response = groupSync.postAsGroupsResponse(request);
        hqAssertSuccess(response);
    }
}
