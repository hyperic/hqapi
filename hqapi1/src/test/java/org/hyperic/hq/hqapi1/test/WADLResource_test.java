package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLResource_test extends WADLTestBase {

    public void testResourceFindGet() throws Exception {
        Endpoint.ResourceFindHqu resourceFind = new Endpoint.ResourceFindHqu();
        Endpoint.ResourceGetHqu resourceGet = new Endpoint.ResourceGetHqu();

        ResourcesResponse response =
                resourceFind.getAsResourcesResponse(null, "CPU", null, false, false);
        hqAssertSuccess(response);
        assertTrue("No resources found", response.getResource().size() > 0);

        Resource r = response.getResource().get(0);
        ResourceResponse getResponse = resourceGet.getAsResourceResponse(r.getId(),
                                                                         null,
                                                                         false,
                                                                         false);
        hqAssertSuccess(getResponse);
        assertTrue("Resource names do not match",
                   r.getName().equals(getResponse.getResource().getName()));
    }

    public void testCreatePlatform() throws Exception {
        Endpoint.ResourceCreatePlatformHqu createPlatform =
                new Endpoint.ResourceCreatePlatformHqu();
        Endpoint.ResourceDeleteHqu deleteResource =
                new Endpoint.ResourceDeleteHqu();
    }

    public void testCreateResource() throws Exception {
        Endpoint.ResourceCreateResourceHqu createResource =
                new Endpoint.ResourceCreateResourceHqu();
        Endpoint.ResourceDeleteHqu deleteResource =
                new Endpoint.ResourceDeleteHqu();
        Endpoint.ResourceSyncHqu syncResource = new Endpoint.ResourceSyncHqu();
    }

    public void testGetResourcePrototypes() throws Exception {
        Endpoint.ResourceGetResourcePrototypesHqu resourceGetPrototypes =
                new Endpoint.ResourceGetResourcePrototypesHqu();

        ResourcePrototypesResponse response =
                resourceGetPrototypes.getAsResourcePrototypesResponse();
        hqAssertSuccess(response);
        assertTrue("No ResourcePrototypes found",
                   response.getResourcePrototype().size() > 0);

        response = resourceGetPrototypes.getAsResourcePrototypesResponse(true);
        hqAssertSuccess(response);
        assertTrue("No ResourcePrototypes found",
                   response.getResourcePrototype().size() > 0);
    }

    public void testGetResourcePrototype() throws Exception {
        Endpoint.ResourceGetResourcePrototypeHqu resourceGetPrototype =
                new Endpoint.ResourceGetResourcePrototypeHqu();

        ResourcePrototypeResponse response =
                resourceGetPrototype.getAsResourcePrototypeResponse("CPU");
        hqAssertSuccess(response);
    }
}
