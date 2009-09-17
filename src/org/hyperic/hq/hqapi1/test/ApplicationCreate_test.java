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
        a.setName("A new app");
        a.setLocation("Doylestown");
        a.setDescription("A test app created using the API");
        a.setEngContact("the Engineer");
        a.setBizContact("the Businessman");
        a.setOpsContact("the Ops Man");

        ApplicationResponse response = api.createApplication(a);

        hqAssertSuccess(response);

        System.out.println("NEW APP: " + response.getApplication().getId());
    }
}
