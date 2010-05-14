/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

package org.hyperic.hq.hqapi1.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.GroupsResponse;
import org.hyperic.hq.hqapi1.types.Operation;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RoleResponse;
import org.hyperic.hq.hqapi1.types.RolesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class GroupGet_test extends GroupTestBase {

    private static final int SYNC_NUM = 3;

    public GroupGet_test(String name) {
        super(name);
    }

    public void testGet() throws Exception {
        GroupApi api = getApi().getGroupApi();

        List<Group> groups = new ArrayList<Group>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Group g = generateTestGroup();
            groups.add(g);
        }

        GroupsResponse response = api.syncGroups(groups);
        hqAssertSuccess(response);

        GroupsResponse getResponse = api.getGroups();
        hqAssertSuccess(response);

        for (Group g : getResponse.getGroup()) {
            validateGroup(g);
        }

        // Remove original synced groups
        cleanup(response.getGroup());
    }

    public void testGetGroupById() throws Exception {

        GroupApi api = getApi().getGroupApi();

        List<Group> groups = new ArrayList<Group>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Group g = generateTestGroup();
            groups.add(g);
        }

        GroupsResponse response = api.syncGroups(groups);
        hqAssertSuccess(response);

        for (Group g : response.getGroup()) {
            // Re-lookup the group by id
            GroupResponse gResponse = api.getGroup(g.getId());
            hqAssertSuccess(gResponse);
            validateGroup(gResponse.getGroup());
        }

        cleanup(response.getGroup());
    }

    public void testGetGroupInvalidId() throws Exception {

        GroupApi api = getApi().getGroupApi();

        GroupResponse groupResponse = api.getGroup(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(groupResponse);
    }

    public void testGetGroupByName() throws Exception {

        GroupApi api = getApi().getGroupApi();

        List<Group> groups = new ArrayList<Group>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Group g = generateTestGroup();
            groups.add(g);
        }

        GroupsResponse response = api.syncGroups(groups);
        hqAssertSuccess(response);

        for (Group g : response.getGroup()) {
            // Re-lookup the group by name
            GroupResponse gResponse = api.getGroup(g.getName());
            hqAssertSuccess(gResponse);
            validateGroup(gResponse.getGroup());
        }

        cleanup(response.getGroup());
    }

    public void getGetGroupByInvalidName() throws Exception {

        GroupApi api = getApi().getGroupApi();

        GroupResponse groupResponse = api.getGroup("Non-existant group");
        hqAssertFailureObjectNotFound(groupResponse);
    }

    public void testGetCompatibleGroups() throws Exception {

        GroupApi api = getApi().getGroupApi();
        Resource platform = getLocalPlatformResource(false, false);

        List<Group> groups = new ArrayList<Group>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Group g = generateTestGroup();
            g.setResourcePrototype(platform.getResourcePrototype());
            groups.add(g);
        }

        GroupsResponse syncResponse = api.syncGroups(groups);
        hqAssertSuccess(syncResponse);

        GroupsResponse getCompatibleResponse = api.getCompatibleGroups();
        hqAssertSuccess(getCompatibleResponse);
        assertTrue("No compatible groups found!",
                   getCompatibleResponse.getGroup().size() > 0);
        for (Group g : getCompatibleResponse.getGroup()) {
            validateGroup(g);
            // All compatible groups have prototypes
            assertNotNull("No prototype found for " + g.getName(),
                          g.getResourcePrototype());
        }

        cleanup(syncResponse.getGroup());
    }

    public void testGetMixedGroups() throws Exception {

        GroupApi api = getApi().getGroupApi();

        List<Group> groups = new ArrayList<Group>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Group g = generateTestGroup();
            groups.add(g);
        }

        GroupsResponse syncResponse = api.syncGroups(groups);
        hqAssertSuccess(syncResponse);

        GroupsResponse getMixedResponse = api.getMixedGroups();
        hqAssertSuccess(getMixedResponse);
        assertTrue("No compatible groups found!",
                   getMixedResponse.getGroup().size() > 0);
        for (Group g : getMixedResponse.getGroup()) {
            validateGroup(g);
            assertNull("Prototype is set for mixed group " + g.getName(),
                       g.getResourcePrototype());
        }

        cleanup(syncResponse.getGroup());
    }

    public void testPlatformGetGroupsContaining() throws Exception {
        createAndGetGroupsForPlatform(true);
    }
    
    public void testPlatformGetGroupsNotContaining() throws Exception {
        createAndGetGroupsForPlatform(false);
    }
    
    public void testServerGetGroupsContaining() throws Exception {
        createAndGetGroupsForServer(true);
    }
    
    public void testServerGetGroupsNotContaining() throws Exception {
        createAndGetGroupsForServer(false);
    }
    
    public void testServiceGetGroupsContaining() throws Exception {
        createAndGetGroupsForService(true);
    }
    
    /**
     * To validate HHQ-3473
     */
    public void testServiceGetGroupsNotContaining() throws Exception {
        createAndGetGroupsForService(false);
    }
    
    public void testServiceGetGroupsNotContainingInvalidResource()
        throws Exception {
        
        GroupApi groupApi = getApi().getGroupApi();

        Resource service = new Resource();
        service.setId(Integer.MAX_VALUE);
        service.setName("Invalid Service Resource");

        GroupsResponse getResponse = groupApi.getGroupsNotContaining(service);
        hqAssertFailureObjectNotFound(getResponse);
    }

    /**
     * To validate HQ-1309
     * 
     * @throws Exception
     */
    public void testGetGroupsByRole() throws Exception {
        GroupApi groupApi = getApi().getGroupApi();
        RoleApi roleApi = getApi().getRoleApi();
        
        List<Operation> operations = new ArrayList<Operation>();
        operations.add(Operation.MANAGE_PLATFORM_ALERTS);
        operations.add(Operation.VIEW_PLATFORM);
        
        Role role1 = createRole(Collections.EMPTY_LIST,
                                operations);

        operations.add(Operation.MODIFY_PLATFORM);
        Role role2 = createRole(Collections.EMPTY_LIST,
                                operations);

        List<Role> roles = new ArrayList<Role>();
        roles.add(role1);
        roles.add(role2);

        // Create test roles
        StatusResponse response = roleApi.syncRoles(roles);
        hqAssertSuccess(response);

        List<Role> createdRoles = new ArrayList<Role>();
        
        for (Role r : roles) {
            RoleResponse getResponse = roleApi.getRole(r.getName());
            hqAssertSuccess(getResponse);
            Role role = getResponse.getRole();
            createdRoles.add(role);
        }
        
        Resource platform = getLocalPlatformResource(false, false);

        // Create test group with the test roles
        Group groupWithRoles = createGroup(Collections.singletonList(platform),
                                           createdRoles);
        
        for (Role r : createdRoles) {
            // This is the code being tested to validate HQ-1309.
            GroupsResponse groupsResponse = groupApi.getGroups(r);
            hqAssertSuccess(groupsResponse);
            List<Group> roleGroups = groupsResponse.getGroup();
            
            assertEquals("Only 1 group should belong to this role.",
                         1, roleGroups.size());
            
            Group g = roleGroups.get(0);
            
            assertEquals(groupWithRoles.getId(), g.getId());                
            assertEquals("The group should be assigned to " + createdRoles.size() + " roles.",
                         createdRoles.size(), g.getRole().size());
        }
        
        // Cleanup
        cleanupRoles();
        cleanupGroup(groupWithRoles);
    }

    private void createAndGetGroupsForPlatform(boolean containing) 
        throws Exception {

        GroupApi groupApi = getApi().getGroupApi();

        Resource platform = getLocalPlatformResource(false, false);
                
        // Create test groups
        List<Group> expectedGroups = createMixedAndCompatibleGroups(platform, containing);

        // Get groups
        GroupsResponse getResponse = null;
        if (containing) {
            getResponse = groupApi.getGroupsContaining(platform);
        } else {
            getResponse = groupApi.getGroupsNotContaining(platform);
        }
        hqAssertSuccess(getResponse);
        validateGetGroups(expectedGroups, getResponse.getGroup());
                
        // Cleanup
        cleanup(expectedGroups);
    }
    
    private void createAndGetGroupsForServer(boolean containing) 
        throws Exception {
        
        GroupApi groupApi = getApi().getGroupApi();

        Resource platform = getLocalPlatformResource(false, true);
        Resource server = null;
        
        for (Resource child : platform.getResource()) {
            String serverPrototype = child.getResourcePrototype().getName();
            
            if (serverPrototype.equals("JBoss 4.2")
                    || serverPrototype.equals("Tomcat 6.0")
                    || serverPrototype.equals("HQ Agent")) {
                
                server = child;
                break;
            }
        }
        assertNotNull("A server could not be found", server);
        
        // Create test groups
        List<Group> expectedGroups = createMixedAndCompatibleGroups(server, containing);

        // Get groups
        GroupsResponse getResponse = null;
        if (containing) {
            getResponse = groupApi.getGroupsContaining(server);
        } else {
            getResponse = groupApi.getGroupsNotContaining(server);
        }
        hqAssertSuccess(getResponse);
        validateGetGroups(expectedGroups, getResponse.getGroup());
        
        // Cleanup
        cleanup(expectedGroups);       
    }
    
    private void createAndGetGroupsForService(boolean containing) throws Exception {
        
        GroupApi groupApi = getApi().getGroupApi();
        ResourceApi resourceApi = getApi().getResourceApi();
        
        // Find CPU resources
        ResourcePrototypeResponse cpuPrototypeResponse =
                resourceApi.getResourcePrototype("CPU");
        hqAssertSuccess(cpuPrototypeResponse);

        ResourcePrototype cpuPrototype = cpuPrototypeResponse.getResourcePrototype();
        ResourcesResponse resourceResponse =
                resourceApi.getResources(cpuPrototype,
                                         false, false);
        hqAssertSuccess(resourceResponse);
        assertFalse(resourceResponse.getResource().isEmpty());
        Resource cpu = resourceResponse.getResource().get(0);
        
        // Create test groups
        List<Group> expectedGroups = createMixedAndCompatibleGroups(cpu, containing);

        // Get groups
        GroupsResponse getResponse = null;
        if (containing) {
            getResponse = groupApi.getGroupsContaining(cpu);
        } else {
            getResponse = groupApi.getGroupsNotContaining(cpu);
        }
        hqAssertSuccess(getResponse);
        validateGetGroups(expectedGroups, getResponse.getGroup());
        
        // Cleanup
        cleanup(expectedGroups);
    }
    
    private List<Group> createMixedAndCompatibleGroups(Resource r,
                                                       boolean isGroupMember)
        throws IOException {

        GroupApi groupApi = getApi().getGroupApi();

        // Create mixed group
        Group mixedGroup = generateTestGroup();
        if (isGroupMember) {
            mixedGroup.getResource().add(r);
        }
        
        // Create compatible group
        Group compatGroup = generateTestGroup();
        compatGroup.setResourcePrototype(r.getResourcePrototype());
        if (isGroupMember) {
            compatGroup.getResource().add(r);
        }
        
        // Add groups
        List<Group> groups = new ArrayList<Group>();
        groups.add(mixedGroup);
        groups.add(compatGroup);
        
        GroupsResponse syncResponse = groupApi.syncGroups(groups);
        hqAssertSuccess(syncResponse);
        List<Group> createdGroups = syncResponse.getGroup();
        assertEquals(2, createdGroups.size());
        
        if (isGroupMember) {
            for (Group g: createdGroups) {
                assertEquals(1, g.getResource().size());
            }
        }

        return createdGroups;
    }
    
    private void validateGetGroups(List<Group> expectedGroups,
                                   List<Group> actualGroups)
        throws IOException {
        
        assertFalse(expectedGroups.isEmpty());
        assertFalse(actualGroups.isEmpty());
        assertTrue(actualGroups.size() >= expectedGroups.size());
        
        // Create map for the new groups for easy lookup during validation
        Set<Integer> expectedGroupIds = new HashSet<Integer>();
        for (Group g : expectedGroups) {
            expectedGroupIds.add(g.getId());
        }
                               
        for (Group g : actualGroups) {
            validateGroup(g);
            if (expectedGroupIds.contains(g.getId())) {
                assertTrue(expectedGroupIds.remove(g.getId()));
            }
        }
        
        assertTrue(expectedGroupIds.size() + " expected groups were not found",
                   expectedGroupIds.isEmpty());        
    }
}
