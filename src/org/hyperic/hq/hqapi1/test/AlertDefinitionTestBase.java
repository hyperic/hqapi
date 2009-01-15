package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.AlertDefinition;

public class AlertDefinitionTestBase extends HQApiTestBase {

    public AlertDefinitionTestBase(String name) {
        super(name);
    }

    protected void validateDefinition(AlertDefinition d) {
        assertNotNull(d.getName());
        assertTrue("Invalid frequency " + d.getFrequency(),
                   d.getFrequency() >= 0 && d.getFrequency() <= 4);
        assertTrue("Invalid priority " + d.getPriority(),
                   d.getPriority() >= 1 & d.getPriority() <= 3);
    }
}
