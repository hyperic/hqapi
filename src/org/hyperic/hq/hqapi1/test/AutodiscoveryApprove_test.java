package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AutodiscoveryApi;
import org.hyperic.hq.hqapi1.types.ApproveResponse;

public class AutodiscoveryApprove_test extends AutodiscoveryTestBase {
    
    public AutodiscoveryApprove_test(String name) {
        super(name);
    }

    public void testApproveNoFQDN() throws Exception {

        AutodiscoveryApi api = getAutodiscoveryApi();

        ApproveResponse response = api.approve("");
        hqAssertFailureInvalidParameters(response);
    }
}
