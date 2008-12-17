package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;

public class ResourceGetPrototype_test extends HQApiTestBase {

    public ResourceGetPrototype_test(String name) {
        super(name);
    }

    public void testGetValidResourcePrototype() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        final String TYPE = "Linux";
        ResourcePrototypeResponse response = api.getResourcePrototype(TYPE);
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
        ResourcePrototypeResponse response = api.getResourcePrototype(TYPE);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetNullPrototype() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        ResourcePrototypeResponse response = api.getResourcePrototype(null);
        hqAssertFailureInvalidParameters(response);
    }
}
