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
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.PingAgentResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;

public class AgentPing_test extends UserTestBase {

    public AgentPing_test(String name) {
        super(name);
    }

    public void testPingInvalidAgent() throws Exception {

        AgentApi api = getApi().getAgentApi();

        Agent a = new Agent();
        a.setId(Integer.MAX_VALUE);
        PingAgentResponse response = api.pingAgent(a);
        hqAssertFailureObjectNotFound(response);
    }
    
    public void testPingValidAgent() throws Exception {
        
        AgentApi api = getApi().getAgentApi();

        Agent a = getRunningAgent();
        
        PingAgentResponse response = api.pingAgent(a);
        hqAssertSuccess(response);
    }
    
    public void testPingValidAgentUserNoPermission() throws Exception {
        
        UserApi userApi = getUserApi();

        User user = generateTestUser();

        UserResponse createResponse = userApi.createUser(user, TESTUSER_PASSWORD);
        hqAssertSuccess(createResponse);
        
        // reconnect as the new user
        AgentApi agentApi = getApi(user.getName(), TESTUSER_PASSWORD).getAgentApi();

        Agent a = getRunningAgent();
        
        PingAgentResponse pingResponse = agentApi.pingAgent(a);
        hqAssertFailurePermissionDenied(pingResponse);
    }
}
