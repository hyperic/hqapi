package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AutodiscoveryApi;
import org.hyperic.hq.hqapi1.types.QueueResponse;

public class AutodiscoveryGetQueue_test extends AutodiscoveryTestBase {
    
    public AutodiscoveryGetQueue_test(String name) {
        super(name);
    }

    public void testGetQueue() throws Exception {

        AutodiscoveryApi api = getAutodiscoveryApi();

        QueueResponse response = api.getQueue();
        hqAssertSuccess(response);
        assertNotNull(response.getAIPlatform());
    }
}
