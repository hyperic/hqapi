package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLAutodiscovery_test extends WADLTestBase {

    public void testGetQueue() throws Exception {

        HttpLocalhost8080HquHqapi1.AutodiscoveryGetQueueHqu adGetQueue =
                new HttpLocalhost8080HquHqapi1.AutodiscoveryGetQueueHqu();

        QueueResponse queue = adGetQueue.getAsQueueResponse();
        hqAssertSuccess(queue);
    }

    public void testApprove() throws Exception {

        HttpLocalhost8080HquHqapi1.AutodiscoveryApproveHqu adApprove =
                new HttpLocalhost8080HquHqapi1.AutodiscoveryApproveHqu();

        StatusResponse approve = adApprove.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(approve); // That id won't exist, just testing endpoints.
    }
}
