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
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;

import java.util.List;

public class ControlExecute_test extends ControlTestBase {

    public ControlExecute_test(String name) {
        super(name);
    }

    public void testExecuteInvalidResource() throws Exception {
        ControlApi api = getApi().getControlApi();

        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);

        StatusResponse response = api.executeAction(r, "none", new String[] {});
        hqAssertFailureObjectNotFound(response);
    }

    public void testExecuteValidResource() throws Exception {
        HQApi api = getApi();
        ControlApi cApi = getApi().getControlApi();

        Resource controllableResource = createControllableResource(api);

        String[] arguments = new String[0];
        StatusResponse executeResponse = cApi.executeAction(controllableResource,
                                                            "run", arguments);
        hqAssertSuccess(executeResponse);

        cleanupResource(api, controllableResource);
    }

    public void testExecuteNoPermission() throws Exception {
        HQApi api = getApi();
        Resource controllableResource = createControllableResource(api);

        List<User> users = createTestUsers(1);
        User user = users.get(0);

        HQApi apiUnpriv = getApi(user.getName(), TESTUSER_PASSWORD);
        ControlApi cApiUnpriv = apiUnpriv.getControlApi();

        StatusResponse executeResponse = cApiUnpriv.executeAction(controllableResource,
                                                            "run", new String[0]);
        hqAssertFailurePermissionDenied(executeResponse);

        deleteTestUsers(users);
        cleanupResource(api, controllableResource);
    }
}
