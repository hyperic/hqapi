package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.EscalationActionBuilder;
import org.hyperic.hq.hqapi1.EscalationActionBuilder.EscalationActionType;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.EscalationResponse;
import org.hyperic.hq.hqapi1.types.Notify;

import java.util.ArrayList;

public class EscalationUpdateEscalation_test extends EscalationTestBase {

    public EscalationUpdateEscalation_test(String name) {
        super(name);
    }

    public void testUpdateEscalation() throws Exception {

        EscalationApi api = getEscalationApi();

        Escalation e = generateEscalation();
        e.getAction().add(EscalationActionBuilder.createNoOpAction(1000));
        EscalationResponse createResponse = api.createEscalation(e);
        hqAssertSuccess(createResponse);

        e = createResponse.getEscalation();

        final String U_NAME = e.getName() + "Updated";
        final String U_DESC = e.getDescription() + "Updated";
        final long   U_PAUSE = e.getMaxPauseTime() * 2;
        final boolean U_NOTIFY = !e.isNotifyAll();
        final boolean U_PAUSE_ALLOWED = !e.isNotifyAll();
        final boolean U_REPEAT = !e.isRepeat();

        // Update everything
        e.setName(U_NAME);
        e.setDescription(U_DESC);
        e.setMaxPauseTime(U_PAUSE);
        e.setNotifyAll(U_NOTIFY);
        e.setPauseAllowed(U_PAUSE_ALLOWED);
        e.setRepeat(U_REPEAT);
        e.getAction().clear();
        e.getAction().add(EscalationActionBuilder.createEmailAction(1000, false, new ArrayList<Notify>()));

        EscalationResponse updateResponse = api.updateEscalation(e);
        hqAssertSuccess(updateResponse);

        Escalation updated = updateResponse.getEscalation();
        assertEquals(updated.getName(), U_NAME);
        assertEquals(updated.getDescription(), U_DESC);
        assertEquals(updated.getMaxPauseTime(), U_PAUSE);
        assertEquals(updated.isNotifyAll(), U_NOTIFY);
        assertEquals(updated.isPauseAllowed(), U_PAUSE_ALLOWED);
        assertEquals(updated.isRepeat(), U_REPEAT);
        assertEquals(updated.getAction().size(), 1);
        assertEquals(updated.getAction().get(0).getActionType(),
                     EscalationActionType.EMAIL.getType());

        cleanup(updated);  
    }
}
