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
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;

import java.util.List;

public class GroupControlExecute_test extends ControlTestBase {

    public GroupControlExecute_test(String name) {
        super(name);
    }

    public void testExecuteInvalidGroup() throws Exception {
        ControlApi api = getApi().getControlApi();

        Group g = new Group();
        g.setResourceId(Integer.MAX_VALUE);

        StatusResponse response = api.executeAction(g, "none", new String[] {});
        hqAssertFailureObjectNotFound(response);
    }

    public void testExecuteValidGroup() throws Exception {
        HQApi api = getApi();

        Group controlGroup = createControllableGroup(api);
        String[] arguments = new String[0];

        ControlApi cApi = api.getControlApi();
        StatusResponse executeResponse = cApi.executeAction(controlGroup,
                                                            "run", arguments);
        
        // TODO Update this when group control action execution is supported
        //hqAssertSuccess(executeResponse);
        hqAssertFailureNotSupported(executeResponse);

        cleanupControllableGroup(api, controlGroup);
    }

    public void testExecuteNoPermission() throws Exception {
        HQApi api = getApi();

        Group controlGroup = createControllableGroup(api);
        List<User> users = createTestUsers(1);
        User user = users.get(0);

        HQApi apiUnpriv = getApi(user.getName(), TESTUSER_PASSWORD);
        ControlApi cApiUnpriv = apiUnpriv.getControlApi();

        StatusResponse executeResponse = cApiUnpriv.executeAction(controlGroup,
                                                                  "run", new String[0]);
       
        // TODO Update this when group control action execution is supported
        //hqAssertFailurePermissionDenied(executeResponse);
        hqAssertFailureNotSupported(executeResponse);

        deleteTestUsers(users);
        cleanupControllableGroup(api, controlGroup);
    }
}
