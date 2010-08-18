package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLResource_test extends WADLTestBase {

    public void testResourceFindGet() throws Exception {
        HttpLocalhost8080HquHqapi1.ResourceFindHqu resourceFind = new HttpLocalhost8080HquHqapi1.ResourceFindHqu();
        HttpLocalhost8080HquHqapi1.ResourceGetHqu resourceGet = new HttpLocalhost8080HquHqapi1.ResourceGetHqu();

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
        HttpLocalhost8080HquHqapi1.ResourceCreatePlatformHqu createPlatform =
                new HttpLocalhost8080HquHqapi1.ResourceCreatePlatformHqu();
        HttpLocalhost8080HquHqapi1.ResourceDeleteHqu deleteResource =
                new HttpLocalhost8080HquHqapi1.ResourceDeleteHqu();
    }

    public void testCreateResource() throws Exception {
        HttpLocalhost8080HquHqapi1.ResourceCreateResourceHqu createResource =
                new HttpLocalhost8080HquHqapi1.ResourceCreateResourceHqu();
        HttpLocalhost8080HquHqapi1.ResourceDeleteHqu deleteResource =
                new HttpLocalhost8080HquHqapi1.ResourceDeleteHqu();
        HttpLocalhost8080HquHqapi1.ResourceSyncHqu syncResource = new HttpLocalhost8080HquHqapi1.ResourceSyncHqu();
    }

    public void testGetResourcePrototypes() throws Exception {
        HttpLocalhost8080HquHqapi1.ResourceGetResourcePrototypesHqu resourceGetPrototypes =
                new HttpLocalhost8080HquHqapi1.ResourceGetResourcePrototypesHqu();

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
        HttpLocalhost8080HquHqapi1.ResourceGetResourcePrototypeHqu resourceGetPrototype =
                new HttpLocalhost8080HquHqapi1.ResourceGetResourcePrototypeHqu();

        ResourcePrototypeResponse response =
                resourceGetPrototype.getAsResourcePrototypeResponse("CPU");
        hqAssertSuccess(response);
    }
}
