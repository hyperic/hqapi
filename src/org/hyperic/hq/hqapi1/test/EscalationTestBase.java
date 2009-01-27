package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class EscalationTestBase extends HQApiTestBase {

    public EscalationTestBase(String name) {
        super(name);
    }

    protected EscalationApi getEscalationApi() {
        return getApi().getEscalationApi();
    }

    protected Escalation generateEscalation() {
        Random r = new Random();
        Escalation e = new Escalation();
        e.setName("Api Test Escalation" + r.nextInt());
        e.setDescription("Api Test Description");
        e.setMaxPauseTime(600000);
        e.setNotifyAll(true);
        e.setPauseAllowed(true);

        return e;
    }

    protected void cleanup(Escalation e) throws IOException {
        EscalationApi api = getEscalationApi();
        StatusResponse response = api.deleteEscalation(e.getId());
        hqAssertSuccess(response);
    }

    protected void cleanup(List<Escalation> escalations) throws Exception {
        EscalationApi api = getEscalationApi();

        for (Escalation e : escalations) {
            StatusResponse response = api.deleteEscalation(e.getId());
            hqAssertSuccess(response);
        }
    }
}
