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

import org.hyperic.hq.hqapi1.MaintenanceApi;
import org.hyperic.hq.hqapi1.types.MaintenanceResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.StatusResponse;

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

    public void testUnschedule() throws Exception {

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

    public void testUnscheduleNotInMaintenance() throws Exception {
 
        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();

        StatusResponse unscheduleResponse = mApi.unschedule(g.getId());
        hqAssertSuccess(unscheduleResponse);

        cleanupGroup(g);
    }
}
