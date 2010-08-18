package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

import java.util.ArrayList;
import java.util.List;

public class WADLAlertDefinition_test extends WADLTestBase {

    public void testListDefinitions() throws Exception {
        HttpLocalhost8080HquHqapi1.AlertdefinitionListDefinitionsHqu adList =
                new HttpLocalhost8080HquHqapi1.AlertdefinitionListDefinitionsHqu();

        AlertDefinitionsResponse response = adList.getAsAlertDefinitionsResponse();
        hqAssertSuccess(response);
    }

    public void testListDefinitionsByResources() throws Exception {
        HttpLocalhost8080HquHqapi1.AlertdefinitionListDefinitionsByResourcesHqu adList =
                new HttpLocalhost8080HquHqapi1.AlertdefinitionListDefinitionsByResourcesHqu();
        List<Resource> resources = new ArrayList<Resource>();
        ResourcesRequest request = new ResourcesRequest();
        request.getResource().addAll(resources);
        AlertDefinitionsResponse response = adList.postApplicationXmlAsAlertDefinitionsResponse(request);
        hqAssertSuccess(response);
    }

    public void testListTypeDefinitions() throws Exception {
        HttpLocalhost8080HquHqapi1.AlertdefinitionListTypeDefinitionsHqu adTypeList =
                new HttpLocalhost8080HquHqapi1.AlertdefinitionListTypeDefinitionsHqu();

        AlertDefinitionsResponse response = adTypeList.getAsAlertDefinitionsResponse();
        hqAssertSuccess(response);
    }

    public void testDelete() throws Exception {
        HttpLocalhost8080HquHqapi1.AlertdefinitionDeleteHqu adDelete =
                new HttpLocalhost8080HquHqapi1.AlertdefinitionDeleteHqu();

        StatusResponse response = adDelete.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(response); // Won't exist.
    }

    public void testSync() throws Exception {
        HttpLocalhost8080HquHqapi1.AlertdefinitionSyncHqu adSync =
                new HttpLocalhost8080HquHqapi1.AlertdefinitionSyncHqu();
        AlertDefinitionsRequest request = new AlertDefinitionsRequest();

        AlertDefinitionsResponse response = adSync.postApplicationXmlAsAlertDefinitionsResponse(request);
        hqAssertSuccess(response);
    }
}
