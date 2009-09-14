package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.types.ApplicationResponse;
import org.hyperic.hq.hqapi1.types.Application;

public class ApplicationUpdate_test extends HQApiTestBase {

    public ApplicationUpdate_test(String name) {
        super(name);
    }

    // TODO: Stub
    public void testUpdate() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        Application a = new Application();
  
        ApplicationResponse response = api.updateApplication(a);
        hqAssertSuccess(response);        
    }
}
