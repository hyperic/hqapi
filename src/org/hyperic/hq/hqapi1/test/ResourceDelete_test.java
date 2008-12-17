package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class ResourceDelete_test extends ResourceTestBase {

    public ResourceDelete_test(String name) {
        super(name);
    }

    public void testDeleteNonExistantService() throws Exception {

        ResourceApi api = getApi().getResourceApi();
        StatusResponse deleteResponse =
                api.deleteResource(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(deleteResponse);
    }
}
