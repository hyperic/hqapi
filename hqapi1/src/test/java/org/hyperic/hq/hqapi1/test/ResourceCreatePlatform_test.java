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
import org.hyperic.hq.hqapi1.types.Ip;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ResourceCreatePlatform_test extends ResourceTestBase {

    public ResourceCreatePlatform_test(String name) {
        super(name);
    }

    public void testCreatePlatform() throws Exception {

        Agent a = getRunningAgent();
        ResourceApi api = getApi().getResourceApi();

        ResourcePrototypeResponse protoResponse =
                api.getResourcePrototype("Network Device");

        Random r = new Random();
        final String name = "Test Network Device" + r.nextInt();
        final String fqdn = "apitest.hyperic.com";

        Ip ip = new Ip();
        ip.setAddress("10.0.0.1");

        List<Ip> ips = new ArrayList<Ip>();
        ips.add(ip);

        Map<String,String> config = new HashMap<String,String>();
        config.put("interface.index", "ifDescr");
        config.put("snmpIp", "10.0.0.1");
        config.put("snmpPort", "161");
        config.put("snmpCommunity", "public");
        config.put("snmpVersion", "v2c");

        ResourceResponse resp =
                api.createPlatform(a, protoResponse.getResourcePrototype(),
                                   name, fqdn, ips, config);
        hqAssertSuccess(resp);

        pauseTest();

        StatusResponse deleteResponse = api.deleteResource(resp.getResource().getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testCreatePlatformLongDescription() throws Exception {
        // give hibernate time to flush current session before running
        // this test which may cause a transaction failure
        pauseTest();
        
        Agent a = getRunningAgent();
        ResourceApi api = getApi().getResourceApi();

        ResourcePrototypeResponse protoResponse =
                api.getResourcePrototype("Network Device");

        Random r = new Random();
        final String name = "Test Network Device" + r.nextInt();
        final String fqdn = "apitest.hyperic.com";

        Ip ip = new Ip();
        ip.setAddress("10.0.0.1");

        List<Ip> ips = new ArrayList<Ip>();
        ips.add(ip);

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

        Map<String,String> config = new HashMap<String,String>();
        config.put("interface.index", "ifDescr");
        config.put("snmpIp", "10.0.0.1");
        config.put("snmpPort", "161");
        config.put("snmpCommunity", "public");
        config.put("snmpVersion", "v2c");
        config.put("description", longDescription);

        ResourceResponse resp =
                api.createPlatform(a, protoResponse.getResourcePrototype(),
                                   name, fqdn, ips, config);
        hqAssertFailureInvalidParameters(resp);
    }
}
