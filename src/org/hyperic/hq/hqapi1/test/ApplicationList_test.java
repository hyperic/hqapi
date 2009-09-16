package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.types.ApplicationsResponse;
import org.hyperic.hq.hqapi1.types.Resource;

public class ApplicationList_test extends HQApiTestBase {

    public ApplicationList_test(String name) {
        super(name);
    }

    // TODO: Stub
    public void testList() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        ApplicationsResponse response = api.listApplications();
        // TODO: remove debugging lines
        System.out.println("TEST " + response);
        System.out.println("TEST " + response.getApplication().size());
        if (response.getApplication().size() > 0) {
            System.out.println("TEST " + response.getApplication().get(0).getLocation());
            System.out.println("TEST " + response.getApplication().get(0).getDescription());
            System.out.println("Resources: " + response.getApplication().get(0).getResource().size());
            System.out.println("Groups: " + response.getApplication().get(0).getGroup().size());
        }
        hqAssertSuccess(response);
    }
}
