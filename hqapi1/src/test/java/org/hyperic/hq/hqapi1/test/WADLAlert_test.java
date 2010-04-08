package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLAlert_test extends WADLTestBase {

    public void testFind() throws Exception {
        Endpoint.AlertFindHqu find = new Endpoint.AlertFindHqu();

        AlertsResponse response = find.getAsAlertsResponse(0l, System.currentTimeMillis(),
                                                           10, 1);
        hqAssertSuccess(response);
    }

    public void testFindByResource() throws Exception {
        Endpoint.AlertFindByResourceHqu find = new Endpoint.AlertFindByResourceHqu();

        AlertsResponse response = find.getAsAlertsResponse(Integer.MAX_VALUE,
                                                           0l, System.currentTimeMillis(),
                                                           10);
        hqAssertFailure(response); // Resource will not be found
    }

    public void testAck() throws Exception {
        Endpoint.AlertAckHqu ack = new Endpoint.AlertAckHqu();

        AlertsResponse response = ack.getAsAlertsResponse(Integer.MAX_VALUE, "Test ack");
        hqAssertFailure(response); // Alert will not exist
    }

    public void testFix() throws Exception {
        Endpoint.AlertFixHqu fix = new Endpoint.AlertFixHqu();

        AlertsResponse response = fix.getAsAlertsResponse(Integer.MAX_VALUE);
        hqAssertFailure(response); // Alert will not exist
    }

    public void testDelete() throws Exception {
        Endpoint.AlertDeleteHqu delete = new Endpoint.AlertDeleteHqu();

        StatusResponse response = delete.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(response);
    }
}
