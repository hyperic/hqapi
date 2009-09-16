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
        System.out.println("TEST - Apps: " + response.getApplication().size());
        if (response.getApplication().size() > 0) {
            System.out.println("APP 1: " + response.getApplication().get(0).getId());
            System.out.println("APP 1: " + response.getApplication().get(0).getLocation());
            System.out.println("APP 1: " + response.getApplication().get(0).getDescription());
            System.out.println("APP 1 Resources: " + response.getApplication().get(0).getResource().size());
            System.out.println("APP 1 Groups: " + response.getApplication().get(0).getGroup().size());
        }
        hqAssertSuccess(response);
    }
}
