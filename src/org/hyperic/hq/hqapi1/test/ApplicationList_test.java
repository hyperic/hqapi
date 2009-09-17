package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.types.ApplicationsResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.Application;

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
        for (Application app : response.getApplication()) {
            Integer id =  app.getId();
            System.out.println("APP " + id + "  name: " + app.getName());
            System.out.println("       location: " + app.getLocation());
            System.out.println("     desription: " + app.getDescription());
            System.out.println("      Resources: " + app.getResource().size());
            System.out.println("         Groups: " + app.getGroup().size());
        }
        hqAssertSuccess(response);
    }
}
