package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.types.ApplicationsResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ApplicationList_test extends ApplicationTestBase {

    public ApplicationList_test(String name) {
        super(name);
    }

    public void testList() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        ApplicationsResponse response = api.listApplications();
        hqAssertSuccess(response);
    }

    public void testListWithApplications() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        int num = 5;
        Map<Integer,Application> apps = new HashMap<Integer,Application>();
        for (int i = 0; i < num; i++) {
            Application a = createTestApplication(null);
            apps.put(a.getId(), a);
        }

        ApplicationsResponse response = api.listApplications();
        hqAssertSuccess(response);

        for (Application a : response.getApplication()) {
            apps.remove(a.getId());
        }

        assertTrue("Not all created applications were found during listing!",
                   apps.isEmpty());

        for (Application a : response.getApplication()) {
            api.deleteApplication(a.getId());
        }
    }
}
