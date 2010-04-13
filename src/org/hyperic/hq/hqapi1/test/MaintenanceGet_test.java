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

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MaintenanceApi;
import org.hyperic.hq.hqapi1.types.MaintenanceResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.MaintenanceEvent;
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

    public void testGetNotInMaintenance() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();
        MaintenanceResponse response = mApi.get(g.getId());
        hqAssertSuccess(response);

        assertNull("Maintenance event found for group not in maintenance",
                    response.getMaintenanceEvent());

        cleanupGroup(g);
    }

    public void testGet() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();
        long start = System.currentTimeMillis() + HOUR;
        long end = start + HOUR;
        
        MaintenanceEvent e = schedule(g, start, end);
        e = get(g);
        assertNotNull("Maintenance event not found for valid group " + g.getName(), e);
        valididateMaintenanceEvent(e, g, start, end);

        StatusResponse unscheduleResponse = mApi.unschedule(g.getId());
        hqAssertSuccess(unscheduleResponse);

        cleanupGroup(g);
    }
    
    public void testGetNoGroupPermission() throws Exception {

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
}
