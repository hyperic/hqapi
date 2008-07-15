package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.types.EmailAction;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.GetEscalationResponse;
import org.hyperic.hq.hqapi1.types.SuppressAction;

public class EscalationUpdateEscalation_test extends EscalationTestBase {

    public EscalationUpdateEscalation_test(String name) {
        super(name);
    }

    public void testUpdateEscalation() throws Exception {
        final int maxWait = 1000;
        final String className = getClass().getName();
        
        EscalationApi escApi = getEscalationApi();
        Escalation esc = getTestEscalation();
        
        // Update the escalation
        esc.setDescription(className);
        esc.setMaxPauseTime(maxWait);
        esc.setNotifyAll(true);
        esc.setPauseAllowed(true);
        esc.setRepeat(true);
        
        // Add some actions
        esc.getAction().add(escApi.createEmailAction());
        esc.getAction().add(escApi.createSuppressAction());
        
        int actCnt = esc.getAction().size();

        escApi.updateEscalation(esc);
        
        // Now look it up
        GetEscalationResponse resp = escApi.getEscalation(esc.getId());
        esc = resp.getEscalation();
        
        // Verify the updates
        assertEquals(className, esc.getDescription());
        assertEquals(maxWait, esc.getMaxPauseTime());
        assertTrue(esc.isNotifyAll());
        assertTrue(esc.isPauseAllowed());
        assertTrue(esc.isRepeat());
        
        assertEquals(actCnt, esc.getAction().size());
    }
}
