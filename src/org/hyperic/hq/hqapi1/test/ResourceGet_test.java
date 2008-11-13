package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.GetResourceResponse;

public class ResourceGet_test extends HQApiTestBase {

    public ResourceGet_test(String name) {
        super(name);
    }

    public void testGetInvalidResource() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        GetResourceResponse resp = api.getResource(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(resp);
    }
}