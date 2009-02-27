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
import org.hyperic.hq.hqapi1.types.AgentResponse;

public class AgentGet_test extends HQApiTestBase {

    public AgentGet_test(String name) {
        super(name);
    }

    private void validateAgent(Agent a) {
        assertTrue(a.getAddress().length() > 0);
        assertTrue(a.getPort() > 0);
        assertTrue(a.getVersion().length() > 0);
        assertTrue(a.getId() > 0);
    }

    public void testGetAgentInvalid() throws Exception {

        AgentApi api = getApi().getAgentApi();

        AgentResponse response = api.getAgent("invalid.hyperic.com", 2144);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetAgentValid() throws Exception {

        Agent agent = getRunningAgent();

        AgentApi api = getApi().getAgentApi();

        AgentResponse response = api.getAgent(agent.getAddress(),
                                              agent.getPort());
        hqAssertSuccess(response);

        Agent a = response.getAgent();
        validateAgent(a);
    }

    public void testGetAgentById() throws Exception {

        Agent agent = getRunningAgent();

        AgentApi api = getApi().getAgentApi();

        AgentResponse response = api.getAgent(agent.getId());
        hqAssertSuccess(response);

        Agent a = response.getAgent();
        validateAgent(a);
    }

    public void testGetAgentByInvalidId() throws Exception {

        AgentApi api = getApi().getAgentApi();

        AgentResponse response = api.getAgent(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);        
    }
}
