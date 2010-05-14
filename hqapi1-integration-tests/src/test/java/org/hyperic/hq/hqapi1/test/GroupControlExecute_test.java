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
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;

import java.util.ArrayList;
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

    public void testExecuteCompatibleGroup() throws Exception {
        HQApi api = getApi();

        Group controlGroup = createControllableGroup(api, 5);
        String[] arguments = new String[0];

        ControlApi cApi = api.getControlApi();
        StatusResponse executeResponse = cApi.executeAction(controlGroup,
                                                            "run", arguments);
        
        hqAssertSuccess(executeResponse);

        cleanupControllableGroup(api, controlGroup);
    }

    public void testExecuteMixedGroup() throws Exception {
        HQApi api = getApi();

        Resource platform = getLocalPlatformResource(false, false);
        Resource service = createControllableResource(api);
        List<Resource> resources = new ArrayList<Resource>();
        resources.add(platform);
        resources.add(service);
        
        Group mixedGroup = createGroup(resources);
        String[] arguments = new String[0];

        ControlApi cApi = api.getControlApi();
        StatusResponse executeResponse = cApi.executeAction(mixedGroup,
                                                            "run", arguments);
        
        hqAssertFailureNotSupported(executeResponse);

        cleanupResource(api, service);
        cleanupGroup(mixedGroup);
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
       
        hqAssertFailurePermissionDenied(executeResponse);

        deleteTestUsers(users);
        cleanupControllableGroup(api, controlGroup);
    }
}
