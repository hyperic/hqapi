package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class ResourceCreateService_test extends ResourceTestBase {

    public ResourceCreateService_test(String name) {
        super(name);
    }

    public void testServiceCreate() throws Exception {

        ResourceApi api = getApi().getResourceApi();
        Resource createdResource = createTestHTTPService();

        // Clean up
        StatusResponse deleteResponse = api.deleteResource(createdResource.getId());
        hqAssertSuccess(deleteResponse);
    }
}
