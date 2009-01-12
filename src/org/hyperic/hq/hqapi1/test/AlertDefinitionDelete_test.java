package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class AlertDefinitionDelete_test extends AlertDefinitionTestBase {

    public AlertDefinitionDelete_test(String name) {
        super(name);
    }

    public void testDeleteBadId() throws Exception {

        AlertDefinitionApi alertDefApi = getApi().getAlertDefinitionApi();

        StatusResponse deleteResponse =
                alertDefApi.deleteAlertDefinition(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(deleteResponse);
    }
}
