package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourceConfig;
import org.hyperic.hq.hqapi1.types.ResourceProperty;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class ResourceUpdate_test extends ResourceTestBase {

    public ResourceUpdate_test(String name) {
        super(name);
    }

    public void testUpdateFields() throws Exception {

        Agent a = getRunningAgent();
        ResourceApi api = getApi().getResourceApi();

        ResourcesResponse resourcesResponse = api.getResources(a, false, false);
        hqAssertSuccess(resourcesResponse);
        Resource platform = resourcesResponse.getResource().get(0);

        final String UPDATED_NAME = "Updated platform";
        final String UPDATED_DESCRIPTION = "Updated description";

        String origName = platform.getName();
        String origDescription = platform.getDescription();

        platform.setName(UPDATED_NAME);
        platform.setDescription(UPDATED_DESCRIPTION);

        StatusResponse updateResponse = api.updateResource(platform);
        hqAssertSuccess(updateResponse);

        ResourceResponse updatedResource = api.getResource(platform.getId(),
                                                           false, false);
        hqAssertSuccess(updatedResource);
        Resource updated = updatedResource.getResource();

        assertEquals(updated.getName(), UPDATED_NAME);
        assertEquals(updated.getDescription(), UPDATED_DESCRIPTION);

        // Reset
        updated.setName(origName);
        updated.setDescription(origDescription);

        updateResponse = api.updateResource(updated);
        hqAssertSuccess(updateResponse);
    }

    public void testUpdateConfig() throws Exception {

        ResourceApi api = getApi().getResourceApi();
        Resource createdResource = createTestHTTPService();

        final String UPDATED_HOSTNAME = "www.yahoo.com";
        for (ResourceConfig c : createdResource.getResourceConfig()) {
            if (c.getKey().equals("hostname")) {
                c.setValue(UPDATED_HOSTNAME);
            }
        }

        StatusResponse updateResponse = api.updateResource(createdResource);
        hqAssertSuccess(updateResponse);

        ResourceResponse getResponse = api.getResource(createdResource.getId(),
                                                       false, false);
        hqAssertSuccess(getResponse);

        Resource updatedResource = getResponse.getResource();
        for (ResourceConfig c : updatedResource.getResourceConfig()) {
            if (c.getKey().equals("hostname")) {
                assertEquals(c.getValue(), UPDATED_HOSTNAME);
            }
        }

        // Cannot delete resources soon after modifying them..
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Ignore
        }

        // Cleanup
        StatusResponse deleteResponse = api.deleteResource(updatedResource.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testUpdateProperties() throws Exception {

        ResourceApi api = getApi().getResourceApi();
        Resource platform = getLocalPlatformResource(true, false);

        final String UPDATED_GW = "1.2.3.4";
        String origGw = null;

        for (ResourceProperty p : platform.getResourceProperty()) {
            if (p.getKey().equals("defaultGateway")) {
                origGw = p.getValue();
                p.setValue(UPDATED_GW);
                break;
            }
        }

        assertNotNull(origGw);

        StatusResponse updateResponse = api.updateResource(platform);
        hqAssertSuccess(updateResponse);

        ResourceResponse getRequest = api.getResource(platform.getId(), false,
                                                      false);
        hqAssertSuccess(getRequest);

        for (ResourceProperty p : platform.getResourceProperty()) {
            if (p.getKey().equals("defaultGateway")) {
                assertEquals(p.getValue(), UPDATED_GW);
                p.setValue(origGw);
                break;
            }
        }

        updateResponse = api.updateResource(platform);
        hqAssertSuccess(updateResponse);
    }

    public void testUpdateInvalidResource() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);
        r.setName("Invalid Resource");

        StatusResponse updateResponse = api.updateResource(r);
        hqAssertFailureObjectNotFound(updateResponse);
    }

    public void testUpdateInvalidChildResource() throws Exception {

        Agent a = getRunningAgent();
        ResourceApi api = getApi().getResourceApi();

        ResourcesResponse resourcesResponse = api.getResources(a, false, false);
        hqAssertSuccess(resourcesResponse);
        Resource platform = resourcesResponse.getResource().get(0);

        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);
        r.setName("Invalid child resource");
        platform.getResource().add(r);

        StatusResponse updateResponse = api.updateResource(platform);
        hqAssertFailureObjectNotFound(updateResponse);
    }
}

