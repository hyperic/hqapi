package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AutodiscoveryApi;
import org.hyperic.hq.hqapi1.HQApi;

public class AutodiscoveryTestBase extends HQApiTestBase {

    public AutodiscoveryTestBase(String name) {
        super(name);
    }

    public AutodiscoveryApi getAutodiscoveryApi() {
        HQApi api = getApi();
        return api.getAutodiscoveryApi();
    }
}
