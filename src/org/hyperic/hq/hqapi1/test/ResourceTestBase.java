package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourceConfig;

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

        for (Resource child : r.getResource()) {
            validateResource(child);
        }
    }    
}
