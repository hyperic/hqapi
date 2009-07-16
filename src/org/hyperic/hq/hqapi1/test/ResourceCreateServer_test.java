package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

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
