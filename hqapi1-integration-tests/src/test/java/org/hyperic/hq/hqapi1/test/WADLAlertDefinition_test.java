package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

import java.util.ArrayList;
import java.util.List;

public class WADLAlertDefinition_test extends WADLTestBase {

    public void testListDefinitions() throws Exception {
        Endpoint.AlertdefinitionListDefinitionsHqu adList =
                new Endpoint.AlertdefinitionListDefinitionsHqu();

        AlertDefinitionsResponse response = adList.getAsAlertDefinitionsResponse();
        hqAssertSuccess(response);
    }

    public void testListDefinitionsByResources() throws Exception {
        Endpoint.AlertdefinitionListDefinitionsByResourcesHqu adList =
                new Endpoint.AlertdefinitionListDefinitionsByResourcesHqu();
        List<Resource> resources = new ArrayList<Resource>();
        ResourcesRequest request = new ResourcesRequest();
        request.getResource().addAll(resources);
        AlertDefinitionsResponse response = adList.postAsAlertDefinitionsResponse(request);
        hqAssertSuccess(response);
    }

    public void testListTypeDefinitions() throws Exception {
        Endpoint.AlertdefinitionListTypeDefinitionsHqu adTypeList =
                new Endpoint.AlertdefinitionListTypeDefinitionsHqu();

        AlertDefinitionsResponse response = adTypeList.getAsAlertDefinitionsResponse();
        hqAssertSuccess(response);
    }

    public void testDelete() throws Exception {
        Endpoint.AlertdefinitionDeleteHqu adDelete =
                new Endpoint.AlertdefinitionDeleteHqu();

        StatusResponse response = adDelete.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(response); // Won't exist.
    }

    public void testSync() throws Exception {
        Endpoint.AlertdefinitionSyncHqu adSync =
                new Endpoint.AlertdefinitionSyncHqu();
        AlertDefinitionsRequest request = new AlertDefinitionsRequest();

        AlertDefinitionsResponse response = adSync.postAsAlertDefinitionsResponse(request);
        hqAssertSuccess(response);
    }
}
