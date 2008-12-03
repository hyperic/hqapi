package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.ListResourcePrototypesResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.FindResourcesResponse;

public class ResourceListPrototypes_test extends HQApiTestBase {

    public ResourceListPrototypes_test(String name) {
        super(name);
    }

    public void testListAllPrototypes() throws Exception {
        ResourceApi api = getApi().getResourceApi();
        
        ListResourcePrototypesResponse resp = api.listAllResourcePrototypes(); 
        
        hqAssertSuccess(resp);
        assertTrue(resp.getResourcePrototype().size() != 0);

        for (ResourcePrototype pt : resp.getResourcePrototype()) {
            assertTrue(pt.getId() > 0);
            assertTrue(pt.getName() != null && pt.getName().length() > 0);
        }
    }

    public void testListPrototypes() throws Exception {
        ResourceApi api = getApi().getResourceApi();

        ListResourcePrototypesResponse allResponse = api.listAllResourcePrototypes();
        hqAssertSuccess(allResponse);

        ListResourcePrototypesResponse response = api.listResourcePrototypes();
        hqAssertSuccess(response);

        assertTrue("All prototypes not greater than existing prototypes " +
                   "all=" + allResponse.getResourcePrototype().size() +
                   " existing=" + response.getResourcePrototype().size(),
                   allResponse.getResourcePrototype().size() >
                   response.getResourcePrototype().size());

        assertTrue("Existing prototypes not > 1",
                   response.getResourcePrototype().size() > 1);
    }
}
