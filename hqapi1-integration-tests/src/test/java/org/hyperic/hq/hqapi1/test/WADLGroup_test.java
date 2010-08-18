package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLGroup_test extends WADLTestBase  {

    public void testGroupList() throws Exception {
        HttpLocalhost8080HquHqapi1.GroupListHqu groupList = new HttpLocalhost8080HquHqapi1.GroupListHqu();

        GroupsResponse response = groupList.getAsGroupsResponse();
        hqAssertSuccess(response);
    }

    public void testGroupGet() throws Exception {
        HttpLocalhost8080HquHqapi1.GroupGetHqu groupGet = new HttpLocalhost8080HquHqapi1.GroupGetHqu();

        GroupResponse response = groupGet.getAsGroupResponse("Some group");
        hqAssertFailure(response);
    }

    public void testGroupDelete() throws Exception {
        HttpLocalhost8080HquHqapi1.GroupDeleteHqu groupDelete = new HttpLocalhost8080HquHqapi1.GroupDeleteHqu();

        StatusResponse response = groupDelete.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(response);
    }

    public void testGroupSync() throws Exception {
        HttpLocalhost8080HquHqapi1.GroupSyncHqu groupSync = new HttpLocalhost8080HquHqapi1.GroupSyncHqu();
        GroupsRequest request = new GroupsRequest();

        GroupsResponse response = groupSync.postApplicationXmlAsGroupsResponse(request);
        hqAssertSuccess(response);
    }
}
