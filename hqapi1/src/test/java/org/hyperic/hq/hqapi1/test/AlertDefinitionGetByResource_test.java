package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.Resource;

public class AlertDefinitionGetByResource_test extends AlertDefinitionTestBase {

    public AlertDefinitionGetByResource_test(String name) {
        super(name);
    }

    public void testGetByResourceWithChildren() throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        Resource localPlatform = getLocalPlatformResource(false, false);
        AlertDefinitionsResponse response = api.getAlertDefinitions(localPlatform, true);
        hqAssertSuccess(response);
    }

    public void testGetByInvalidResourceWithChildren() throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);
        AlertDefinitionsResponse response = api.getAlertDefinitions(r, true);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetByResource() throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        Resource localPlatform = getLocalPlatformResource(false, false);
        AlertDefinitionsResponse response = api.getAlertDefinitions(localPlatform, false);
        hqAssertSuccess(response);
    }

    public void testGetByInvalidResource() throws Exception {
        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);
        AlertDefinitionsResponse response = api.getAlertDefinitions(r, false);
        hqAssertFailureObjectNotFound(response);
    }
}
