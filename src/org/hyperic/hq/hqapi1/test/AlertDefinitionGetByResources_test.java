package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;

import java.util.ArrayList;
import java.util.List;

public class AlertDefinitionGetByResources_test extends AlertDefinitionTestBase {

    public AlertDefinitionGetByResources_test(String name) {
        super(name);
    }

    public void testFindAlertDefinitions() throws Exception {

        HQApi api = getApi();
        ResourceApi rApi = api.getResourceApi();
        AlertDefinitionApi dApi = api.getAlertDefinitionApi();

        ResourcePrototypeResponse protoResponse =
                rApi.getResourcePrototype("CPU");
        hqAssertSuccess(protoResponse);

        ResourcesResponse resourcesResponse =
                rApi.getResources(protoResponse.getResourcePrototype(),
                                  false, false);
        hqAssertSuccess(resourcesResponse);

        AlertDefinitionsResponse response =
                dApi.getAlertDefinitions(resourcesResponse.getResource());
        hqAssertSuccess(response);
    }

    public void testFindAlertDefinitionsEmptyList() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi dApi = api.getAlertDefinitionApi();

        List<Resource> resources = new ArrayList<Resource>();

        AlertDefinitionsResponse response = dApi.getAlertDefinitions(resources);
        hqAssertSuccess(response);
    }

    public void testFindAlertDefinitionsInvalidResource() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi dApi = api.getAlertDefinitionApi();

        List<Resource> resources = new ArrayList<Resource>();
        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);
        r.setName("Invalid resource");
        resources.add(r);

        AlertDefinitionsResponse response =
                dApi.getAlertDefinitions(resources);
        hqAssertFailureObjectNotFound(response);
    }
}
