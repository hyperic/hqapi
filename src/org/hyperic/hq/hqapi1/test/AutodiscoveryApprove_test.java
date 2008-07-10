package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AutodiscoveryApi;
import org.hyperic.hq.hqapi1.types.ApproveResponse;
import org.hyperic.hq.hqapi1.types.GetQueueResponse;
import org.hyperic.hq.hqapi1.types.AIPlatform;

/**
 * XXX: Tests for autoinventory approve are somewhat limited since the
 * approval process requires prior database state.
 */
public class AutodiscoveryApprove_test extends AutodiscoveryTestBase {
    
    public AutodiscoveryApprove_test(String name) {
        super(name);
    }

    public void testApproveNonExistantPlatform() throws Exception {

        AutodiscoveryApi api = getAutodiscoveryApi();

        ApproveResponse response = api.approve(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
}
