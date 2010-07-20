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
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.User;

import java.util.Collections;
import java.util.List;

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

    public void testGetResourceVerboseWithChildren() throws Exception {

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
        assertTrue("No properties found for resource " + r.getName(),
                   resource.getResourceProperty().size() > 0);
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
        assertTrue("Properties found for resource " + r.getName(),
                   resource.getResourceProperty().size() == 0);
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
        assertTrue("No configuration found for resource " + r.getName(),
                   resource.getResourceConfig().size() > 0);
        assertTrue("No properties found for resource " + r.getName(),
                   resource.getResourceProperty().size() > 0);
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
        assertTrue("Configuration found for resource " + r.getName(),
                   resource.getResourceConfig().size() == 0);
        assertTrue("Properties found for resource " + r.getName(),
                   resource.getResourceProperty().size() == 0);
        assertTrue("No child resources found for resource " + r.getName(),
                   resource.getResource().size() > 0);
    }

    public void testGetPlatformResource() throws Exception {

        Agent a = getRunningAgent();

        ResourceApi api = getApi().getResourceApi();

        ResourcesResponse findResponse = api.getResources(a, false, false);
        hqAssertSuccess(findResponse);

        assertTrue("Found 0 platform resources for agent " + a.getId(),
                   findResponse.getResource().size() > 0);

        Resource r = findResponse.getResource().get(0);

        ResourceResponse getResponse = api.getPlatformResource(r.getName(),
                                                               false, false);
        hqAssertSuccess(getResponse);
        Resource resource = getResponse.getResource();
        validateResource(resource);
    }

    public void testGetInvalidPlatformResource() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        ResourceResponse getResponse = api.getPlatformResource("Invalid platform",
                                                               false, false);
        hqAssertFailureObjectNotFound(getResponse);
    }

    public void testGetResourceByIdUnauthorized() throws Exception {
        List<User> users = createTestUsers(1);
        User user = users.get(0);
        ResourceApi api = getApi(user.getName(), TESTUSER_PASSWORD).getResourceApi();

        // Use admin user to get local platform..
        Resource localPlatform = getLocalPlatformResource(false, false);

        // Test find by ID
        ResourceResponse response = api.getResource(localPlatform.getId(), false, false);
        hqAssertFailurePermissionDenied(response);

        deleteTestUsers(users);
    }

    public void testGetResourceByNameUnauthorized() throws Exception {
        List<User> users = createTestUsers(1);
        User user = users.get(0);
        ResourceApi api = getApi(user.getName(), TESTUSER_PASSWORD).getResourceApi();

        // Use admin user to get local platform..
        Resource localPlatform = getLocalPlatformResource(false, false);

        // Test find by name
        ResourceResponse response = api.getPlatformResource(localPlatform.getName(), false, false);
        hqAssertFailurePermissionDenied(response);

        deleteTestUsers(users);
    }

    public void testGetPlatformResourceByPlatform() throws Exception {

        ResourceApi api = getApi().getResourceApi();
        Resource localPlatform = getLocalPlatformResource(false, false);

        ResourceResponse response = api.getPlatformResource(localPlatform.getId(), false, false);
        hqAssertSuccess(response);

        assertEquals("Platform ids not equal", localPlatform.getId(),
                     response.getResource().getId());
    }

    public void testGetPlatformResourceByServer() throws Exception {

        ResourceApi api = getApi().getResourceApi();
        Resource localPlatform = getLocalPlatformResource(false, true);

        List<Resource> servers = localPlatform.getResource();
        assertTrue("No servers found for " + localPlatform.getName(),
                   servers.size() > 0);

        Resource server = servers.get(0);

        ResourceResponse response = api.getPlatformResource(server.getId(), false, false);
        hqAssertSuccess(response);

        assertEquals("Platform ids not equal", localPlatform.getId(),
                     response.getResource().getId());
    }

    public void testGetPlatformResourceByService() throws Exception {

        ResourceApi api = getApi().getResourceApi();
        Resource localPlatform = getLocalPlatformResource(false, true);

        List<Resource> servers = localPlatform.getResource();
        assertTrue("No servers found for " + localPlatform.getName(),
                   servers.size() > 0);
        Resource service = null;
        for (Resource server : servers) {
            if (server.getResource().size() > 0) {
                service = server.getResource().get(0);
                break;
            }
        }

        assertNotNull("Unable to find a service for platform " + localPlatform.getName(),
                      service);

        ResourceResponse response = api.getPlatformResource(service.getId(), false, false);
        hqAssertSuccess(response);

        assertEquals("Platform ids not equal", localPlatform.getId(),
                     response.getResource().getId());
    }

    public void testGetPlatformResourceInvalidId() throws Exception {
        ResourceApi api = getApi().getResourceApi();

        ResourceResponse response = api.getPlatformResource(Integer.MAX_VALUE, false, false);
        hqAssertFailureObjectNotFound(response);
    }
}