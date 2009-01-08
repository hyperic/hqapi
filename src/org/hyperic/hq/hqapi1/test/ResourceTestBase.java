package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourceConfig;
import org.hyperic.hq.hqapi1.types.ResourceProperty;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ResourceTestBase extends HQApiTestBase {

    public ResourceTestBase(String name) {
        super(name);
    }

    protected void validateResource(Resource r) {
        assertNotNull(r);
        assertNotNull(r.getId());
        assertNotNull(r.getName());

        assertNotNull("No resource prototype found for resource id " + r.getId(),
                      r.getResourcePrototype());
        assertTrue(r.getResourcePrototype().getId() > 0);
        assertTrue(r.getResourcePrototype().getName().length() > 0);

        for (ResourceConfig config : r.getResourceConfig()) {
            assertNotNull("Null key found for resoruce id + " + r.getId(),
                          config.getKey());
            assertNotNull("Null value found for key " + config.getKey() +
                          " on resource id " + r.getId(), config.getValue());
        }

        for (ResourceProperty p : r.getResourceProperty()) {
            assertNotNull("Null key found for resoruce id + " + r.getId(),
                          p.getKey());
            assertNotNull("Null value found for key " + p.getKey() +
                          " on resource id " + r.getId(), p.getValue());
        }

        for (Resource child : r.getResource()) {
            validateResource(child);
        }
    }

    protected Resource createTestHTTPService() throws Exception {
        
        Agent a = getRunningAgent();

        ResourceApi api = getApi().getResourceApi();

        // Find HTTP resource type
        ResourcePrototypeResponse protoResponse = api.getResourcePrototype("HTTP");
        hqAssertSuccess(protoResponse);
        ResourcePrototype pt = protoResponse.getResourcePrototype();

        // Find local platform
        ResourcesResponse resourcesResponse = api.getResources(a, false, false);
        hqAssertSuccess(resourcesResponse);
        assertTrue("Did not find a single platform for " + a.getAddress() + ":" +
                   a.getPort(), resourcesResponse.getResource().size() == 1);
        Resource platform = resourcesResponse.getResource().get(0);

        // Configure service
        Map<String,String> params = new HashMap<String,String>();
        params.put("hostname", "www.hyperic.com");
        params.put("port", "80");
        params.put("sotimeout", "10");
        params.put("path", "/");
        params.put("method", "GET");

        Random r = new Random();
        String name = "My HTTP Check " + r.nextInt();

        ResourceResponse resp = api.createService(pt, platform, name, params);
        hqAssertSuccess(resp);
        Resource createdResource = resp.getResource();
        assertEquals(createdResource.getName(), name);

        // TODO: Fix me
        try {
            // HQ does not like it when resources are modified so quickly
            // after being created.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Ignore
        }

        return createdResource;
    }
}
