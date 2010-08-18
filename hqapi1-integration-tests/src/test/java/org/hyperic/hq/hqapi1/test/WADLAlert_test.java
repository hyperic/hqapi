package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLAlert_test extends WADLTestBase {

    public void testFind() throws Exception {
        HttpLocalhost8080HquHqapi1.AlertFindHqu find = new HttpLocalhost8080HquHqapi1.AlertFindHqu();

        AlertsResponse response = find.getAsAlertsResponse(0l, System.currentTimeMillis(),
                                                           10, 1);
        hqAssertSuccess(response);
    }

    public void testFindByResource() throws Exception {
        HttpLocalhost8080HquHqapi1.AlertFindByResourceHqu find = new HttpLocalhost8080HquHqapi1.AlertFindByResourceHqu();

        AlertsResponse response = find.getAsAlertsResponse(Integer.MAX_VALUE,
                                                           0l, System.currentTimeMillis(),
                                                           10);
        hqAssertFailure(response); // Resource will not be found
    }

    public void testAck() throws Exception {
        HttpLocalhost8080HquHqapi1.AlertAckHqu ack = new HttpLocalhost8080HquHqapi1.AlertAckHqu();

        AlertsResponse response = ack.getAsAlertsResponse(Integer.MAX_VALUE, "Test ack");
        hqAssertFailure(response); // Alert will not exist
    }

    public void testFix() throws Exception {
        HttpLocalhost8080HquHqapi1.AlertFixHqu fix = new HttpLocalhost8080HquHqapi1.AlertFixHqu();

        AlertsResponse response = fix.getAsAlertsResponse(Integer.MAX_VALUE);
        hqAssertFailure(response); // Alert will not exist
    }

    public void testDelete() throws Exception {
        HttpLocalhost8080HquHqapi1.AlertDeleteHqu delete = new HttpLocalhost8080HquHqapi1.AlertDeleteHqu();

        StatusResponse response = delete.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(response);
    }
}
