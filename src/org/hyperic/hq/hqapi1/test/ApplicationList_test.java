package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.types.ApplicationsResponse;

public class ApplicationList_test extends HQApiTestBase {

    public ApplicationList_test(String name) {
        super(name);
    }

    // TODO: Stub
    public void testList() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        ApplicationsResponse response = api.listApplications();
        hqAssertSuccess(response);  
    }
}
