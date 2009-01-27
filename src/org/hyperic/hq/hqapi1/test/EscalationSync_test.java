package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.EscalationResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.ArrayList;
import java.util.List;

public class EscalationSync_test extends EscalationTestBase {

    public EscalationSync_test(String name) {
        super(name);
    }

    public void testSyncCreateNoActions() throws Exception {

        EscalationApi api = getEscalationApi();

        Escalation e = generateEscalation();
        List<Escalation> escalations = new ArrayList<Escalation>();
        escalations.add(e);
        StatusResponse syncResponse = api.syncEscalations(escalations);
        hqAssertSuccess(syncResponse);

        for (Escalation esc : escalations) {
            EscalationResponse escResponse = api.getEscalation(esc.getName());
            hqAssertSuccess(escResponse);
            cleanup(escResponse.getEscalation());
        }
    }
}
