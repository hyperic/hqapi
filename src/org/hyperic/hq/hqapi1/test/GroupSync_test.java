package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.RolesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

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
        GroupApi groupApi = api.getGroupApi();

        Resource platform = getLocalPlatformResource(false, true);

        List<Resource> children = platform.getResource();
        assertTrue("No child resources for platform " + platform.getName(),
                   children.size() > 0);

        Group g = generateTestGroup();
        g.getResource().addAll(children);

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
                resourceApi.getResources(prototypeResponse.getResourcePrototype(),
                                         false, false);
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
        assertEquals(g.getResourcePrototype().getName(),
                     prototypeResponse.getResourcePrototype().getName());
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

        HQApi api = getApi();
        RoleApi roleApi = api.getRoleApi();
        GroupApi groupApi = api.getGroupApi();

        // Create
        Group g = generateTestGroup();
        GroupResponse createResponse = groupApi.createGroup(g);
        hqAssertSuccess(createResponse);

        Group createdGroup = createResponse.getGroup();
        assertTrue(createdGroup.getRole().size() == 0);

        // Add all Roles
        RolesResponse roleResponse = roleApi.getRoles();
        hqAssertSuccess(roleResponse);
        createdGroup.getRole().addAll(roleResponse.getRole());
        GroupResponse updateResponse = groupApi.updateGroup(createdGroup);
        hqAssertSuccess(updateResponse);
        Group updatedGroup = updateResponse.getGroup();
        assertTrue(updatedGroup.getRole().size() == roleResponse.getRole().size());

        // Clear all roles
        updatedGroup.getRole().clear();
        updateResponse = groupApi.updateGroup(updatedGroup);
        hqAssertSuccess(updateResponse);
        updatedGroup = updateResponse.getGroup();
        assertTrue(updatedGroup.getRole().size() == 0);

        // Cleanup
        StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testUpdateResources() throws Exception {

        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();
        GroupApi groupApi = api.getGroupApi();

        // Find CPU resources
        ResourcePrototypeResponse prototypeResponse =
                resourceApi.getResourcePrototype("CPU");
        hqAssertSuccess(prototypeResponse);

        ResourcesResponse resourceResponse =
                resourceApi.getResources(prototypeResponse.getResourcePrototype(),
                                         false, false);
        hqAssertSuccess(resourceResponse);

        // Create
        Group g = generateTestGroup();
        g.setResourcePrototype(prototypeResponse.getResourcePrototype());
        GroupResponse createResponse = groupApi.createGroup(g);
        hqAssertSuccess(createResponse);
        Group createdGroup = createResponse.getGroup();
        assertTrue(createdGroup.getResource().size() == 0);
        assertEquals(createdGroup.getResourcePrototype().getName(),
                     prototypeResponse.getResourcePrototype().getName());

        // Update with resources
        createdGroup.getResource().addAll(resourceResponse.getResource());
        GroupResponse updateResponse = groupApi.updateGroup(createdGroup);
        hqAssertSuccess(updateResponse);
        Group updatedGroup = updateResponse.getGroup();
        assertTrue(updatedGroup.getResource().size() ==
                   resourceResponse.getResource().size());

        // Clear all resources
        updatedGroup.getResource().clear();
        updateResponse = groupApi.updateGroup(updatedGroup);
        hqAssertSuccess(updateResponse);
        updatedGroup = updateResponse.getGroup();
        assertTrue(updatedGroup.getResource().size() == 0);

        // Cleanup
        StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testUpdateResourcesWrongPrototype() throws Exception {
        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();
        GroupApi groupApi = api.getGroupApi();

        // Find CPU resources
        ResourcePrototypeResponse cpuPrototypeResponse =
                resourceApi.getResourcePrototype("CPU");
        hqAssertSuccess(cpuPrototypeResponse);
        ResourcePrototypeResponse fileServerFileResponse =
                resourceApi.getResourcePrototype("FileServer File");
        hqAssertSuccess(fileServerFileResponse);

        ResourcesResponse resourceResponse =
                resourceApi.getResources(cpuPrototypeResponse.getResourcePrototype(),
                                         false, false);
        hqAssertSuccess(resourceResponse);

        // Create
        Group g = generateTestGroup();
        g.setResourcePrototype(fileServerFileResponse.getResourcePrototype());
        GroupResponse createResponse = groupApi.createGroup(g);
        hqAssertSuccess(createResponse);
        Group createdGroup = createResponse.getGroup();

        // Add CPU resources to FileServer File compat group
        createdGroup.getResource().addAll(resourceResponse.getResource());
        GroupResponse updateResponse = groupApi.updateGroup(createdGroup);
        hqAssertFailureInvalidParameters(updateResponse);

        // Cleanup
        StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testUpdatePrototype() throws Exception {
        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();
        GroupApi groupApi = api.getGroupApi();

        // Find prototypes
        ResourcePrototypeResponse cpuProtoResponse =
                resourceApi.getResourcePrototype("CPU");
        hqAssertSuccess(cpuProtoResponse);
        ResourcePrototypeResponse fileServerFileResponse =
                resourceApi.getResourcePrototype("FileServer File");
        hqAssertSuccess(fileServerFileResponse);

        // Create
        Group g = generateTestGroup();
        g.setResourcePrototype(cpuProtoResponse.getResourcePrototype());
        GroupResponse createResponse = groupApi.createGroup(g);
        hqAssertSuccess(createResponse);
        Group createdGroup = createResponse.getGroup();

        // Update prototype
        createdGroup.setResourcePrototype(fileServerFileResponse.getResourcePrototype());
        GroupResponse updateResponse = groupApi.updateGroup(createdGroup);
        hqAssertFailureNotSupported(updateResponse);

        // Cleanup
        StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup.getId());
        hqAssertSuccess(deleteResponse);
    }    
}
