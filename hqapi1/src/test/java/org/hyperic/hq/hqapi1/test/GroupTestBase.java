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

import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public abstract class GroupTestBase extends HQApiTestBase {

    static final String GROUP_NAME = "API Test Group";
    static final String GROUP_LOCATION = "API Test Group Location";
    static final String GROUP_DESCRIPTION = "API Test Group Description";

    public GroupTestBase(String name) {
        super(name);
    }

    protected void validateGroup(Group g) {
        assertTrue("Invalid id for Group.", g.getId() >= 0);
        assertTrue("Found invalid name for Group with id=" + g.getId(),
                   g.getName().length() > 0);

        if (g.getResourcePrototype() != null) {
            ResourcePrototype pt = g.getResourcePrototype();
            assertTrue("Invalid prototype id", pt.getId() > 0);
            assertTrue("Invalid prototype name for group " + g.getName(),
                       pt.getName().length() > 0);
        }

        if (g.getResource().size() > 0) {
            for (Resource r : g.getResource()) {
                assertTrue("Invalid resource id for group member", r.getId() > 0);
                assertTrue("Invalid resource name for group member",
                           r.getName().length() > 0);
            }
        }

        if (g.getRole().size() > 0) {
            for (Role r : g.getRole()) {
                assertTrue("Invalid role id", r.getId() >= 0);
                assertTrue("Invalid role name", r.getName().length() > 0);
            }
        }
    }

    /**
     * Generate a valid Group object that's guaranteed to have a unique Name
     *
     * @return A valid Group object.
     */
    protected Group generateTestGroup() {

        Random r = new Random();

        Group group = new Group();
        group.setName(GROUP_NAME + r.nextInt());
        group.setDescription(GROUP_DESCRIPTION);
        group.setLocation(GROUP_LOCATION);

        return group;
    }

    protected void cleanup(List<Group> groups) throws IOException {
        GroupApi api = getApi().getGroupApi();
        for (Group g : groups) {
            StatusResponse response = api.deleteGroup(g.getId());
            hqAssertSuccess(response);     
        }
    }
}
