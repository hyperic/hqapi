package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.GroupsResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;

import java.util.List;

public class Group_test extends HQApiTestBase {

    public Group_test(String name) {
        super(name);
    }

    private void validateGroup(Group g) {
        assertTrue("Invalid id for Group.", g.getId() > 0);
        assertTrue("Found invalid name for Group with id=" + g.getId(),
                   g.getName().length() > 0);

        if (g.getResourcePrototype() != null) {
            ResourcePrototype pt = g.getResourcePrototype();
            assertTrue("Invalid prototype id", pt.getId() > 0);
            assertTrue("Invalid prototype name for group " + g.getName(),
                       pt.getName().length() > 0);
        }
    }

    public void testCreate() throws Exception {
        GroupApi api = getApi().getGroupApi();

        Group g = new Group();
        GroupResponse resp = api.createGroup(g);
        hqAssertFailureNotImplemented(resp);
    }

    public void testDelete() throws Exception {
        GroupApi api = getApi().getGroupApi();

        StatusResponse resp = api.deleteGroup(1);
        hqAssertFailureNotImplemented(resp);
    }

    public void testRemoveResource() throws Exception {
        GroupApi api = getApi().getGroupApi();

        StatusResponse resp = api.removeResource(1,2);
        hqAssertFailureNotImplemented(resp);
    }

    public void testAddResource() throws Exception {

        GroupApi api = getApi().getGroupApi();

        StatusResponse resp = api.addResource(1,2);
        hqAssertFailureNotImplemented(resp);
    }
    
    public void testList() throws Exception {
        GroupApi api = getApi().getGroupApi();

        GroupsResponse resp = api.listGroups();
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

        GroupsResponse resp = api.listGroups();
        hqAssertSuccess(resp);

        List<Group> groups = resp.getGroup();
        if (groups.size() == 0) {
            throw new Exception("No groups found.");
        }

        for (Group g : resp.getGroup()) {
            validateGroup(g);
            ResourcesResponse resourceResponse = api.listResources(g.getId());
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

        ResourcesResponse resp = api.listResources(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testGetGroupById() throws Exception {
        GroupApi api = getApi().getGroupApi();

        GroupsResponse resp = api.listGroups();
        hqAssertSuccess(resp);

        List<Group> groups = resp.getGroup();
        if (groups.size() == 0) {
            throw new Exception("No groups found.");
        }

        Group g = resp.getGroup().get(0);

        GroupResponse groupResponse = api.getGroup(g.getId());
        hqAssertSuccess(groupResponse);
        validateGroup(groupResponse.getGroup());
    }

    public void testGetGroupInvalidId() throws Exception {

        GroupApi api = getApi().getGroupApi();

        GroupResponse groupResponse = api.getGroup(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(groupResponse);
    }

    public void testGetGroupByName() throws Exception {

        GroupApi api = getApi().getGroupApi();

        GroupsResponse resp = api.listGroups();
        hqAssertSuccess(resp);

        List<Group> groups = resp.getGroup();
        if (groups.size() == 0) {
            throw new Exception("No groups found.");
        }

        Group g = resp.getGroup().get(0);

        GroupResponse groupResponse = api.getGroup(g.getName());
        hqAssertSuccess(groupResponse);
        validateGroup(groupResponse.getGroup());
    }

    public void getGetGroupByInvalidName() throws Exception {

        GroupApi api = getApi().getGroupApi();

        GroupResponse groupResponse = api.getGroup("Non-existant group");
        hqAssertFailureObjectNotFound(groupResponse);
    }

    public void testGetCompatibleGroups() throws Exception {

        GroupApi api = getApi().getGroupApi();

        GroupsResponse response = api.listCompatibleGroups();
        hqAssertSuccess(response);

        List<Group> groups = response.getGroup();
        if (groups.size() == 0) {
            throw new Exception("No compatible groups found in inventory.");
        }

        for (Group g : groups) {
            // All compatible groups will have a prototype.
            assertNotNull(g.getResourcePrototype());
            validateGroup(g);
        }
    }

    public void testGetMixedGroups() throws Exception {

        GroupApi api = getApi().getGroupApi();

        GroupsResponse response = api.listMixedGroups();
        hqAssertSuccess(response);

        List<Group> groups = response.getGroup();
        if (groups.size() == 0) {
            throw new Exception("No mixed groups found in inventory.");
        }

        for (Group g : groups) {
            // All mixed groups will not have a prototype.
            assertNull(g.getResourcePrototype());
            validateGroup(g);
        }
    }
}
