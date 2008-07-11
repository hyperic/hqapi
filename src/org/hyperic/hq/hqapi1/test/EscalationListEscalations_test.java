package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.types.ListEscalationsResponse;
import org.hyperic.hq.hqapi1.types.Escalation;

public class EscalationListEscalations_test extends EscalationTestBase {

    public EscalationListEscalations_test(String name) {
        super(name);
    }

    public void testListEscalations() throws Exception {
        EscalationApi api = getEscalationApi();
        ListEscalationsResponse response = api.listEscalations();

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
