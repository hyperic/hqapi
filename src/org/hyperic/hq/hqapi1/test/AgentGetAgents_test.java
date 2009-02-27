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

import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.AgentsResponse;

import java.util.List;

public class AgentGetAgents_test extends HQApiTestBase {

    public AgentGetAgents_test(String name) {
        super(name);
    }

    public void testGetAllAgents() throws Exception {

        AgentApi api = getApi().getAgentApi();

        AgentsResponse response = api.getAgents();
        hqAssertSuccess(response);

        List<Agent> agents = response.getAgent();
        if (agents.size() == 0) {
            getLog().warn("Agent listing returned 0 results.  Test results " +
                          "may not be accurate.");
        }

        for (Agent a : agents) {
            assertTrue(a.getAddress().length() > 0);
            assertTrue(a.getPort() > 0);
            assertTrue(a.getVersion().length() > 0);
            assertTrue(a.getId() > 0);
        }
    }
}
