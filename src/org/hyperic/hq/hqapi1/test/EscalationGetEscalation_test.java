package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.EscalationActionBuilder;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.EscalationResponse;

public class EscalationGetEscalation_test extends EscalationTestBase {

    public EscalationGetEscalation_test(String name) {
        super(name);
    }

    public void testGetEscalationById() throws Exception {
        
        EscalationApi escApi = getEscalationApi();

        Escalation e = generateEscalation();
        e.getAction().add(EscalationActionBuilder.createNoOpAction(1000));

        EscalationResponse response = escApi.createEscalation(e);
        hqAssertSuccess(response);

        // Look up escalation by id
        response  = escApi.getEscalation(response.getEscalation().getId());
        hqAssertSuccess(response);

        // Cleanup
        cleanup(response.getEscalation());
    }

    public void testGetEscalationByName() throws Exception {

        EscalationApi escApi = getEscalationApi();

        Escalation e = generateEscalation();
        e.getAction().add(EscalationActionBuilder.createNoOpAction(1000));

        EscalationResponse response = escApi.createEscalation(e);
        hqAssertSuccess(response);

        // Look up escalation by id
        response  = escApi.getEscalation(response.getEscalation().getName());
        hqAssertSuccess(response);

        // Cleanup
        cleanup(response.getEscalation());
    }
    
}
