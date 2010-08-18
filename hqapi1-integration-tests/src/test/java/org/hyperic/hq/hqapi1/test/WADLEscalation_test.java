package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLEscalation_test extends WADLTestBase {

    public void testEscalationGet() throws Exception {
        HttpLocalhost8080HquHqapi1.EscalationGetHqu escGet = new HttpLocalhost8080HquHqapi1.EscalationGetHqu();

        EscalationResponse response = escGet.getAsEscalationResponse("Some Esc");
        hqAssertFailure(response); // Won't exist
    }

    public void testEscalationList() throws Exception {
        HttpLocalhost8080HquHqapi1.EscalationListHqu escList = new HttpLocalhost8080HquHqapi1.EscalationListHqu();

        EscalationsResponse response = escList.getAsEscalationsResponse();
        hqAssertSuccess(response);
    }

    public void testEscalationDelete() throws Exception {
        HttpLocalhost8080HquHqapi1.EscalationDeleteHqu escDelete =
                new HttpLocalhost8080HquHqapi1.EscalationDeleteHqu();

        StatusResponse response = escDelete.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(response); // Won't exist
    }

    public void testEscalationSync() throws Exception {
        HttpLocalhost8080HquHqapi1.EscalationSyncHqu escSync =
                new HttpLocalhost8080HquHqapi1.EscalationSyncHqu();

        EscalationsRequest request = new EscalationsRequest();

        StatusResponse response = escSync.postApplicationXmlAsStatusResponse(request);
        hqAssertSuccess(response);
    }
}
