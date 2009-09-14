package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.types.Application;
import org.hyperic.hq.hqapi1.types.ApplicationResponse;

public class ApplicationCreate_test extends HQApiTestBase {

    public ApplicationCreate_test(String name) {
        super(name);
    }

    // TODO: Stub
    public void testApplicationCreate() throws Exception {

        ApplicationApi api = getApi().getApplicationApi();

        Application a = new Application();

        ApplicationResponse response = api.createApplication(a);
        hqAssertSuccess(response);
    }
}
