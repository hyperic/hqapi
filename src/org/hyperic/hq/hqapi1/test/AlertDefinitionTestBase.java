package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertPriority;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class AlertDefinitionTestBase extends HQApiTestBase {

    public AlertDefinitionTestBase(String name) {
        super(name);
    }

    protected AlertDefinition generateTestDefinition() {
        AlertDefinition d = new AlertDefinition();

        Random r = new Random();
        d.setName("Test Alert Definition" + r.nextInt());
        d.setDescription("Test Alert Description");
        d.setPriority(AlertPriority.MEDIUM.getPriority());
        d.setActive(true);
        return d;
    }

    protected void validateDefinition(AlertDefinition d) {
        assertNotNull(d.getName());
        assertTrue("Invalid frequency " + d.getFrequency(),
                   d.getFrequency() >= 0 && d.getFrequency() <= 4);
        assertTrue("Invalid priority " + d.getPriority(),
                   d.getPriority() >= 1 & d.getPriority() <= 3);
    }

    protected void cleanup(List<AlertDefinition> definitions) throws IOException {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        for (AlertDefinition d : definitions) {
            StatusResponse deleteResponse = api.deleteAlertDefinition(d.getId());
            hqAssertSuccess(deleteResponse);
        }
    }
}
