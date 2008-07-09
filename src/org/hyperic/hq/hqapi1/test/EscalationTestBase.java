package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EscalationApi;

public class EscalationTestBase extends HQApiTestBase {

    public EscalationTestBase(String name) {
        super(name);
    }

    protected EscalationApi getEscalationApi() {
        return getApi().getEscalationApi();
    }

}
