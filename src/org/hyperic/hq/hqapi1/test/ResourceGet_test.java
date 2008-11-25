package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.GetResourceResponse;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.FindResourcesResponse;
import org.hyperic.hq.hqapi1.types.Resource;

public class ResourceGet_test extends ResourceTestBase {

    public ResourceGet_test(String name) {
        super(name);
    }

    public void testGetInvalidResource() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        GetResourceResponse resp = api.getResource(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testGetResource() throws Exception {

        Agent a = getRunningAgent();

        ResourceApi api = getApi().getResourceApi();

        FindResourcesResponse findResponse = api.findResources(a);
        hqAssertSuccess(findResponse);

        // This test assumes if you have a local agent that is pingable that
        // there will be at least one platform servicing it.
        assertTrue("Found 0 platform resources for agent " + a.getId(),
                   findResponse.getResource().size() > 0);
        for (Resource r : findResponse.getResource()) {
            // Now that we have valid resource ids, query each
            Integer rid = r.getId();

            GetResourceResponse getResponse = api.getResource(rid);
            hqAssertSuccess(getResponse);
            Resource resource = getResponse.getResource();
            validateResource(resource);
        }
    }

    final Integer ID_10001 = 10001;

    public void testGetResourceByPlatformId() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        // TODO: Get a valid id?
        //GetResourceResponse resp = api.getResourceForPlatform(ID_10001);
        //hqAssertSuccess(resp);
        //validateResource(resp.getResource());
    }

    public void testGetResourceByInvalidPlatformId() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        GetResourceResponse resp = api.getResourceForPlatform(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testGetResourceByPlatformName() throws Exception {

        //ResourceApi api = getApi().getResourceApi();

        // TODO: Get a valid id.
        //GetResourceResponse resp = api.getResourceForPlatform(ID_10001);
        //hqAssertSuccess(resp);
        //Resource r1 = resp.getResource();
        //validateResource(r1);

        //String name = resp.getResource().getName();
        //GetResourceResponse respByName = api.getResourceForPlatform(name);
        //hqAssertSuccess(respByName);
        //Resource r2 = resp.getResource();
        //validateResource(r2);

        //assertEquals(r1.getId(), r2.getId());
        //assertEquals(r1.getDescription(), r2.getDescription());
        //assertEquals(r1.getName(), r2.getName());
    }

    public void testGetResourceByInvalidPlatformName() throws Exception {

        final String name = "Non-existant platform name";
        ResourceApi api = getApi().getResourceApi();
        GetResourceResponse resp = api.getResourceForPlatform(name);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testGetResourceByServer() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        // TODO: Get a valid id?
        //GetResourceResponse resp = api.getResourceForServer(ID_10001);
        //hqAssertSuccess(resp);
        //validateResource(resp.getResource());
    }

    public void testGetResourceByInvalidServer() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        GetResourceResponse resp = api.getResourceForServer(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testGetResourceByService() throws Exception {

        ResourceApi api = getApi().getResourceApi();
        // TODO: Get a valid id?
        //GetResourceResponse resp = api.getResourceForService(ID_10001);
        //hqAssertSuccess(resp);
        //validateResource(resp.getResource());
    }

    public void testGetResourceByInvalidService() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        GetResourceResponse resp = api.getResourceForService(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(resp);
    }
}