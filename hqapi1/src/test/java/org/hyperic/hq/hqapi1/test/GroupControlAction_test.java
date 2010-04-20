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

import org.hyperic.hq.hqapi1.ControlApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.ControlActionResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.User;

import java.util.List;

public class GroupControlAction_test extends ControlTestBase {

    public GroupControlAction_test(String name) {
        super(name);
    }

    public void testControlActionInvalidGroup() throws Exception {
        ControlApi api = getApi().getControlApi();

        Group g = new Group();
        g.setResourceId(Integer.MAX_VALUE);

        ControlActionResponse response = api.getActions(g);
        hqAssertFailureObjectNotFound(response);
    }
    
    public void testControlActionValidGroup() throws Exception {
        HQApi api = getApi();
        
        Group controlGroup = createControllableGroup(api);
        
        ControlApi cApi = api.getControlApi();
        ControlActionResponse response = cApi.getActions(controlGroup);
        hqAssertSuccess(response);

        assertTrue("Should have found 1 control action for group " +
                    controlGroup.getName(), response.getAction().size() == 1);

        assertEquals("run", response.getAction().get(0));

        cleanupControllableGroup(api, controlGroup);
    }

    public void testControlActionNoPermission() throws Exception {
        HQApi api = getApi();
        
        Group controlGroup = createControllableGroup(api);
        List<User> users = createTestUsers(1);
        User user = users.get(0);

        HQApi apiUnpriv = getApi(user.getName(), TESTUSER_PASSWORD);
        ControlApi cApiUnpriv = apiUnpriv.getControlApi();

        ControlActionResponse response = cApiUnpriv.getActions(controlGroup);
        hqAssertFailurePermissionDenied(response);

        deleteTestUsers(users);
        cleanupControllableGroup(api, controlGroup);
    }
}
