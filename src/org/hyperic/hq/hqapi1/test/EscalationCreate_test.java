package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.EscalationActionBuilder;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.EscalationResponse;

public class EscalationCreate_test extends EscalationTestBase {

    public EscalationCreate_test(String name) {
        super(name);
    }

    public void testCreateValidEscalation() throws Exception {

        EscalationApi api = getEscalationApi();

        Escalation e = generateEscalation();
        e.getAction().add(EscalationActionBuilder.createNoOpAction(1000));
        EscalationResponse createResponse = api.createEscalation(e);
        hqAssertSuccess(createResponse);
    }

    public void testCreateEscalationNoActions() throws Exception {

        EscalationApi api = getEscalationApi();

        Escalation e = generateEscalation();
        EscalationResponse createResponse = api.createEscalation(e);
        hqAssertSuccess(createResponse);
    }

    public void testCreateEscalationEmptyName() throws Exception {

        EscalationApi api = getEscalationApi();

        Escalation e = generateEscalation();
        e.setName("");
        EscalationResponse createResponse = api.createEscalation(e);
        hqAssertFailureInvalidParameters(createResponse);
    }

    public void testCreateEscalationNullName() throws Exception {

        EscalationApi api = getEscalationApi();

        Escalation e = generateEscalation();
        e.setName(null);
        EscalationResponse createResponse = api.createEscalation(e);
        hqAssertFailureInvalidParameters(createResponse);
    }
}
