package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.types.ApplicationResponse;
import org.hyperic.hq.hqapi1.types.Application;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class ApplicationUpdate_test extends ApplicationTestBase {

    private static final String UPDATE_PREFIX = "UPDATED-";

    public ApplicationUpdate_test(String name) {
        super(name);
    }

    public void testUpdateNoServices() throws Exception {
        ApplicationApi api = getApi().getApplicationApi();

        Application a = createTestApplication(null, null);

        a.setName(UPDATE_PREFIX + a.getName());
        a.setDescription(UPDATE_PREFIX + a.getDescription());
        a.setLocation(UPDATE_PREFIX + a.getLocation());
        a.setOpsContact(UPDATE_PREFIX + a.getOpsContact());
        a.setBizContact(UPDATE_PREFIX + a.getBizContact());
        a.setEngContact(UPDATE_PREFIX + a.getEngContact());

        ApplicationResponse updateResponse = api.updateApplication(a);
        hqAssertSuccess(updateResponse);

        Application updatedApp = updateResponse.getApplication();

        assertEquals(a.getName(), updatedApp.getName());
        assertEquals(a.getDescription(), updatedApp.getDescription());
        assertEquals(a.getLocation(), updatedApp.getLocation());
        assertEquals(a.getOpsContact(), updatedApp.getOpsContact());
        assertEquals(a.getBizContact(), updatedApp.getBizContact());
        assertEquals(a.getEngContact(), updatedApp.getEngContact());

        StatusResponse deleteResponse = api.deleteApplication(updatedApp.getId());
        hqAssertSuccess(deleteResponse);
    }
}
