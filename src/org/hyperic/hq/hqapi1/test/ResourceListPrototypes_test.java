package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.ListResourcePrototypesResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;

public class ResourceListPrototypes_test extends HQApiTestBase {

    public ResourceListPrototypes_test(String name) {
        super(name);
    }

    public void testListPrototypes() throws Exception {
        ResourceApi api = getApi().getResourceApi();
        
        ListResourcePrototypesResponse resp = api.listResourcePrototypes(); 
        
        hqAssertSuccess(resp);
        assertTrue(resp.getResourcePrototype().size() != 0);

        for (ResourcePrototype pt : resp.getResourcePrototype()) {
            assertTrue(pt.getId() > 0);
            assertTrue(pt.getName() != null && pt.getName().length() > 0);
        }
    }
}
