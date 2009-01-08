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

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            // Ignore
        }

        StatusResponse deleteResponse = api.deleteResource(resp.getResource().getId());
        hqAssertSuccess(deleteResponse);
    }
}
