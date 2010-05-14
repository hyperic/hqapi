package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class EscalationDelete_test extends EscalationTestBase {

    public EscalationDelete_test(String name) {
        super(name);
    }

    public void testDeleteNonExistantEscalation() throws Exception {

        EscalationApi api = getEscalationApi();

        StatusResponse response = api.deleteEscalation(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
}
