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
        a.setName("A new app");
        a.setLocation("Tahiti");
        a.setDescription("A test app created using the API");
        a.setEngContact("the Engineer");
        a.setBizContact("the Businessman");
        a.setOpsContact("the Ops Man");

        ApplicationResponse newResponse = api.createApplication(a);

        Application a2 = newResponse.getApplication();
        a2.setBizContact("new biz contact");

        ApplicationResponse response = api.updateApplication(a2);
        hqAssertSuccess(response);
        assertEquals("new biz contact", newResponse.getApplication().getBizContact());

        api.deleteApplication(newResponse.getApplication().getId());
    }
}
