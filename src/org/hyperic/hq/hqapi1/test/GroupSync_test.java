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
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;

import java.util.List;

/**
 * This class tests all aspects of the group sync. (which includes update/create)
 */
public class GroupSync_test extends GroupTestBase {

    public GroupSync_test(String name) {
        super(name);
    }

    public void testCreateMixed() throws Exception {

        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();
        GroupApi groupApi = api.getGroupApi();

        Resource platform = getLocalPlatformResource();

        ResourcesResponse resourceResponse =
                resourceApi.getResourceChildren(platform);
        hqAssertSuccess(resourceResponse);
        List<Resource> resources = resourceResponse.getResource();
        assertTrue("No servers found on platform " + platform.getName(),
                   resources.size() > 0);

        Group g = generateTestGroup();
        g.getResource().addAll(resources);

        GroupResponse createResponse = groupApi.createGroup(g);
        hqAssertSuccess(createResponse);

        Group createdGroup = createResponse.getGroup();
        validateGroup(createdGroup);
        assertEquals(g.getName(), createdGroup.getName());
        assertEquals(g.getDescription(), createdGroup.getDescription());
        assertEquals(g.getLocation(), createdGroup.getLocation());

        // Cleanup
        StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup.getId());
        hqAssertSuccess(deleteResponse);
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

        GroupResponse createResponse = groupApi.createGroup(g);
        hqAssertSuccess(createResponse);

        Group createdGroup = createResponse.getGroup();
        validateGroup(createdGroup);
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

    public void testCreateCompatibleInvalidPrototype() throws Exception {

        GroupApi groupApi = getApi().getGroupApi();

        ResourcePrototype type = new ResourcePrototype();
        type.setName("Invalid Resource Prototype");

        // Create
        Group g = generateTestGroup();
        g.setResourcePrototype(type);

        GroupResponse createResponse = groupApi.createGroup(g);
        hqAssertFailureObjectNotFound(createResponse);    
    }

    public void testUpdateFields() throws Exception {

        HQApi api = getApi();
        GroupApi groupApi = api.getGroupApi();

        // Create
        Group g = generateTestGroup();

        GroupResponse response = groupApi.createGroup(g);
        hqAssertSuccess(response);

        // Update
        Group createdGroup = response.getGroup();

        final String UPDATED = "Updated";

        createdGroup.setName(g.getName() + UPDATED);
        createdGroup.setDescription(g.getDescription() + UPDATED);
        createdGroup.setLocation(g.getLocation() + UPDATED);

        GroupResponse updateResponse = groupApi.updateGroup(createdGroup);
        hqAssertSuccess(updateResponse);

        // Validate
        Group updatedGroup = updateResponse.getGroup();
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
