package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertLogLevel;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertConditionType;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertCondition;

import java.util.List;
import java.util.ArrayList;

public class AlertDefinitionSyncLogCondition_test extends AlertDefinitionTestBase {

    public AlertDefinitionSyncLogCondition_test(String name) {
        super(name);
    }

    public void testValidPropertyConditon() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        final String LOG_MATCHES = "login";
        d.getAlertCondition().add(AlertDefinitionBuilder.
                createLogCondition(true, AlertLogLevel.INFO, LOG_MATCHES));

        List<AlertDefinition> defintions = new ArrayList<AlertDefinition>();
        defintions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(defintions);
        hqAssertSuccess(response);

        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(d);
            for (AlertCondition c : def.getAlertCondition()) {
                assertNotNull("Condition was null", c);
                assertEquals(c.getType(), AlertConditionType.LOG.getType());
                assertEquals(c.getLogLevel(), AlertLogLevel.INFO.getLevel());
                assertEquals(c.getLogMatches(), LOG_MATCHES);
            }
        }

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testValidPropertyConditonTypeAlert() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        final String LOG_MATCHES = "login";
        d.getAlertCondition().add(AlertDefinitionBuilder.
                createLogCondition(true, AlertLogLevel.INFO, LOG_MATCHES));

        List<AlertDefinition> defintions = new ArrayList<AlertDefinition>();
        defintions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(defintions);
        hqAssertSuccess(response);

        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(d);
            for (AlertCondition c : def.getAlertCondition()) {
                assertNotNull("Condition was null", c);
                assertEquals(c.getType(), AlertConditionType.LOG.getType());
                assertEquals(c.getLogLevel(), AlertLogLevel.INFO.getLevel());
                assertEquals(c.getLogMatches(), LOG_MATCHES);
            }
        }

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    // TODO: Required fields

    // TODO: Invalid log levels
}
