package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.CreateGroupResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.DeleteGroupResponse;
import org.hyperic.hq.hqapi1.types.RemoveResourceFromGroupResponse;
import org.hyperic.hq.hqapi1.types.GetGroupsResponse;
import org.hyperic.hq.hqapi1.types.FindResourcesResponse;
import org.hyperic.hq.hqapi1.types.AddResourceToGroupResponse;
import org.hyperic.hq.hqapi1.types.Resource;

import java.util.List;

public class Group_test extends HQApiTestBase {

    public Group_test(String name) {
        super(name);
    }

    private void validateGroup(Group g) {
        assertTrue("Invalid id for Group.", g.getId() > 0);
        assertTrue("Found invalid name for Group with id=" + g.getId(),
                   g.getName().length() > 0);
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
        hqAssertSuccess(resp);

        List<Group> groups = resp.getGroup();
        if (groups.size() == 0) {
            getLog().warn("No groups found, skipping test");
            return;
        }

        for (Group g : groups) {
            validateGroup(g);
        }
    }

    public void testGetResourcesInGroup() throws Exception {
        GroupApi api = getApi().getGroupApi();

        GetGroupsResponse resp = api.listGroups();
        hqAssertSuccess(resp);

        List<Group> groups = resp.getGroup();
        if (groups.size() == 0) {
            getLog().warn("No groups found, skipping test");
            return;
        }

        for (Group g : resp.getGroup()) {
            validateGroup(g);
            FindResourcesResponse resourceResponse = api.listResources(g.getId());
            hqAssertSuccess(resourceResponse);
            if (resourceResponse.getResource().size() == 0) {
                getLog().warn("Zero group members found for " + g.getId());
            }

            for (Resource r : resourceResponse.getResource()) {
                assertNotNull(r);
                assertTrue(r.getId() > 0);
                assertTrue(r.getName().length() > 0);
            }
        }
    }

    public void testGetResourcesInInvalidGroup() throws Exception {

        GroupApi api = getApi().getGroupApi();

        FindResourcesResponse resp = api.listResources(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testAddResource() throws Exception {

        GroupApi api = getApi().getGroupApi();

        AddResourceToGroupResponse resp = api.addResource(1,2);
        hqAssertFailureNotImplemented(resp);
    }
}
