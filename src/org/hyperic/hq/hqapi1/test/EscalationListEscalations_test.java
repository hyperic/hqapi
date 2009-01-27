package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.types.EscalationsResponse;

public class EscalationListEscalations_test extends EscalationTestBase {

    public EscalationListEscalations_test(String name) {
        super(name);
    }

    public void testListEscalations() throws Exception {

        EscalationApi api = getEscalationApi();

        EscalationsResponse response = api.getEscalations();
        hqAssertSuccess(response);
        assertTrue("No escalations found", response.getEscalation().size() > 0);
    }
}
