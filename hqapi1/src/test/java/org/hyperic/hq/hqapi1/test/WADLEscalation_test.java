package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLEscalation_test extends WADLTestBase {

    public void testEscalationGet() throws Exception {
        Endpoint.EscalationGetHqu escGet = new Endpoint.EscalationGetHqu();

        EscalationResponse response = escGet.getAsEscalationResponse("Some Esc");
        hqAssertFailure(response); // Won't exist
    }

    public void testEscalationList() throws Exception {
        Endpoint.EscalationListHqu escList = new Endpoint.EscalationListHqu();

        EscalationsResponse response = escList.getAsEscalationsResponse();
        hqAssertSuccess(response);
    }

    public void testEscalationDelete() throws Exception {
        Endpoint.EscalationDeleteHqu escDelete =
                new Endpoint.EscalationDeleteHqu();

        StatusResponse response = escDelete.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(response); // Won't exist
    }

    public void testEscalationSync() throws Exception {
        Endpoint.EscalationSyncHqu escSync =
                new Endpoint.EscalationSyncHqu();

        EscalationsRequest request = new EscalationsRequest();

        StatusResponse response = escSync.postAsStatusResponse(request);
        hqAssertSuccess(response);
    }
}
