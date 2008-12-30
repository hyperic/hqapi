package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.GroupsResponse;

import java.util.List;

public class GroupGet_test extends GroupTestBase {

    public GroupGet_test(String name) {
        super(name);
    }

    public void testGet() throws Exception {
        GroupApi api = getApi().getGroupApi();

        GroupsResponse resp = api.getGroups();
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

    public void testGetGroupById() throws Exception {
        GroupApi api = getApi().getGroupApi();

        GroupsResponse resp = api.getGroups();
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

        GroupsResponse resp = api.getGroups();
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

        GroupsResponse response = api.getCompatibleGroups();
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

        GroupsResponse response = api.getMixedGroups();
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
