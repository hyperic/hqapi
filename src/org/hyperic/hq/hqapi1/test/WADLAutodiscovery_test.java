package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLAutodiscovery_test extends WADLTestBase {

    public void testGetQueue() throws Exception {

        Endpoint.AutodiscoveryGetQueueHqu adGetQueue =
                new Endpoint.AutodiscoveryGetQueueHqu();

        QueueResponse queue = adGetQueue.getAsQueueResponse();
        hqAssertSuccess(queue);
    }

    public void testApprove() throws Exception {

        Endpoint.AutodiscoveryApproveHqu adApprove =
                new Endpoint.AutodiscoveryApproveHqu();

        StatusResponse approve = adApprove.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(approve); // That id won't exist, just testing endpoints.
    }
}
