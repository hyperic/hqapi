package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.CreateGroupResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.DeleteGroupResponse;
import org.hyperic.hq.hqapi1.types.RemoveResourceFromGroupResponse;
import org.hyperic.hq.hqapi1.types.GetGroupsResponse;
import org.hyperic.hq.hqapi1.types.FindResourcesResponse;
import org.hyperic.hq.hqapi1.types.AddResourceToGroupResponse;

public class Group_test extends HQApiTestBase {

    public Group_test(String name) {
        super(name);
    }

    public void testCreate() throws Exception {
        GroupApi api = getApi().getGroupApi();

        Group g = new Group();
        CreateGroupResponse resp = api.createGroup(g);
        hqAssertFailureNotImplemented(resp);
    }

    public void testDelete() throws Exception {
        GroupApi api = getApi().getGroupApi();

        DeleteGroupResponse resp = api.deleteGroup(1);
        hqAssertFailureNotImplemented(resp);
    }

    public void testRemoveResource() throws Exception {
        GroupApi api = getApi().getGroupApi();

        RemoveResourceFromGroupResponse resp = api.removeResource(1,2);
        hqAssertFailureNotImplemented(resp);
    }

    public void testList() throws Exception {
        GroupApi api = getApi().getGroupApi();

        GetGroupsResponse resp = api.listGroups();
        hqAssertFailureNotImplemented(resp);
    }

    public void testGetResourcesInGroup() throws Exception {

        GroupApi api = getApi().getGroupApi();

        FindResourcesResponse resp = api.listResources(1);
        hqAssertFailureNotImplemented(resp);
    }

    public void testAddResource() throws Exception {

        GroupApi api = getApi().getGroupApi();

        AddResourceToGroupResponse resp = api.addResource(1,2);
        hqAssertFailureNotImplemented(resp);
    }
}
