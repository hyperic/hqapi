package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;

public class ResourceGet_test extends ResourceTestBase {

    public ResourceGet_test(String name) {
        super(name);
    }

    public void testGetInvalidResource() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        ResourceResponse resp = api.getResource(Integer.MAX_VALUE, false, false);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testGetResource() throws Exception {

        Agent a = getRunningAgent();

        ResourceApi api = getApi().getResourceApi();

        ResourcesResponse findResponse = api.getResources(a, false, false);
        hqAssertSuccess(findResponse);

        assertTrue("Found 0 platform resources for agent " + a.getId(),
                   findResponse.getResource().size() > 0);

        Resource r = findResponse.getResource().get(0);

        ResourceResponse getResponse = api.getResource(r.getId(), false, false);
        hqAssertSuccess(getResponse);
        Resource resource = getResponse.getResource();
        validateResource(resource);
    }

    public void testGetResourceWithConfigAndChildren() throws Exception {

        Agent a = getRunningAgent();

        ResourceApi api = getApi().getResourceApi();

        ResourcesResponse findResponse = api.getResources(a, false, false);
        hqAssertSuccess(findResponse);

        assertTrue("Found 0 platform resources for agent " + a.getId(),
                   findResponse.getResource().size() > 0);

        Resource r = findResponse.getResource().get(0);

        ResourceResponse getResponse = api.getResource(r.getId(), true, true);
        hqAssertSuccess(getResponse);
        Resource resource = getResponse.getResource();
        validateResource(resource);
        assertTrue("No configuration found for resource " + r.getName(),
                   resource.getResourceConfig().size() > 0);
        assertTrue("No child resources found for resource " + r.getName(),
                   resource.getResource().size() > 0);
    }

    public void testGetResourceNoConfigNoChildren() throws Exception {

        Agent a = getRunningAgent();

        ResourceApi api = getApi().getResourceApi();

        ResourcesResponse findResponse = api.getResources(a, false, false);
        hqAssertSuccess(findResponse);

        assertTrue("Found 0 platform resources for agent " + a.getId(),
                   findResponse.getResource().size() > 0);

        Resource r = findResponse.getResource().get(0);

        ResourceResponse getResponse = api.getResource(r.getId(), false, false);
        hqAssertSuccess(getResponse);
        Resource resource = getResponse.getResource();
        validateResource(resource);
        assertTrue("Configuration found for resource " + r.getName(),
                   resource.getResourceConfig().size() == 0);
        assertTrue("Child resources found for resource " + r.getName(),
                   resource.getResource().size() == 0);
    }

    public void testGetResourceConfigOnly() throws Exception {

        Agent a = getRunningAgent();

        ResourceApi api = getApi().getResourceApi();

        ResourcesResponse findResponse = api.getResources(a, false, false);
        hqAssertSuccess(findResponse);

        assertTrue("Found 0 platform resources for agent " + a.getId(),
                   findResponse.getResource().size() > 0);

        Resource r = findResponse.getResource().get(0);

        ResourceResponse getResponse = api.getResource(r.getId(), true, false);
        hqAssertSuccess(getResponse);
        Resource resource = getResponse.getResource();
        validateResource(resource);
        assertTrue("No configuration found for reosurce " + r.getName(),
                   resource.getResourceConfig().size() > 0);
        assertTrue("Child resources found for resource " + r.getName(),
                   resource.getResource().size() == 0);
    }

    public void testGetResourceChildrenOnly() throws Exception {

        Agent a = getRunningAgent();

        ResourceApi api = getApi().getResourceApi();

        ResourcesResponse findResponse = api.getResources(a, false, false);
        hqAssertSuccess(findResponse);

        assertTrue("Found 0 platform resources for agent " + a.getId(),
                   findResponse.getResource().size() > 0);

        Resource r = findResponse.getResource().get(0);

        ResourceResponse getResponse = api.getResource(r.getId(), false, true);
        hqAssertSuccess(getResponse);
        Resource resource = getResponse.getResource();
        validateResource(resource);
        assertTrue("Configuration found for reosurce " + r.getName(),
                   resource.getResourceConfig().size() == 0);
        assertTrue("No child resources found for resource " + r.getName(),
                   resource.getResource().size() > 0);
    }
}