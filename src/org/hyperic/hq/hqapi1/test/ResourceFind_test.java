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

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.User;

import java.util.List;

public class ResourceFind_test extends ResourceTestBase {

    public ResourceFind_test(String name) {
        super(name);
    }

    public void testFindByAgent() throws Exception {

        Agent a = getRunningAgent();
        
        ResourceApi api = getApi().getResourceApi();
        ResourcesResponse resp = api.getResources(a, false, false);
        hqAssertSuccess(resp);

        // This test assumes if you have a local agent that is pingable that
        // there will be at least one platform servicing it.
        assertTrue("Found 0 platform resources for agent " + a.getId(),
                   resp.getResource().size() > 0);
        for (Resource r : resp.getResource()) {
            validateResource(r);
        }
    }

    public void testFindByInvalidAgent() throws Exception {

        Agent a = new Agent();
        a.setId(Integer.MAX_VALUE);
        a.setAddress("1.2.3.4");
        a.setPort(80);
        a.setVersion("4.0");

        ResourceApi api = getApi().getResourceApi();
        ResourcesResponse resp = api.getResources(a, false, false);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testFindByPrototype() throws Exception {
        final String TYPE = "CPU";
        ResourceApi api = getApi().getResourceApi();

        ResourcePrototypeResponse protoResponse =
                api.getResourcePrototype(TYPE);
        hqAssertSuccess(protoResponse);

        ResourcePrototype pt = protoResponse.getResourcePrototype();

        ResourcesResponse resp = api.getResources(pt, false, false);
        hqAssertSuccess(resp);

        // We assume we're running against a server with valid resources
        if (resp.getResource().size() == 0) {
            getLog().warn("Warning, no resources of type " + TYPE + " found!");
        }

        for (Resource r : resp.getResource()) {
            validateResource(r);
        }
    }

    public void testFindByInvalidPrototype() throws Exception {
        final String INVALID_TYPE = "Non-existant type";

        ResourceApi api = getApi().getResourceApi();

        ResourcePrototypeResponse protoResponse =
            api.getResourcePrototype(INVALID_TYPE);
        hqAssertFailureObjectNotFound(protoResponse);
    }

    public void testFindByDescription() throws Exception {
        final String DESC = "Hyperic HQ monitor Agent";
        ResourceApi api = getApi().getResourceApi();

        ResourcesResponse response = api.getResources(DESC, false, false);
        hqAssertSuccess(response);

        assertTrue("Found no matches for '" + DESC + "'", response.getResource().size() > 0);
    }

    public void testFindByDescriptionPartialMatch() throws Exception {
        final String DESC = "HQ monitor";
        ResourceApi api = getApi().getResourceApi();

        ResourcesResponse response = api.getResources(DESC, false, false);
        hqAssertSuccess(response);

        assertTrue("Found no matches for '" + DESC + "'", response.getResource().size() > 0);
    }

    public void testFindByDescriptionNoMatches() throws Exception {
        final String DESC = "ASDFASDFASDF";
        ResourceApi api = getApi().getResourceApi();

        ResourcesResponse response = api.getResources(DESC, false, false);
        hqAssertSuccess(response);

        assertTrue("Found matches for '" + DESC + "'", response.getResource().size() == 0);
    }

    public void testFindResourceByAgentUnauthorized() throws Exception {
        List<User> users = createTestUsers(1);
        User user = users.get(0);
        ResourceApi api = getApi(user.getName(), TESTUSER_PASSWORD).getResourceApi();

        // Use admin user to get local agent..
        Agent agent = getRunningAgent();

        // Test find by agent
        ResourcesResponse response = api.getResources(agent, false, false);
        hqAssertSuccess(response);

        assertTrue("Found resources with unauthorized user", response.getResource().size() == 0);

        deleteTestUsers(users);
    }

    public void testFindResourceByPrototypeUnauthorized() throws Exception {
        List<User> users = createTestUsers(1);
        User user = users.get(0);
        ResourceApi api = getApi(user.getName(), TESTUSER_PASSWORD).getResourceApi();

        // Use admin user to get local platform..
        Resource localPlatform = getLocalPlatformResource(false, false);

        // Test find by prototype
        ResourcesResponse response = api.getResources(localPlatform.getResourcePrototype(), false, false);
        hqAssertSuccess(response);

        assertTrue("Found resources with unauthorized user", response.getResource().size() == 0);

        deleteTestUsers(users);
    }

    public void testFindResourceByDescriptionUnauthorized() throws Exception {
        final String DESC = "Hyperic HQ monitor Agent";
        List<User> users = createTestUsers(1);
        User user = users.get(0);
        ResourceApi api = getApi(user.getName(), TESTUSER_PASSWORD).getResourceApi();

        // Use admin user to get local platform..
        Resource localPlatform = getLocalPlatformResource(false, false);

        // Test find by prototype
        ResourcesResponse response = api.getResources(DESC, false, false);
        hqAssertSuccess(response);

        assertTrue("Found resources with unauthorized user", response.getResource().size() == 0);

        deleteTestUsers(users);
    }
}
