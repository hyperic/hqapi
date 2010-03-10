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

package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.CreatePlatformRequest;
import org.hyperic.hq.hqapi1.types.CreateResourceRequest;
import org.hyperic.hq.hqapi1.types.Ip;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourceConfig;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypesResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.ResourcesRequest;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Hyperic HQ Resource API.
 * <br><br>
 * This class provides access to the {@link org.hyperic.hq.hqapi1.types.Resource}s
 * and {@link ResourcePrototype}s within the HQ system.  Each of the methods in
 * this class return
 * {@link org.hyperic.hq.hqapi1.types.Response} objects that wrap the result
 * of the method with a
 * {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 * <br><br>
 * A {@link org.hyperic.hq.hqapi1.types.ResourcePrototype} is a class of
 * {@link org.hyperic.hq.hqapi1.types.Resource}.  For example, Linux, OS X,
 * FileServer File, Nagios Check
 * <br><br>
 * A {@link org.hyperic.hq.hqapi1.types.Resource} is an instance of a
 * {@link org.hyperic.hq.hqapi1.types.ResourcePrototype}.  For example,
 * google.com port 80 check, Local Tomcat Instance   
 */
public class ResourceApi extends BaseApi {
    
    ResourceApi(HQConnection conn) {
        super(conn);
    }

    /**
     * Find all {@link ResourcePrototype}s in the system.  
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the list of ResourcePrototypes are returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourcePrototypesResponse#getResourcePrototype()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */    
    public ResourcePrototypesResponse getAllResourcePrototypes()
        throws IOException
    {   
        return doGet("resource/getResourcePrototypes.hqu",
                     new HashMap<String,String[]>(),
                     ResourcePrototypesResponse.class);
    }

    /**
     * Find {@link ResourcePrototype}s in the system that have at least 1
     * {@link Resource} of that type.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the list of ResourcePrototypes are returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourcePrototypesResponse#getResourcePrototype()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourcePrototypesResponse getResourcePrototypes()
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("existing", new String[] { Boolean.toString(Boolean.TRUE) });
        return doGet("resource/getResourcePrototypes.hqu", params,
                     ResourcePrototypesResponse.class);
    }

    /**
     * Find a {@link ResourcePrototype} by name.
     *
     * @param name The name of the ResourcePrototype to find
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the ResourcePrototypes is returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse#getResourcePrototype()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourcePrototypeResponse getResourcePrototype(String name)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("name", new String[] { name });
        return doGet("resource/getResourcePrototype.hqu",
                     params, ResourcePrototypeResponse.class);
    }

    /**
     * Create a Platform {@link Resource} with the given name.
     *
     * @param agent The {@link org.hyperic.hq.hqapi1.types.Agent} which will service this platform.
     * @param type The resource prototype for the resource to be created.
     * @param name The name of the resource to create.
     * @param fqdn The FQDN for the platform.
     * @param ips The list of {@link org.hyperic.hq.hqapi1.types.Ip}s for this platform.
     * @param config The configuration for the platform.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the created Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourceResponse#getResource()}.
     * 
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourceResponse createPlatform(Agent agent,
                                           ResourcePrototype type,
                                           String name,
                                           String fqdn,
                                           List<Ip> ips,
                                           Map<String,String> config)
        throws IOException
    {
        Resource platform = new Resource();
        platform.setName(name);
        for (String k : config.keySet()) {
            ResourceConfig c = new ResourceConfig();
            c.setKey(k);
            c.setValue(config.get(k));
            platform.getResourceConfig().add(c);
        }

        CreatePlatformRequest request = new CreatePlatformRequest();
        request.setAgent(agent);
        request.setResource(platform);
        request.setPrototype(type);
        request.setFqdn(fqdn);
        request.getIp().addAll(ips);

        return doPost("resource/createPlatform.hqu", request,
                      ResourceResponse.class);
    }

    private ResourceResponse createResource(ResourcePrototype type,
                                            Resource parent,
                                            String name,
                                            Map<String,String> config)
        throws IOException
    {
        Resource resource = new Resource();
        resource.setName(name);
        for (String k : config.keySet()) {
            ResourceConfig c = new ResourceConfig();
            c.setKey(k);
            c.setValue(config.get(k));
            resource.getResourceConfig().add(c);
        }


        CreateResourceRequest request = new CreateResourceRequest();
        request.setParent(parent);
        request.setResource(resource);
        request.setPrototype(type);

        return doPost("resource/createResource.hqu", request,
                      ResourceResponse.class);
    }

    /**
     * Create a Server {@link Resource} with the given name.
     *
     * @param type The resource prototype for the resource to be created.
     * @param parent The parent resource for the created resource.
     * @param name The name of the resource to create.
     * @param config The configuration for the server.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the created Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourceResponse createServer(ResourcePrototype type,
                                         Resource parent,
                                         String name,
                                         Map<String,String> config)
        throws IOException
    {
        return createResource(type, parent, name, config);
    }

    /**
     * Create a Service {@link Resource} with the given name.
     *
     * @param type The {@link ResourcePrototype} for the resource to be created.
     * @param parent The parent {@link Resource} for the created resource. In
     * the case of platform service checks like HTTP, the parent resource
     * will be the platform Resource.
     * @param name The name of the resource to create.
     * @param config The configuration for the service.
     * 
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the created Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourceResponse createService(ResourcePrototype type,
                                          Resource parent,
                                          String name,
                                          Map<String,String> config)
        throws IOException
    {
        return createResource(type, parent, name, config);
    }

    /**
     * Get a {@link Resource} by id.
     *
     * @param id The resource id to look up.
     * @param verbose Flag to indicate whether {@link org.hyperic.hq.hqapi1.types.ResourceConfig}
     * and {@link org.hyperic.hq.hqapi1.types.ResourceProperty} information will
     * be included.
     * @param children Flag to control whether child resources of this resource
     * will be included.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourceResponse getResource(int id, boolean verbose, boolean children)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(id) });
        params.put("verbose", new String[] { Boolean.toString(verbose) });
        params.put("children", new String[] { Boolean.toString(children)});
        return doGet("resource/get.hqu", params,
                     ResourceResponse.class);
    }

    /**
     * Get a {@link Resource} by it's platform name.
     *
     * @param name The platform name to look up.
     * @param verbose Flag to indicate whether {@link org.hyperic.hq.hqapi1.types.ResourceConfig}
     * and {@link org.hyperic.hq.hqapi1.types.ResourceProperty} information will
     * be included.
     * @param children Flag to control whether child resources of this resource
     * will be included.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourceResponse getPlatformResource(String name, boolean verbose,
                                                boolean children)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("platformName", new String[] { name });
        params.put("verbose", new String[] { Boolean.toString(verbose) });
        params.put("children", new String[] { Boolean.toString(children)});
        return doGet("resource/get.hqu", params,
                     ResourceResponse.class);
    }

    /**
     * Find the platform {@link Resource}s serviced by the given
     * {@link org.hyperic.hq.hqapi1.types.Agent}.
     *
     * @param agent The {@link org.hyperic.hq.hqapi1.types.Agent} to query.
     * @param verbose Flag to indicate whether {@link org.hyperic.hq.hqapi1.types.ResourceConfig}
     * and {@link org.hyperic.hq.hqapi1.types.ResourceProperty} information will
     * be included.
     * @param children Flag to control whether child resources of this resource
     * will be included.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the list of Resources are returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourcesResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     *
     */
    public ResourcesResponse getResources(Agent agent, boolean verbose,
                                          boolean children)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("agentId", new String[] { Integer.toString(agent.getId()) });
        params.put("verbose", new String[] { Boolean.toString(verbose) });
        params.put("children", new String[] { Boolean.toString(children)});
        return doGet("resource/find.hqu", params,
                     ResourcesResponse.class);
    }

    /**
     * Find {@link org.hyperic.hq.hqapi1.types.Resource}s of the
     * given {@link org.hyperic.hq.hqapi1.types.ResourcePrototype}.
     *
     * @param pt The {@link ResourcePrototype} to search for.
     * @param verbose Flag to indicate whether {@link org.hyperic.hq.hqapi1.types.ResourceConfig}
     * and {@link org.hyperic.hq.hqapi1.types.ResourceProperty} information will
     * be included.
     * @param children Flag to control whether child resources of this resource
     * will be included.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the list of Resources are returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourcesResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourcesResponse getResources(ResourcePrototype pt, boolean verbose,
                                          boolean children)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String, String[]>();
        params.put("prototype", new String[] { pt.getName() });
        params.put("verbose", new String[] { Boolean.toString(verbose) });
        params.put("children", new String[] { Boolean.toString(children)});
        return doGet("resource/find.hqu", params,
                     ResourcesResponse.class);
    }

    /**
     * Find {@link org.hyperic.hq.hqapi1.types.Resource}s that have a
     * description that matches in whole or part the passed value.
     *
     * @param description The description to search for.
     * @param verbose Flag to indicate whether {@link org.hyperic.hq.hqapi1.types.ResourceConfig}
     * and {@link org.hyperic.hq.hqapi1.types.ResourceProperty} information will
     * be included.
     * @param children Flag to control whether child resources of this resource
     * will be included.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the list of Resources are returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourcesResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourcesResponse getResources(String description, boolean verbose,
                                          boolean children)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String, String[]>();
        params.put("description", new String[] { description });
        params.put("verbose", new String[] { Boolean.toString(verbose) });
        params.put("children", new String[] { Boolean.toString(children)});
        return doGet("resource/find.hqu", params,
                     ResourcesResponse.class);
    }

    /**
     * Update a {@link org.hyperic.hq.hqapi1.types.Resource}
     *
     * @param resource The Resource to update
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * users were updated successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse updateResource(Resource resource)
        throws IOException
    {
        ResourcesRequest request = new ResourcesRequest();
        request.getResource().add(resource);

        return doPost("resource/update.hqu", request, StatusResponse.class);
    }

    /**
     * Sync a list of {@link org.hyperic.hq.hqapi1.types.Resource}s.
     *
     * @param resources The list of resources to sync.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * resources were synced successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse syncResources(List<Resource> resources)
        throws IOException
    {
        ResourcesRequest request = new ResourcesRequest();
        request.getResource().addAll(resources);

        return doPost("resource/sync.hqu", request, StatusResponse.class);
    }

    /**
     * Delete the given {@link Resource}.
     *
     * @param id The id of the {@link org.hyperic.hq.hqapi1.types.Resource} to delete.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the list of Resources are returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourcesResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public StatusResponse deleteResource(int id)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(id) });
        return doGet("resource/delete.hqu", params, StatusResponse.class);
    }

    /**
     * Move a Resource
     *
     * @param target The target {@link Resource} to move.
     * @param destination The destination {@link Resource} for the move.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * Resource was successfully moved.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public StatusResponse moveResource(Resource target, Resource destination)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String, String[]>();
        params.put("targetId", new String[] { Integer.toString(target.getId()) });
        params.put("destinationId", new String[] { Integer.toString(destination.getId() )});
        return doGet("resource/move.hqu", params, StatusResponse.class);
    }
}
