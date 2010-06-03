/*
 *
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 *
 * Copyright (C) [2010], VMware, Inc.
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

import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class AgentTransferPlugin_test extends HQApiTestBase {

    public AgentTransferPlugin_test(String name) {
        super(name);
    }

    public void testInvalidAgent() throws Exception {

        AgentApi api = getApi().getAgentApi();

        Agent a = new Agent();
        a.setId(Integer.MAX_VALUE);
        StatusResponse response = api.transferPlugin(a, "system-plugin.jar");
        hqAssertFailureObjectNotFound(response);
    }

    public void testInvalidPlugin() throws Exception {

        AgentApi api = getApi().getAgentApi();

        Agent a = getRunningAgent();
        StatusResponse response = api.transferPlugin(a, "foo-plugin.jar");
        hqAssertFailureInvalidParameters(response);
    }

    // Cannot really test a valid plugin as the agent restart would cause
    // other tests to fail.. maybe a transfer/sleep/ping?
}
