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
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ResourceCreateServer_test extends ResourceTestBase {

    public ResourceCreateServer_test(String name) {
        super(name);
    }

    public void testCreateServer() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        ResourcePrototypeResponse protoResponse =
                api.getResourcePrototype("Apache httpd");
        hqAssertSuccess(protoResponse);

        Resource parent = getLocalPlatformResource(false, false);

        Random r = new Random();
        final String name = "Test Apache Server" + r.nextInt();

        Map<String,String> config = new HashMap<String,String>();
        config.put("hostname", "localhost");
        config.put("port", "80");
        config.put("path", "/server-status");
        config.put("sotimeout", "10");

        ResourceResponse resp =
                api.createServer(protoResponse.getResourcePrototype(),
                                 parent, name, config);
        hqAssertSuccess(resp);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Ignore
        }

        // Cleanup
        StatusResponse deleteResponse = api.deleteResource(resp.getResource().getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testCreateServerInvalidAppdefType() throws Exception {
        ResourceApi api = getApi().getResourceApi();

        ResourcePrototypeResponse protoResponse =
                api.getResourcePrototype("Apache httpd");
        hqAssertSuccess(protoResponse);

        ResourcePrototypeResponse cpuProtoResponse =
                api.getResourcePrototype("CPU");
        hqAssertSuccess(cpuProtoResponse);

        ResourcesResponse cpusResponse = api.getResources(cpuProtoResponse.getResourcePrototype(),
                                                          false, false);
        hqAssertSuccess(cpusResponse);
        assertTrue("No CPUs found", cpusResponse.getResource().size() > 0);

        Resource parent = cpusResponse.getResource().get(0);

        Random r = new Random();
        final String name = "Test Apache Server" + r.nextInt();

        Map<String,String> config = new HashMap<String,String>();
        config.put("hostname", "localhost");
        config.put("port", "80");
        config.put("path", "/server-status");
        config.put("sotimeout", "10");

        ResourceResponse resp =
                api.createServer(protoResponse.getResourcePrototype(),
                                 parent, name, config);
        hqAssertFailureInvalidParameters(resp);
    }

    public void testCreateServerInvalidPrototype() throws Exception {
        ResourceApi api = getApi().getResourceApi();

        ResourcePrototypeResponse protoResponse =
                api.getResourcePrototype("Cisco IOS Server");
        hqAssertSuccess(protoResponse);

        Resource parent = getLocalPlatformResource(false, false);

        Random r = new Random();
        final String name = "Test Cisco IOS Server" + r.nextInt();

        Map<String,String> config = new HashMap<String,String>();

        ResourceResponse resp =
                api.createServer(protoResponse.getResourcePrototype(),
                                 parent, name, config);
        hqAssertFailureInvalidParameters(resp);
    }

    public void testCreateServerLongDescription() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        ResourcePrototypeResponse protoResponse =
                api.getResourcePrototype("Apache httpd");
        hqAssertSuccess(protoResponse);

        Resource parent = getLocalPlatformResource(false, false);

        String longDescription = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        Random r = new Random();
        final String name = "Test Apache Server" + r.nextInt();

        Map<String,String> config = new HashMap<String,String>();
        config.put("hostname", "localhost");
        config.put("port", "80");
        config.put("path", "/server-status");
        config.put("sotimeout", "10");
        config.put("description", longDescription);

        ResourceResponse resp =
                api.createServer(protoResponse.getResourcePrototype(),
                                 parent, name, config);
        hqAssertFailureInvalidParameters(resp);
    }
}
