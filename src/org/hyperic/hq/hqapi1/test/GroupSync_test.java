package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.RolesResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.GroupResponse;

/**
 * This class tests all aspects of the group sync. (which includes update/create)
 */
public class GroupSync_test extends GroupTestBase {

    public GroupSync_test(String name) {
        super(name);
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

        // Create
        Group g = generateTestGroup();
        g.setResourcePrototype(prototypeResponse.getResourcePrototype());
        g.getResource().addAll(resourceResponse.getResource());
        g.getRole().addAll(roleResponse.getRole());

        StatusResponse response = groupApi.createGroup(g);
        hqAssertSuccess(response);

        GroupResponse createGroupResponse = groupApi.getGroup(g.getName());
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

        // Create
        Group g = generateTestGroup();

        StatusResponse response = groupApi.createGroup(g);
        hqAssertSuccess(response);

        GroupResponse createGroupResponse = groupApi.getGroup(g.getName());
        hqAssertSuccess(createGroupResponse);

        // Update
        Group createdGroup = createGroupResponse.getGroup();

        final String UPDATED = "Updated";

        createdGroup.setName(g.getName() + UPDATED);
        createdGroup.setDescription(g.getDescription() + UPDATED);
        createdGroup.setLocation(g.getLocation() + UPDATED);

        StatusResponse updateResponse = groupApi.updateGroup(createdGroup);
        hqAssertSuccess(updateResponse);

        // Validate
        GroupResponse getResponse = groupApi.getGroup(createdGroup.getId());
        hqAssertSuccess(getResponse);

        Group updatedGroup = getResponse.getGroup();
        assertTrue(updatedGroup.getName().endsWith(UPDATED));
        assertTrue(updatedGroup.getDescription().endsWith(UPDATED));
        assertTrue(updatedGroup.getLocation().endsWith(UPDATED));

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
