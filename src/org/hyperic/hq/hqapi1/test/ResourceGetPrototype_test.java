package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.GetResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;

public class ResourceGetPrototype_test extends HQApiTestBase {

    public ResourceGetPrototype_test(String name) {
        super(name);
    }

    public void testGetValidResourcePrototype() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        final String TYPE = "Linux";
        GetResourcePrototypeResponse response = api.getResourcePrototype(TYPE);
        hqAssertSuccess(response);

        ResourcePrototype type = response.getResourcePrototype();
        assertNotNull("Requested prototype " + TYPE + " was null", type);
        assertTrue("Requested prototype id " + type.getId() + " invalid",
                   type.getId() > 10000);
        assertEquals(TYPE, type.getName());
    }

    public void testGetInvalidResourcePrototype() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        final String TYPE = "Some unkonwn type";
        GetResourcePrototypeResponse response = api.getResourcePrototype(TYPE);
        hqAssertFailureObjectNotFound(response);
    }
}
