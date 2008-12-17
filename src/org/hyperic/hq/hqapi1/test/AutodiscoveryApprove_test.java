package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AutodiscoveryApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;

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

        StatusResponse response = api.approve(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
}
