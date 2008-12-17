package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.EscalationsResponse;

public class EscalationListEscalations_test extends EscalationTestBase {

    public EscalationListEscalations_test(String name) {
        super(name);
    }

    public void testListEscalations() throws Exception {
        EscalationApi api = getEscalationApi();
        EscalationsResponse response = api.getEscalations();

        // Assert success response
        hqAssertSuccess(response);

        // Check each Escalation in the list has a Name and find test escalation
        boolean testEscFound = false;
        for (Escalation e : response.getEscalation()) {
            assertNotNull(e.getName());
            testEscFound |= e.getName().equals(TEST_NAME);
        }
        assertTrue(testEscFound);
    }
}
