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

import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.MaintenanceEvent;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.GroupApi;

import java.util.Random;

public class MaintenanceTestBase extends HQApiTestBase {

    public MaintenanceTestBase(String name) {
        super(name);
    }

    void cleanupGroup(Group g) throws Exception {
        GroupApi api = getApi().getGroupApi();
        StatusResponse response = api.deleteGroup(g.getId());
        hqAssertSuccess(response);
    }

    Group getFileServerMountCompatibleGroup() throws Exception {

        HQApi api = getApi();
        ResourceApi resourceApi = api.getResourceApi();
        GroupApi groupApi = api.getGroupApi();

        ResourcePrototypeResponse protoResponse =
                resourceApi.getResourcePrototype("FileServer Mount");
        hqAssertSuccess(protoResponse);

        ResourcesResponse resources = resourceApi.getResources(protoResponse.getResourcePrototype(),
                                                               false, false);
        hqAssertSuccess(resources);
        assertTrue("Unable to find resources of type " +
                   protoResponse.getResourcePrototype().getName(),
                   resources.getResource().size() > 0);

        Random r = new Random();
        Group g = new Group();
        g.setName("Compatible Group for Maintenance Tests" + r.nextInt());
        g.setResourcePrototype(protoResponse.getResourcePrototype());
        g.getResource().addAll(resources.getResource());

        GroupResponse groupResponse = groupApi.createGroup(g);
        hqAssertSuccess(groupResponse);

        return groupResponse.getGroup();
    }

    void valididateMaintenanceEvent(MaintenanceEvent e, Group g, long start, long end) {
        assertEquals(e.getGroupId(), g.getId().intValue());
        assertEquals(e.getStartTime(), start);
        assertEquals(e.getEndTime(), end);        
    }
}
