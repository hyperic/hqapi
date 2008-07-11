package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.GetEscalationResponse;

public class EscalationUpdateEscalation_test extends EscalationTestBase {

    public EscalationUpdateEscalation_test(String name) {
        super(name);
    }

    public void testUpdateEscalation() throws Exception {
        Escalation esc = getTestEscalation();
        
        // Update the escalation
        esc.setDescription(getClass().getName());
        esc.setMaxPauseTime(1000);
        esc.setNotifyAll(true);
        esc.setPauseAllowed(true);
        esc.setRepeat(true);
        getEscalationApi().updateEscalation(esc);
        
        // Now look it up
        GetEscalationResponse resp =
            getEscalationApi().getEscalation(esc.getId());
        esc = resp.getEscalation();
        
        // Verify the updates
        assertEquals(esc.getDescription(), getClass().getName());
        assertEquals(esc.getMaxPauseTime(), 1000);
        assertTrue(esc.isNotifyAll());
        assertTrue(esc.isPauseAllowed());
        assertTrue(esc.isRepeat());
    }
}
