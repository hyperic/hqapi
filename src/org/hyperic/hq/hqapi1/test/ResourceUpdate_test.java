/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

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
                                                       true, false);
        hqAssertSuccess(getResponse);

        Resource updatedResource = getResponse.getResource();
        assertTrue("No configuration found for " + updatedResource.getName(),
                   updatedResource.getResourceConfig().size() > 0);
        boolean foundHostname = false;
        for (ResourceConfig c : updatedResource.getResourceConfig()) {
            if (c.getKey().equals("hostname")) {
                assertEquals(c.getValue(), UPDATED_HOSTNAME);
                foundHostname = true;
            }
        }
        assertTrue("Unable to find hostname configuration for " + updatedResource.getName(),
                   foundHostname);

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

    public void testUpdateResetConfig() throws Exception {
        ResourceApi api = getApi().getResourceApi();
        Resource createdResource = createTestHTTPService();

        // Add pattern attribute
        ResourceConfig patternConfig = new ResourceConfig();
        patternConfig.setKey("pattern");
        patternConfig.setValue("html");
        createdResource.getResourceConfig().add(patternConfig);

        StatusResponse updateResponse = api.updateResource(createdResource);
        hqAssertSuccess(updateResponse);

        ResourceResponse getResponse = api.getResource(createdResource.getId(),
                                                       true, false);
        hqAssertSuccess(getResponse);

        Resource updatedResource = getResponse.getResource();
        assertTrue("No configuration found for " + updatedResource.getName(),
                   updatedResource.getResourceConfig().size() > 0);
        boolean foundPattern = false;
        for (ResourceConfig c : updatedResource.getResourceConfig()) {
            if (c.getKey().equals("pattern")) {
                assertEquals(c.getValue(), "html");
                // Reset to ""
                c.setValue("");
                foundPattern = true;
            }
        }

        assertTrue("Unable to find pattern configuration for " + updatedResource.getName(),
                   foundPattern);

        updateResponse = api.updateResource(updatedResource);
        hqAssertSuccess(updateResponse);

        getResponse = api.getResource(createdResource.getId(), true, false);
        hqAssertSuccess(getResponse);

        updatedResource = getResponse.getResource();
        assertTrue("No configuration found for " + updatedResource.getName(),
                   updatedResource.getResourceConfig().size() > 0);
        foundPattern = false;
        for (ResourceConfig c : updatedResource.getResourceConfig()) {
            if (c.getKey().equals("pattern")) {
                assertEquals("pattern is not empty", c.getValue(), "");
                foundPattern = true;
            }
        }

        assertTrue("Unable to find pattern configuration for " + updatedResource.getName(),
                   foundPattern);

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

        assertNotNull("Unable to find default gateway property for resource " +
                      platform.getName() + origGw);

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

    public void testUpdateInvalidDescription() throws Exception {

        ResourceApi api = getApi().getResourceApi();
        Resource createdResource = createTestHTTPService();

        String longDescription = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        createdResource.setDescription(longDescription);

        StatusResponse updateResponse = api.updateResource(createdResource);
        hqAssertFailureInvalidParameters(updateResponse);

        // Cannot delete resources soon after modifying them..
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Ignore
        }

        // Cleanup
        StatusResponse deleteResponse = api.deleteResource(createdResource.getId());
        hqAssertSuccess(deleteResponse);
    }
}

