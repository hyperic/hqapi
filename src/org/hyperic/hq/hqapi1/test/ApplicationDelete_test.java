package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Application;

public class ApplicationDelete_test extends ApplicationTestBase {

    public ApplicationDelete_test(String name) {
        super(name);
    }

    public void testDeleteExistingApp() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();
        Application a = createTestApplication(null, null);

        StatusResponse response = api.deleteApplication(a.getId());
        hqAssertSuccess(response);
    }

    public void testDeleteNonExistingApp() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        StatusResponse response = api.deleteApplication(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
}
