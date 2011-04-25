/*
 * NOTE: This copyright does *not* cover user programs that use Hyperic
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 *
 * Copyright (C) [2004-2011], VMware, Inc.
 * This file is part of Hyperic.
 *
 * Hyperic is free software; you can redistribute it and/or modify
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
 */

package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MaintenanceApi;
import org.hyperic.hq.hqapi1.types.MaintenanceResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.Operation;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RoleResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;

import java.util.List;

public class MaintenanceUnschedule_test extends MaintenanceTestBase {

    private static final long HOUR = 60 * 60 * 1000;
        
    public MaintenanceUnschedule_test(String name) {
        super(name);
    }

    public void testUnscheduleInvalidGroup() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        long start = System.currentTimeMillis() + HOUR;
        long end = start + HOUR;
        MaintenanceResponse response = mApi.schedule(Integer.MAX_VALUE,
                                                     start, end);
        hqAssertFailureObjectNotFound(response);
    }

    public void testUnscheduleGroup() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();
        long start = System.currentTimeMillis() + HOUR;
        long end = start + HOUR;
        MaintenanceResponse response = mApi.schedule(g.getId(),
                                                     start, end);
        hqAssertSuccess(response);

        StatusResponse unscheduleResponse = mApi.unschedule(g.getId());
        hqAssertSuccess(unscheduleResponse);

        cleanupGroup(g);
    }
    
    public void testUnscheduleService() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        List<Resource> resources = getFileServerMountResources();
        Resource service = resources.get(0);
        long start = System.currentTimeMillis() + HOUR;
        long end = start + HOUR;
        MaintenanceResponse response = mApi.schedule(service,
                                                     start, end);
        hqAssertSuccess(response);

        StatusResponse unscheduleResponse = mApi.unschedule(service);
        hqAssertSuccess(unscheduleResponse);
    }

    public void testUnscheduleGroupNotInMaintenance() throws Exception {
 
        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();

        StatusResponse unscheduleResponse = mApi.unschedule(g.getId());
        hqAssertSuccess(unscheduleResponse);

        cleanupGroup(g);
    }

    public void testUnscheduleServiceNotInMaintenance() throws Exception {
    	 
        MaintenanceApi mApi = getApi().getMaintenanceApi();

        List<Resource> resources = getFileServerMountResources();
        Resource service = resources.get(0);

        StatusResponse unscheduleResponse = mApi.unschedule(service);
        hqAssertSuccess(unscheduleResponse);
    }
    
    public void testUnscheduleGroupWithNoGroupPermission() throws Exception {
        
        List<User> users = createTestUsers(1);
        User user = users.get(0);
        
        HQApi apiUnpriv = getApi(user.getName(), TESTUSER_PASSWORD);
        MaintenanceApi mApi = apiUnpriv.getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();

        StatusResponse response = mApi.unschedule(g.getId());
        hqAssertFailurePermissionDenied(response);

        deleteTestUsers(users);
        cleanupGroup(g);         
    }

    public void testUnscheduleServiceWithNoServicePermission() throws Exception {
        
        List<User> users = createTestUsers(1);
        User user = users.get(0);
        
        HQApi apiUnpriv = getApi(user.getName(), TESTUSER_PASSWORD);
        MaintenanceApi mApi = apiUnpriv.getMaintenanceApi();

        List<Resource> resources = getFileServerMountResources();
        Resource service = resources.get(0);

        StatusResponse response = mApi.unschedule(service);
        hqAssertFailurePermissionDenied(response);

        deleteTestUsers(users);
    }
    
    /**
     * To validate HQ-1832
     */
    public void testUnscheduleWithNoMaintenancePermission() throws Exception {
        
        // create user
        List<User> users = createTestUsers(1);
        User user = users.get(0);

        // create role with view group permissions
        Role r = generateTestRole();
        r.getOperation().add(Operation.VIEW_RESOURCE_GROUP);
        r.getUser().add(user);
        RoleResponse roleResponse = getApi().getRoleApi().createRole(r);
        hqAssertSuccess(roleResponse);
        Role viewRole = roleResponse.getRole();
        assertEquals("The role should have one user",
                     1, viewRole.getUser().size());
        assertTrue("The role should have view group permissions",
                   viewRole.getOperation().contains(Operation.VIEW_RESOURCE_GROUP));
        
        // create group with view role
        Group g = getFileServerMountCompatibleGroup();
        g.getRole().add(viewRole);
        GroupResponse groupResponse = getApi().getGroupApi().updateGroup(g);
        hqAssertSuccess(groupResponse);
        Group groupWithRole = groupResponse.getGroup();
        assertEquals("The group should have one role",
                     1, groupWithRole.getRole().size());

        HQApi apiUnpriv = getApi(user.getName(), TESTUSER_PASSWORD);
        MaintenanceApi mApi = apiUnpriv.getMaintenanceApi();

        // unschedule maintanence for group with insufficient permissions
        StatusResponse statusResponse = mApi.unschedule(groupWithRole.getId());
        hqAssertFailurePermissionDenied(statusResponse);

        // unschedule maintanence for resource with insufficient permissions
        Resource resource = g.getResource().get(0);
        statusResponse = mApi.unschedule(resource);
        hqAssertFailurePermissionDenied(statusResponse);

        // cleanup
        deleteTestUsers(users);
        cleanupRole(viewRole);
        cleanupGroup(groupWithRole);
    }
}
