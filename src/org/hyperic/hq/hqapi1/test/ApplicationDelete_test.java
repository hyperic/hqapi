package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class ApplicationDelete_test extends HQApiTestBase {

    public ApplicationDelete_test(String name) {
        super(name);
    }

    // TODO: Stub
    public void testDelete() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        StatusResponse response = api.deleteApplication(Integer.MAX_VALUE);
        hqAssertSuccess(response);
    }
}
