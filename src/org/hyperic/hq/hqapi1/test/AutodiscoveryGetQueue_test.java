package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AutodiscoveryApi;
import org.hyperic.hq.hqapi1.types.GetQueueResponse;

public class AutodiscoveryGetQueue_test extends AutodiscoveryTestBase {
    
    public AutodiscoveryGetQueue_test(String name) {
        super(name);
    }

    public void testGetQueue() throws Exception {

        AutodiscoveryApi api = getAutodiscoveryApi();

        GetQueueResponse response = api.getQueue();
        hqAssertSuccess(response);
        assertNotNull(response.getAIPlatform());
    }
}
