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

import java.util.ArrayList;
import java.util.List;

import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Criteria;
import org.hyperic.hq.hqapi1.types.CriteriaList;
import org.hyperic.hq.hqapi1.types.CriteriaProp;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.GroupsResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.RolesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

/**
 * This class tests all aspects of the group sync. (which includes
 * update/create)
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
		StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup
				.getId());
		hqAssertSuccess(deleteResponse);
	}

	public void testCreateCompatible() throws Exception {

		HQApi api = getApi();
		ResourceApi resourceApi = api.getResourceApi();
		RoleApi roleApi = api.getRoleApi();
		GroupApi groupApi = api.getGroupApi();

		// Find CPU resources
		ResourcePrototypeResponse prototypeResponse = resourceApi
				.getResourcePrototype("CPU");
		hqAssertSuccess(prototypeResponse);

		ResourcesResponse resourceResponse = resourceApi.getResources(
				prototypeResponse.getResourcePrototype(), false, false);
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
		assertEquals(g.getResourcePrototype().getName(), prototypeResponse
				.getResourcePrototype().getName());
		assertEquals(createdGroup.getResource().size(), g.getResource().size());
		assertEquals(createdGroup.getRole().size(), g.getRole().size());

		// Cleanup
		StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup
				.getId());
		hqAssertSuccess(deleteResponse);
	}

	public void testCreateCompatibleWithCriteriaMatchingAll() throws Exception {
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
          g.getRole().addAll(roleResponse.getRole());
          
          Criteria groupCriteria = new Criteria();
		  groupCriteria
				.setClazz("org.hyperic.hq.grouping.critters.ProtoCritterType");
		  CriteriaProp criteriaProp = new CriteriaProp();
		  criteriaProp.setName("protoType");
		  criteriaProp.setValue("CPU");
		  criteriaProp.setType("proto");
		  groupCriteria.getCriteriaProp().add(criteriaProp);
		  CriteriaList criteriaList = new CriteriaList();
		  criteriaList.setAny(false);
		  criteriaList.getCriteria().add(groupCriteria);
          g.setCriteriaList(criteriaList);
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
                       resourceResponse.getResource().size());
          assertEquals(createdGroup.getRole().size(),
                       g.getRole().size());

          // Cleanup
          StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup.getId());
          hqAssertSuccess(deleteResponse);
    }
	
	public void testCreateCompatibleWithCriteriaNotMatchingAll() throws Exception {
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
        g.getRole().addAll(roleResponse.getRole());
        
        Criteria groupCriteria = new Criteria();
		groupCriteria
				.setClazz("org.hyperic.hq.grouping.critters.ProtoCritterType");
		CriteriaProp criteriaProp = new CriteriaProp();
		criteriaProp.setName("protoType");
		criteriaProp.setValue("CPU");
		criteriaProp.setType("proto");
		groupCriteria.getCriteriaProp().add(criteriaProp);
		
		Criteria nameCriteria = new Criteria();
		nameCriteria
					.setClazz("org.hyperic.hq.grouping.critters.ResourceNameCritterType");
		CriteriaProp nameProp = new CriteriaProp();
		nameProp.setName("name");
		nameProp.setValue("foo");
		nameProp.setType("string");
		nameCriteria.getCriteriaProp().add(nameProp);
			
		CriteriaList criteriaList = new CriteriaList();
		criteriaList.setAny(false);
		criteriaList.getCriteria().add(groupCriteria);
		criteriaList.getCriteria().add(nameCriteria);
        g.setCriteriaList(criteriaList);
        GroupResponse createResponse = groupApi.createGroup(g);
        hqAssertSuccess(createResponse);

        Group createdGroup = createResponse.getGroup();
        validateGroup(createdGroup);
        assertEquals(createdGroup.getName(), g.getName());
        assertEquals(createdGroup.getDescription(), g.getDescription());
        assertEquals(createdGroup.getLocation(), g.getLocation());
        assertEquals(g.getResourcePrototype().getName(),
                     prototypeResponse.getResourcePrototype().getName());
        assertEquals(0,createdGroup.getResource().size());
        assertEquals(createdGroup.getRole().size(),
                     g.getRole().size());

        // Cleanup
        StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup.getId());
        hqAssertSuccess(deleteResponse);
  }
	
	public void testCreateCompatibleWithCriteriaNotMatchingAny() throws Exception {
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
	        g.getRole().addAll(roleResponse.getRole());
	        
	        
			
			Criteria nameCriteria = new Criteria();
			nameCriteria
						.setClazz("org.hyperic.hq.grouping.critters.ResourceNameCritterType");
			CriteriaProp nameProp = new CriteriaProp();
			nameProp.setName("name");
			nameProp.setValue("foo");
			nameProp.setType("string");
			nameCriteria.getCriteriaProp().add(nameProp);
			
			Criteria nameCriteria2 = new Criteria();
			nameCriteria2
						.setClazz("org.hyperic.hq.grouping.critters.ResourceNameCritterType");
			CriteriaProp nameProp2 = new CriteriaProp();
			nameProp2.setName("name");
			nameProp2.setValue("bar");
			nameProp2.setType("string");
			nameCriteria2.getCriteriaProp().add(nameProp2);
				
			CriteriaList criteriaList = new CriteriaList();
			criteriaList.setAny(true);
			criteriaList.getCriteria().add(nameCriteria);
			criteriaList.getCriteria().add(nameCriteria2);
	        g.setCriteriaList(criteriaList);
	        GroupResponse createResponse = groupApi.createGroup(g);
	        hqAssertSuccess(createResponse);

	        Group createdGroup = createResponse.getGroup();
	        validateGroup(createdGroup);
	        assertEquals(createdGroup.getName(), g.getName());
	        assertEquals(createdGroup.getDescription(), g.getDescription());
	        assertEquals(createdGroup.getLocation(), g.getLocation());
	        assertEquals(g.getResourcePrototype().getName(),
	                     prototypeResponse.getResourcePrototype().getName());
	        assertEquals(0,createdGroup.getResource().size());
	        assertEquals(createdGroup.getRole().size(),
	                     g.getRole().size());

	        // Cleanup
	        StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup.getId());
	        hqAssertSuccess(deleteResponse);
	  }
	
	public void testCreateCompatibleWithCriteriaMatchingAny() throws Exception {
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
        g.getRole().addAll(roleResponse.getRole());
        
        Criteria groupCriteria = new Criteria();
		groupCriteria
				.setClazz("org.hyperic.hq.grouping.critters.ProtoCritterType");
		CriteriaProp criteriaProp = new CriteriaProp();
		criteriaProp.setName("protoType");
		criteriaProp.setValue("CPU");
		criteriaProp.setType("proto");
		groupCriteria.getCriteriaProp().add(criteriaProp);
		CriteriaList criteriaList = new CriteriaList();
		criteriaList.setAny(true);
		criteriaList.getCriteria().add(groupCriteria);
        g.setCriteriaList(criteriaList);
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
                     resourceResponse.getResource().size());
        assertEquals(createdGroup.getRole().size(),
                     g.getRole().size());

        // Cleanup
        StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup.getId());
        hqAssertSuccess(deleteResponse);
  }

	public void testCreateNoName() throws Exception {

		GroupApi groupApi = getApi().getGroupApi();

		ResourcePrototype type = new ResourcePrototype();
		type.setName("CPU");

		// Create
		Group g = generateTestGroup();
		g.setName(null);
		g.setResourcePrototype(type);

		GroupResponse createResponse = groupApi.createGroup(g);
		hqAssertFailureInvalidParameters(createResponse);
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
		StatusResponse deleteResponse = groupApi.deleteGroup(updatedGroup
				.getId());
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
		assertTrue(updatedGroup.getRole().size() == roleResponse.getRole()
				.size());

		// Clear all roles
		updatedGroup.getRole().clear();
		updateResponse = groupApi.updateGroup(updatedGroup);
		hqAssertSuccess(updateResponse);
		updatedGroup = updateResponse.getGroup();
		assertTrue(updatedGroup.getRole().size() == 0);

		// Cleanup
		StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup
				.getId());
		hqAssertSuccess(deleteResponse);
	}
	
	public void testUpdateCriteria() throws Exception {
		HQApi api = getApi();
		GroupApi groupApi = api.getGroupApi();
		ResourceApi resourceApi = api.getResourceApi();

		// Create
		Group g = generateTestGroup();
		GroupResponse createResponse = groupApi.createGroup(g);
		hqAssertSuccess(createResponse);
		
		// Find CPU resources
		ResourcePrototypeResponse prototypeResponse = resourceApi
				.getResourcePrototype("CPU");
		hqAssertSuccess(prototypeResponse);

		ResourcesResponse resourceResponse = resourceApi.getResources(
				prototypeResponse.getResourcePrototype(), false, false);
		hqAssertSuccess(resourceResponse);

		Group createdGroup = createResponse.getGroup();
		assertTrue(createdGroup.getResource().size() == 0);
		
		Criteria groupCriteria = new Criteria();
		groupCriteria
				.setClazz("org.hyperic.hq.grouping.critters.ProtoCritterType");
		CriteriaProp criteriaProp = new CriteriaProp();
		criteriaProp.setName("protoType");
		criteriaProp.setValue("CPU");
		criteriaProp.setType("proto");
		groupCriteria.getCriteriaProp().add(criteriaProp);
		CriteriaList criteriaList = new CriteriaList();
		criteriaList.setAny(true);
		criteriaList.getCriteria().add(groupCriteria);
        createdGroup.setCriteriaList(criteriaList);

        GroupResponse updateResponse = groupApi.updateGroup(createdGroup);
		hqAssertSuccess(updateResponse);
		Group updatedGroup = updateResponse.getGroup();
		assertEquals(resourceResponse.getResource().size(),updatedGroup.getResource().size());


		// Cleanup
		StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup
				.getId());
		hqAssertSuccess(deleteResponse);
	}

	public void testClearResources() throws Exception {

		HQApi api = getApi();
		ResourceApi resourceApi = api.getResourceApi();
		GroupApi groupApi = api.getGroupApi();

		// Find CPU resources
		ResourcePrototypeResponse prototypeResponse = resourceApi
				.getResourcePrototype("CPU");
		hqAssertSuccess(prototypeResponse);

		ResourcesResponse resourceResponse = resourceApi.getResources(
				prototypeResponse.getResourcePrototype(), false, false);
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
		assertTrue(updatedGroup.getResource().size() == resourceResponse
				.getResource().size());

		// Clear all resources
		updatedGroup.getResource().clear();
		updateResponse = groupApi.updateGroup(updatedGroup);
		hqAssertSuccess(updateResponse);
		updatedGroup = updateResponse.getGroup();
		assertTrue(updatedGroup.getResource().size() == 0);

		// Cleanup
		StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup
				.getId());
		hqAssertSuccess(deleteResponse);
	}

	public void testUpdateDuplicateResources() throws Exception {

		HQApi api = getApi();
		ResourceApi resourceApi = api.getResourceApi();
		GroupApi groupApi = api.getGroupApi();

		// Find CPU resources
		ResourcePrototypeResponse prototypeResponse = resourceApi
				.getResourcePrototype("CPU");
		hqAssertSuccess(prototypeResponse);

		ResourcesResponse resourceResponse = resourceApi.getResources(
				prototypeResponse.getResourcePrototype(), false, false);
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
		assertTrue(updatedGroup.getResource().size() == resourceResponse
				.getResource().size());

		// Update with same resources
		updatedGroup.getResource().addAll(resourceResponse.getResource());
		updateResponse = groupApi.updateGroup(updatedGroup);
		hqAssertSuccess(updateResponse);
		updatedGroup = updateResponse.getGroup();
		// Ensure no duplicates
		assertTrue("Unexpected member count expected = "
				+ resourceResponse.getResource().size() + " found="
				+ updatedGroup.getResource().size(), updatedGroup.getResource()
				.size() == resourceResponse.getResource().size());

		// Cleanup
		StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup
				.getId());
		hqAssertSuccess(deleteResponse);
	}

	public void testUpdateResourcesWrongPrototype() throws Exception {
		HQApi api = getApi();
		ResourceApi resourceApi = api.getResourceApi();
		GroupApi groupApi = api.getGroupApi();

		// Find CPU resources
		ResourcePrototypeResponse cpuPrototypeResponse = resourceApi
				.getResourcePrototype("CPU");
		hqAssertSuccess(cpuPrototypeResponse);
		ResourcePrototypeResponse fileServerFileResponse = resourceApi
				.getResourcePrototype("FileServer File");
		hqAssertSuccess(fileServerFileResponse);

		ResourcesResponse resourceResponse = resourceApi.getResources(
				cpuPrototypeResponse.getResourcePrototype(), false, false);
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
		StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup
				.getId());
		hqAssertSuccess(deleteResponse);
	}

	public void testUpdatePrototype() throws Exception {
		HQApi api = getApi();
		ResourceApi resourceApi = api.getResourceApi();
		GroupApi groupApi = api.getGroupApi();

		// Find prototypes
		ResourcePrototypeResponse cpuProtoResponse = resourceApi
				.getResourcePrototype("CPU");
		hqAssertSuccess(cpuProtoResponse);
		ResourcePrototypeResponse fileServerFileResponse = resourceApi
				.getResourcePrototype("FileServer File");
		hqAssertSuccess(fileServerFileResponse);

		// Create
		Group g = generateTestGroup();
		g.setResourcePrototype(cpuProtoResponse.getResourcePrototype());
		GroupResponse createResponse = groupApi.createGroup(g);
		hqAssertSuccess(createResponse);
		Group createdGroup = createResponse.getGroup();

		// Update prototype
		createdGroup.setResourcePrototype(fileServerFileResponse
				.getResourcePrototype());
		GroupResponse updateResponse = groupApi.updateGroup(createdGroup);
		hqAssertFailureNotSupported(updateResponse);

		// Cleanup
		StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup
				.getId());
		hqAssertSuccess(deleteResponse);
	}

	public void testUpdateSystemGroup() throws Exception {
		HQApi api = getApi();
		GroupApi groupApi = api.getGroupApi();

		Group g = new Group();
		g.setId(0);
		g.setName("Updated Name");
		List<Group> groups = new ArrayList<Group>();
		groups.add(g);

		// We don't allow updates of system groups.
		GroupsResponse syncResponse = groupApi.syncGroups(groups);
		hqAssertFailureNotSupported(syncResponse);
	}
}
