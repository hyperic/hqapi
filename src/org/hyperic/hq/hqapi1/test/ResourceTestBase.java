package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Resource;

public class ResourceTestBase extends HQApiTestBase {

    public ResourceTestBase(String name) {
        super(name);
    }

    protected void validateResource(Resource r) {
        assertNotNull(r);
        assertNotNull(r.getId());
        assertNotNull(r.getName());

        assertTrue("No configuration properties found for resource id " +
                  r.getId(), r.getResourceConfig().size() > 0);

        assertNotNull("No resource prototype found for resource id " + r.getId(),
                      r.getResourcePrototype());
        assertTrue(r.getResourcePrototype().getId() > 0);
        assertTrue(r.getResourcePrototype().getName().length() > 0);
    }    
}
