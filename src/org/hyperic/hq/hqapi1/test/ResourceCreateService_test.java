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
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.Agent;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class ResourceCreateService_test extends ResourceTestBase {

    public ResourceCreateService_test(String name) {
        super(name);
    }

    public void testServiceCreate() throws Exception {

        ResourceApi api = getApi().getResourceApi();
        Resource createdResource = createTestHTTPService();

        // Clean up
        StatusResponse deleteResponse = api.deleteResource(createdResource.getId());
        hqAssertSuccess(deleteResponse);
    }

    // Attempt to create a resource on an invalid appdef type. (e.g. service on service)
    public void testCreateServiceInvalidAppdefType() throws Exception {
        ResourceApi api = getApi().getResourceApi();

        // Find HTTP resource type
        ResourcePrototypeResponse protoResponse = api.getResourcePrototype("HTTP");
        hqAssertSuccess(protoResponse);
        ResourcePrototype pt = protoResponse.getResourcePrototype();

        // Find CPU resource type
        ResourcePrototypeResponse cpuProtoResponse = api.getResourcePrototype("CPU");
        hqAssertSuccess(cpuProtoResponse);
        ResourcePrototype cpuPt = protoResponse.getResourcePrototype();

        ResourcesResponse cpusResponse = api.getResources(cpuPt, false, false);
        hqAssertSuccess(cpusResponse);
        assertTrue("0 CPUs found", cpusResponse.getResource().size() != 0);
        Resource cpu = cpusResponse.getResource().get(0);

        // Configure service
        Map<String,String> params = new HashMap<String,String>();
        params.put("hostname", "www.hyperic.com");
        params.put("port", "80");
        params.put("sotimeout", "10");
        params.put("path", "/");
        params.put("method", "GET");

        Random r = new Random();
        String name = "My HTTP Check " + r.nextInt();

        ResourceResponse resp = api.createService(pt, cpu, name, params);
        hqAssertFailureInvalidParameters(resp);
    }

    // Attempt to create a resource on an invalid prototype
    public void testServiceCreateInvalidPrototype() throws Exception {
        ResourceApi api = getApi().getResourceApi();

        // Find HTTP resource type
        ResourcePrototypeResponse protoResponse = api.getResourcePrototype("HTTP");
        hqAssertSuccess(protoResponse);
        ResourcePrototype pt = protoResponse.getResourcePrototype();

        // Find CPU resource type
        ResourcePrototypeResponse hqAgentProtoResponse = api.getResourcePrototype("HQ Agent");
        hqAssertSuccess(hqAgentProtoResponse);
        ResourcePrototype hqAgentPt = protoResponse.getResourcePrototype();

        ResourcesResponse agentsResponse = api.getResources(hqAgentPt, false, false);
        hqAssertSuccess(agentsResponse);
        assertTrue("0 Agents found", agentsResponse.getResource().size() != 0);
        Resource agent = agentsResponse.getResource().get(0);

        // Configure service
        Map<String,String> params = new HashMap<String,String>();
        params.put("hostname", "www.hyperic.com");
        params.put("port", "80");
        params.put("sotimeout", "10");
        params.put("path", "/");
        params.put("method", "GET");

        Random r = new Random();
        String name = "My HTTP Check " + r.nextInt();

        ResourceResponse resp = api.createService(pt, agent, name, params);
        hqAssertFailureInvalidParameters(resp);
    }
}
