package org.hyperic.hq.hqapi1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.CreateResourceResponse;
import org.hyperic.hq.hqapi1.types.FindResourcesResponse;
import org.hyperic.hq.hqapi1.types.GetResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.GetResourceResponse;
import org.hyperic.hq.hqapi1.types.ListResourcePrototypesResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.CreateServiceRequest;
import org.hyperic.hq.hqapi1.types.ResourceConfig;
import org.hyperic.hq.hqapi1.types.CreatePlatformRequest;
import org.hyperic.hq.hqapi1.types.CreateServerRequest;

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
     * {@link org.hyperic.hq.hqapi1.types.ListResourcePrototypesResponse#getResourcePrototype()}.
     *
     * @see ResponseStatus#SUCCESS
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */    
    public ListResourcePrototypesResponse listResourcePrototypes() 
        throws IOException
    {   
        return doGet("resource/listResourcePrototypes.hqu",
                     new HashMap<String,String>(),
                     ListResourcePrototypesResponse.class);
    }

    /**
     * Find a {@link ResourcePrototype} by name.
     *
     * @param name The name of the ResourcePrototype to find
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the ResourcePrototypes is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetResourcePrototypeResponse#getResourcePrototype()}.
     *
     * @see ResponseStatus#SUCCESS
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public GetResourcePrototypeResponse getResourcePrototype(String name)
        throws IOException
    {
        Map<String,String> params = new HashMap<String,String>();
        params.put("name", name);
        return doGet("resource/getResourcePrototype.hqu",
                     params, GetResourcePrototypeResponse.class);
    }

    /**
     * Create a Platform {@link Resource} with the given name.<br><b>This API is
     * not yet availabile.  It will return an not implemented error.</b>
     *
     * @param agent The {@link org.hyperic.hq.hqapi1.types.Agent} which will service this platform.
     * @param type The resource prototype for the resource to be created.
     * @param name The name of the resource to create.
     * @param fqdn The FQDN for the platform.
     * @param configs The configuration for the platform.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the created Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.CreateResourceResponse#getResource()}.
     * 
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public CreateResourceResponse createPlatform(Agent agent,
                                                 ResourcePrototype type,
                                                 String name,
                                                 String fqdn,
                                                 Map configs)
        throws IOException
    {

        CreatePlatformRequest request = new CreatePlatformRequest();
        request.setAgent(agent);
        request.setPlatformPrototype(type);

        return doPost("resource/createPlatform.hqu", request,
                      CreateResourceResponse.class);
    }

    /**
     * Create a Server {@link Resource} with the given name.<br><b>This API is
     * not yet availabile.  It will return an not implemented error.</b>
     *
     * @param type The resource prototype for the resource to be created.
     * @param parent The parent resource for the created resource.
     * @param name The name of the resource to create.
     * @param installPath The install path for the server.
     * @param config The configuration for the server.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the created Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.CreateResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public CreateResourceResponse createServer(ResourcePrototype type,
                                               Resource parent,
                                               String name,
                                               String installPath,
                                               Map config)
        throws IOException
    {
        CreateServerRequest request = new CreateServerRequest();
        request.setParent(parent);
        request.setServerPrototype(type);

        return doPost("resource/createServer.hqu", request,
                      CreateResourceResponse.class);
    }

    /**
     * Create a Service {@link Resource} with the given name.
     *
     * @param type The {@link ResourcePrototype} for the resource to be created.
     * @param parent The parent {@link Resource} for the created resource.
     * @param name The name of the resource to create.
     * @param config The configuration for the service.
     * 
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the created Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.CreateResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public CreateResourceResponse createService(ResourcePrototype type,
                                                Resource parent,
                                                String name,
                                                Map<String,String> config)
        throws IOException
    {
        Resource service = new Resource();
        service.setName(name);
        for (String k : config.keySet()) {
            ResourceConfig c = new ResourceConfig();
            c.setKey(k);
            c.setValue(config.get(k));
            service.getResourceConfig().add(c);
        }

        CreateServiceRequest request = new CreateServiceRequest();
        request.setParent(parent);
        request.setService(service);
        request.setServicePrototype(type);

        return doPost("resource/createService.hqu", request,
                      CreateResourceResponse.class);
    }

    /**
     * Get a {@link Resource} by id.
     *
     * @param id The resource id to look up.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public GetResourceResponse getResource(Integer id)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id.toString());
        return doGet("resource/get.hqu", params,
                     GetResourceResponse.class);
    }

    /**
     * Get a {@link Resource} by it's platform id.
     *
     * @param id The platform id to look up.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public GetResourceResponse getResourceForPlatform(Integer id)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("platformId", id.toString());
        return doGet("resource/get.hqu", params,
                     GetResourceResponse.class);
    }

    /**
     * Get a {@link Resource} by it's platform name.
     *
     * @param name The platform name to look up.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public GetResourceResponse getResourceForPlatform(String name)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("platformName", name);
        return doGet("resource/get.hqu", params,
                     GetResourceResponse.class);
    }


    /**
     * Get a {@link Resource} by it's server id.
     *
     * @param id The platform id to look up.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public GetResourceResponse getResourceForServer(Integer id)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("serverId", id.toString());
        return doGet("resource/get.hqu", params,
                     GetResourceResponse.class);
    }

    /**
     * Get a {@link Resource} by it's service id.
     *
     * @param id The platform id to look up.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public GetResourceResponse getResourceForService(Integer id)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("serviceId", id.toString());
        return doGet("resource/get.hqu", params,
                     GetResourceResponse.class);
    }

    /**
     * Find the platform {@link Resource}s serviced by the given
     * {@link org.hyperic.hq.hqapi1.types.Agent}.
     *
     * @param agent The {@link org.hyperic.hq.hqapi1.types.Agent} to query.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the list of Resources are returned via
     * {@link org.hyperic.hq.hqapi1.types.FindResourcesResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     *
     */
    public FindResourcesResponse findResources(Agent agent)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("agentId", Integer.toString(agent.getId()));
        return doGet("resource/find.hqu", params,
                     FindResourcesResponse.class);
    }

    /**
     * Find {@link org.hyperic.hq.hqapi1.types.Resource}s of the
     * given {@link org.hyperic.hq.hqapi1.types.ResourcePrototype}.
     *
     * @param pt The {@link ResourcePrototype} to search for.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the list of Resources are returned via
     * {@link org.hyperic.hq.hqapi1.types.FindResourcesResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public FindResourcesResponse findResources(ResourcePrototype pt)
        throws IOException
    {
        Map<String,String> params = new HashMap<String, String>();
        params.put("prototype", pt.getName());
        return doGet("resource/find.hqu", params,
                     FindResourcesResponse.class);
    }

    /**
     * Find the child {@link org.hyperic.hq.hqapi1.types.Resource}s for the
     * given {@link org.hyperic.hq.hqapi1.types.Resource}.
     *
     * @param r The {@link org.hyperic.hq.hqapi1.types.Resource} to search for children.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the list of Resources are returned via
     * {@link org.hyperic.hq.hqapi1.types.FindResourcesResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public FindResourcesResponse findResourceChildren(Resource r)
        throws IOException
    {
        Map<String,String> params = new HashMap<String, String>();
        params.put("childrenOfId", Integer.toString(r.getId()));
        return doGet("resource/find.hqu", params,
                     FindResourcesResponse.class);
    }
}
