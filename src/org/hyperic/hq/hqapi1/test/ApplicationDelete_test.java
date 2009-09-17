package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Application;
import org.hyperic.hq.hqapi1.types.ApplicationResponse;

public class ApplicationDelete_test extends HQApiTestBase {

    public ApplicationDelete_test(String name) {
        super(name);
    }

    public void testDeleteExistingApp() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        Application a = new Application();
        a.setName("App to delete");
        a.setLocation("Hawaii");
        a.setDescription("A test app created using the API");
        a.setEngContact("the Engineer");
        a.setBizContact("the Businessman");
        a.setOpsContact("the Ops Man");
        ApplicationResponse appResponse = api.createApplication(a);

        StatusResponse response = api.deleteApplication(appResponse.getApplication().getId());
        hqAssertSuccess(response);

    }

    public void testDeleteNonExistingApp() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        StatusResponse response = api.deleteApplication(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);

    }
}
