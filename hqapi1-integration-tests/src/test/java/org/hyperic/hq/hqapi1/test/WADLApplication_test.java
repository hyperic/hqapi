package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.HttpLocalhost8080HquHqapi1;
import org.hyperic.hq.hqapi1.wadl.*;

import java.util.Random;

public class WADLApplication_test extends WADLTestBase {

    protected static final String APP_NAME = "Test Application";
    protected static final String APP_LOCATION = "SFO";
    protected static final String APP_DESC = "Test Application Description";
    protected static final String APP_ENG_CONTACT = "415-555-5555";
    protected static final String APP_BIZ_CONTACT = "212-555-5555";
    protected static final String APP_OPS_CONTACT = "510-555-5555";

    protected Application generateTestApplication()
        throws Exception {

        Random r = new Random();
        Application a = new Application();

        String name = APP_NAME + r.nextInt();
        a.setName(name);
        a.setLocation(APP_LOCATION);
        a.setDescription(APP_DESC);
        a.setEngContact(APP_ENG_CONTACT);
        a.setBizContact(APP_BIZ_CONTACT);
        a.setOpsContact(APP_OPS_CONTACT);

        return a;
    }

    public void testList() throws Exception {
        HttpLocalhost8080HquHqapi1.ApplicationListHqu list =
                new HttpLocalhost8080HquHqapi1.ApplicationListHqu();

        ApplicationsResponse response = list.getAsApplicationsResponse();
        hqAssertSuccess(response);
    }

    public void testCreate() throws Exception {
        HttpLocalhost8080HquHqapi1.ApplicationCreateHqu create =
                new HttpLocalhost8080HquHqapi1.ApplicationCreateHqu();

        Application a = generateTestApplication();
        ApplicationRequest req = new ApplicationRequest();
        req.setApplication(a);

        ApplicationResponse response =
                create.postApplicationXmlAsApplicationResponse(req);
        hqAssertSuccess(response);

        HttpLocalhost8080HquHqapi1.ApplicationDeleteHqu delete =
                new HttpLocalhost8080HquHqapi1.ApplicationDeleteHqu();
        StatusResponse deleteResponse =
                delete.getAsStatusResponse(response.getApplication().getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testUpdate() throws Exception {
        HttpLocalhost8080HquHqapi1.ApplicationUpdateHqu update =
                new HttpLocalhost8080HquHqapi1.ApplicationUpdateHqu();

        Application a = generateTestApplication();
        ApplicationRequest req = new ApplicationRequest();
        req.setApplication(a);

        ApplicationResponse resp = update.postApplicationXmlAsApplicationResponse(req);
        hqAssertFailure(resp);
    }

    public void testSync() throws Exception {
        HttpLocalhost8080HquHqapi1.ApplicationSyncHqu sync =
                new HttpLocalhost8080HquHqapi1.ApplicationSyncHqu();

        Application a = generateTestApplication();
        ApplicationsRequest req = new ApplicationsRequest();
        req.getApplication().add(a);

        ApplicationsResponse response = sync.postApplicationXmlAsApplicationsResponse(req);
        hqAssertSuccess(response);

        HttpLocalhost8080HquHqapi1.ApplicationDeleteHqu delete =
                new HttpLocalhost8080HquHqapi1.ApplicationDeleteHqu();
        for (Application app : response.getApplication()) {
            StatusResponse deleteResponse = delete.getAsStatusResponse(app.getId());
            hqAssertSuccess(deleteResponse);
        }
    }

    public void testDelete() throws Exception {
        HttpLocalhost8080HquHqapi1.ApplicationDeleteHqu delete =
                new HttpLocalhost8080HquHqapi1.ApplicationDeleteHqu();

        StatusResponse deleteResponse = delete.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(deleteResponse);
    }
}
