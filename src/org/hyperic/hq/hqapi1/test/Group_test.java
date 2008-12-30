package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.GroupsResponse;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.RolesResponse;

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

        if (g.getResource().size() > 0) {
            for (Resource r : g.getResource()) {
                assertTrue("Invalid resource id for group member", r.getId() > 0);
                assertTrue("Invalid resource name for group member",
                           r.getName().length() > 0);
            }
        }

        if (g.getRole().size() > 0) {
            for (Role r : g.getRole()) {
                assertTrue("Invalid role id", r.getId() >= 0);
                assertTrue("Invalid role name", r.getName().length() > 0);
            }
        }
    }

    public void testDeleteInvalidId() throws Exception {
        GroupApi api = getApi().getGroupApi();

        StatusResponse resp = api.deleteGroup(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testList() throws Exception {
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

    public void testCreateMixed() throws Exception {

    }

    public void testCreateCompatible() throws Exception {

        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();
        RoleApi roleApi = api.getRoleApi();
        GroupApi groupApi = api.getGroupApi();

        // Find CPU resources
        ResourcePrototypeResponse prototypeResponse =
                resourceApi.getResourcePrototype("CPU");
        hqAssertSuccess(prototypeResponse);

        ResourcesResponse resourceResponse =
                resourceApi.getResources(prototypeResponse.getResourcePrototype());
        hqAssertSuccess(resourceResponse);

        // Find all Roles
        RolesResponse roleResponse = roleApi.getRoles();
        hqAssertSuccess(roleResponse);

        final String NAME = "API Test Group";
        final String DESCRIPTION = "Api Test Group Description";
        final String LOCATION = "Api Test Group Location";

        // Create
        Group g = new Group();
        g.setName(NAME);
        g.setDescription(DESCRIPTION);
        g.setLocation(LOCATION);
        g.setResourcePrototype(prototypeResponse.getResourcePrototype());
        g.getResource().addAll(resourceResponse.getResource());
        g.getRole().addAll(roleResponse.getRole());

        StatusResponse response = groupApi.createGroup(g);
        hqAssertSuccess(response);

        GroupResponse createGroupResponse = groupApi.getGroup(NAME);
        hqAssertSuccess(createGroupResponse);

        Group createdGroup = createGroupResponse.getGroup();
        assertEquals(createdGroup.getName(), g.getName());
        assertEquals(createdGroup.getDescription(), g.getDescription());
        assertEquals(createdGroup.getLocation(), g.getLocation());
        assertEquals(createdGroup.getResource().size(),
                     g.getResource().size());
        assertEquals(createdGroup.getRole().size(),
                     g.getRole().size());

        // Cleanup
        StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testCreateCompatibleWrongPrototype() throws Exception {

    }

    public void testUpdateFields() throws Exception {

        HQApi api = getApi();
        GroupApi groupApi = api.getGroupApi();

        final String NAME = "API Test Group";
        final String DESCRIPTION = "Api Test Group Description";
        final String LOCATION = "Api Test Group Location";

        // Create
        Group g = new Group();
        g.setName(NAME);
        g.setDescription(DESCRIPTION);
        g.setLocation(LOCATION);

        StatusResponse response = groupApi.createGroup(g);
        hqAssertSuccess(response);

        GroupResponse createGroupResponse = groupApi.getGroup(NAME);
        hqAssertSuccess(createGroupResponse);

        // Update
        Group createdGroup = createGroupResponse.getGroup();

        final String UPDATED_NAME = "API Test Group Updated";
        final String UPDATED_DESC = "Api Test Description Updated";
        final String UPDATED_LOCATION = "Api Test Location Updated";

        createdGroup.setName(UPDATED_NAME);
        createdGroup.setDescription(UPDATED_DESC);
        createdGroup.setLocation(UPDATED_LOCATION);

        StatusResponse updateResponse = groupApi.updateGroup(createdGroup);
        hqAssertSuccess(updateResponse);

        // Validate

        GroupResponse getResponse = groupApi.getGroup(createdGroup.getId());
        hqAssertSuccess(getResponse);

        Group updatedGroup = getResponse.getGroup();
        assertEquals(updatedGroup.getName(), UPDATED_NAME);
        assertEquals(updatedGroup.getDescription(), UPDATED_DESC);
        assertEquals(updatedGroup.getLocation(), UPDATED_LOCATION);

        // Cleanup
        StatusResponse deleteResponse = groupApi.deleteGroup(updatedGroup.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testUpdateRoles() throws Exception {

    }

    public void testUpdateResources() throws Exception {

    }

    public void testUpdateResourcesWrongPrototype() throws Exception {

    }

    public void testUpdatePrototype() throws Exception {
        
    }
}
