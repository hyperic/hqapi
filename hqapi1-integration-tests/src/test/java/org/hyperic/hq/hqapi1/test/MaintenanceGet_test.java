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
import org.hyperic.hq.hqapi1.types.MaintenanceEvent;
import org.hyperic.hq.hqapi1.types.MaintenanceState;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;

import java.util.List;

public class MaintenanceGet_test extends MaintenanceTestBase {

    private static final long HOUR = 60 * 60 * 1000;

    public MaintenanceGet_test(String name) {
        super(name);
    }

    public void testGetInvalidGroup() throws Exception {
        MaintenanceApi mApi = getApi().getMaintenanceApi();
        MaintenanceResponse response = mApi.get(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetGroupNotInMaintenance() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();
        MaintenanceResponse response = mApi.get(g.getId());
        hqAssertSuccess(response);

        assertNull("Maintenance event found for group not in maintenance",
                    response.getMaintenanceEvent());

        cleanupGroup(g);
    }

    public void testGetServiceNotInMaintenance() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        List<Resource> resources = getFileServerMountResources();
        Resource service = resources.get(0);
        MaintenanceResponse response = mApi.get(service);
        hqAssertSuccess(response);

        assertNull("Maintenance event found for resource not in maintenance",
                    response.getMaintenanceEvent());
    }
    
    public void testGetGroup() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();
        long start = System.currentTimeMillis() + HOUR;
        long end = start + HOUR;
        
        MaintenanceEvent e = schedule(g, start, end);
        e = get(g);
        assertNotNull("Maintenance event not found for valid group " + g.getName(), e);
        validateMaintenanceEvent(e, g, start, end);

        StatusResponse unscheduleResponse = mApi.unschedule(g.getId());
        hqAssertSuccess(unscheduleResponse);

        cleanupGroup(g);
    }
    
    public void testGetService() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        List<Resource> resources = getFileServerMountResources();
        Resource service = resources.get(0);
        long start = System.currentTimeMillis() + HOUR;
        long end = start + HOUR;
        
        MaintenanceEvent e = schedule(service, start, end);
        e = get(service);
        assertNotNull("Maintenance event not found for valid resource " + service.getName(), e);
        validateMaintenanceEvent(e, service, start, end);

        StatusResponse unscheduleResponse = mApi.unschedule(service);
        hqAssertSuccess(unscheduleResponse);
    }
    
    public void testGetAll() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        List<Resource> resources = getFileServerMountResources();
        Resource service = resources.get(0);
        long start = System.currentTimeMillis() + HOUR;
        long end = start + HOUR;
        
        MaintenanceEvent e = schedule(service, start, end);
        List<MaintenanceEvent> schedules = getAll(null);
        
        assertTrue("Unable to find any maintenance events",
                   schedules.size() > 0);

        StatusResponse unscheduleResponse = mApi.unschedule(service);
        hqAssertSuccess(unscheduleResponse);
    }

    public void testGetNew() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        List<Resource> resources = getFileServerMountResources();
        Resource service = resources.get(0);
        long start = System.currentTimeMillis() + HOUR;
        long end = start + HOUR;
        
        MaintenanceEvent e = schedule(service, start, end);
        List<MaintenanceEvent> schedules = getAll(MaintenanceState.NEW);
        
        assertTrue("Unable to find any new maintenance events",
                   schedules.size() > 0);

        StatusResponse unscheduleResponse = mApi.unschedule(service);
        hqAssertSuccess(unscheduleResponse);
    }
    
    public void testGetGroupWithNoGroupPermission() throws Exception {

        List<User> users = createTestUsers(1);
        User user = users.get(0);
        
        HQApi apiUnpriv = getApi(user.getName(), TESTUSER_PASSWORD);
        MaintenanceApi mApi = apiUnpriv.getMaintenanceApi();
        
        Group g = getFileServerMountCompatibleGroup();
        MaintenanceResponse response = mApi.get(g.getId());
        hqAssertFailurePermissionDenied(response);

        deleteTestUsers(users);
        cleanupGroup(g);
    }
    
    public void testGetServiceWithNoServicePermission() throws Exception {

        List<User> users = createTestUsers(1);
        User user = users.get(0);
        
        HQApi apiUnpriv = getApi(user.getName(), TESTUSER_PASSWORD);
        MaintenanceApi mApi = apiUnpriv.getMaintenanceApi();
        
        List<Resource> resources = getFileServerMountResources();
        Resource service = resources.get(0);
        MaintenanceResponse response = mApi.get(service);
        hqAssertFailurePermissionDenied(response);

        deleteTestUsers(users);
    }
}
