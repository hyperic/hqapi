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

import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ControlActionResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.ControlApi;
import org.hyperic.hq.hqapi1.HQApi;

import java.util.List;

public class ControlAction_test extends ControlTestBase {

    public ControlAction_test(String name) {
        super(name);
    }

    public void testControlActionInvalidResource() throws Exception {
        ControlApi api = getApi().getControlApi();

        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);

        ControlActionResponse response = api.getActions(r);
        hqAssertFailureObjectNotFound(response);
    }

    public void testControlActionNoControlPlugin() throws Exception {
        ControlApi api = getApi().getControlApi();

        Resource localPlatform = getLocalPlatformResource(false, false);

        ControlActionResponse response = api.getActions(localPlatform);
        hqAssertSuccess(response);

        assertTrue("Should have found 0 actions for " +
                   localPlatform.getName(), response.getAction().size() == 0);
    }

    public void testControlActionValidResource() throws Exception {
        HQApi api = getApi();
        ControlApi cApi = getApi().getControlApi();

        Resource controllableResource = createControllableResource(api);

        ControlActionResponse response = cApi.getActions(controllableResource);
        hqAssertSuccess(response);

        assertTrue("Should have found 1 action for " +
                   controllableResource.getName(), response.getAction().size() == 1);

        assertEquals("run", response.getAction().get(0));

        cleanupResource(api, controllableResource);   
    }

    public void testControlActionNoPermission() throws Exception {
        HQApi api = getApi();
        Resource controllableResource = createControllableResource(api);

        List<User> users = createTestUsers(1);
        User user = users.get(0);

        HQApi apiUnpriv = getApi(user.getName(), TESTUSER_PASSWORD);
        ControlApi cApi = apiUnpriv.getControlApi();

        ControlActionResponse response = cApi.getActions(controllableResource);
        hqAssertFailurePermissionDenied(response);

        deleteTestUsers(users);
        cleanupResource(api, controllableResource);
    }
}
