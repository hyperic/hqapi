/*
 *
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 *
 * Copyright (C) [2008-2010], Hyperic, Inc.
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

import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.MaintenanceEvent;
import org.hyperic.hq.hqapi1.types.MaintenanceResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;

import java.util.List;
import java.util.Random;

public abstract class MaintenanceTestBase extends AlertTestBase {

    public MaintenanceTestBase(String name) {
        super(name);
    }

    MaintenanceEvent get(Group g) throws Exception {

        MaintenanceResponse getResponse = 
            getApi().getMaintenanceApi().get(g.getId());
        
        hqAssertSuccess(getResponse);

        return getResponse.getMaintenanceEvent();
    }
    
    MaintenanceEvent schedule(Group g, long start, long end)
        throws Exception {

        MaintenanceResponse response = 
            getApi().getMaintenanceApi().schedule(g.getId(), start, end);
        
        hqAssertSuccess(response);
        
        MaintenanceEvent event = response.getMaintenanceEvent();
        assertNotNull("The scheduled maintenance event should not be null",
                      event);
        valididateMaintenanceEvent(event, g, start, end);
        
        return event;
    }

    Group getFileServerMountCompatibleGroup() throws Exception {

        ResourceApi resourceApi = getApi().getResourceApi();

        ResourcePrototypeResponse protoResponse =
                resourceApi.getResourcePrototype("FileServer Mount");
        hqAssertSuccess(protoResponse);

        ResourcesResponse resources = resourceApi.getResources(protoResponse.getResourcePrototype(),
                                                               false, false);
        hqAssertSuccess(resources);
        assertTrue("Unable to find resources of type " +
                   protoResponse.getResourcePrototype().getName(),
                   resources.getResource().size() > 0);

        return createGroup(resources.getResource());
    }

    void valididateMaintenanceEvent(MaintenanceEvent e, Group g, long start, long end) {
        assertEquals(e.getGroupId(), g.getId().intValue());
        assertEquals(e.getStartTime(), start);
        assertEquals(e.getEndTime(), end);
        assertNotNull(e.getModifiedBy());
        assertTrue(e.getModifiedBy().trim().length() > 0);
    }
}
