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

import org.hyperic.hq.hqapi1.ControlApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.ControlHistory;
import org.hyperic.hq.hqapi1.types.ControlHistoryResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;

import java.util.List;

public class GroupControlHistory_test extends ControlTestBase {

    public GroupControlHistory_test(String name) {
        super(name);
    }

    public void testControlHistoryInvalidGroup() throws Exception {
        ControlApi api = getApi().getControlApi();

        Group g = new Group();
        g.setResourceId(Integer.MAX_VALUE);

        ControlHistoryResponse response = api.getHistory(g);
        hqAssertFailureObjectNotFound(response);
    }

    public void testControlHistoryValidGroupWithHistory() throws Exception {
        HQApi api = getApi();
        
        Group controlGroup = createControllableGroup(api, 5);

        ControlApi cApi = getApi().getControlApi();
        StatusResponse executeResponse = cApi.executeAction(controlGroup,
                                                            "run", new String[0]);

        hqAssertSuccess(executeResponse);

        // validate the control history for the group and it's members        
        ControlHistory groupHistory = findControlHistory(controlGroup, 60);
        
        for (Resource r : controlGroup.getResource()) {
            findControlHistory(r, 1);
        }
        
        // verify that the group control action is started without "significant" delay
        // TODO: ideally, this needs to be less than 1 second
        long timeToStart = groupHistory.getStartTime() - groupHistory.getDateScheduled();
        assertTrue(timeToStart < (40*SECOND));

        cleanupControllableGroup(api, controlGroup);
    }

    public void testControlHistoryValidGroupWithoutHistory() throws Exception {
        HQApi api = getApi();
        
        Group controlGroup = createControllableGroup(api);

        ControlApi cApi = getApi().getControlApi();
        ControlHistoryResponse response = cApi.getHistory(controlGroup);
        hqAssertSuccess(response);
        
        assertTrue("Should have no control history for group " + controlGroup.getName(), 
                    response.getControlHistory().isEmpty());

        cleanupControllableGroup(api, controlGroup);
    }
    
    public void testControlHistoryNoPermission() throws Exception {
        HQApi api = getApi();

        Group controlGroup = createControllableGroup(api);
        List<User> users = createTestUsers(1);
        User user = users.get(0);

        HQApi apiUnpriv = getApi(user.getName(), TESTUSER_PASSWORD);
        ControlApi cApiUnpriv = apiUnpriv.getControlApi();

        ControlHistoryResponse response = cApiUnpriv.getHistory(controlGroup);
        hqAssertFailurePermissionDenied(response);

        deleteTestUsers(users);
        cleanupControllableGroup(api, controlGroup);
    }
}
