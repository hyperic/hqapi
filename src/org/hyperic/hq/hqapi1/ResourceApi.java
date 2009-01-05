package org.hyperic.hq.hqapi1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.CreateServiceRequest;
import org.hyperic.hq.hqapi1.types.ResourceConfig;
import org.hyperic.hq.hqapi1.types.CreatePlatformRequest;
import org.hyperic.hq.hqapi1.types.CreateServerRequest;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypesResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;

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
     * {@link org.hyperic.hq.hqapi1.types.ResourceResponse#getResource()}.
     * 
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourceResponse createPlatform(Agent agent,
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
                      ResourceResponse.class);
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
     * {@link org.hyperic.hq.hqapi1.types.ResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourceResponse createServer(ResourcePrototype type,
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
                      ResourceResponse.class);
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
                      ResourceResponse.class);
    }

    /**
     * Get a {@link Resource} by id.
     *
     * @param id The resource id to look up.
     * @param config Flag to indicate if resource configuration should be included.
     * @param children Flag to control whether child resources of this resource
     * will be included.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourceResponse getResource(int id, boolean config, boolean children)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(id) });
        params.put("config", new String[] { Boolean.toString(config) });
        params.put("children", new String[] { Boolean.toString(children)});
        return doGet("resource/get.hqu", params,
                     ResourceResponse.class);
    }

    /**
     * Get a {@link Resource} by it's platform name.
     *
     * @param name The platform name to look up.
     * @param config Flag to indicate if resource configuration should be included.
     * @param children Flag to control whether child resources of this resource
     * will be included.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Resource is returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourceResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourceResponse getPlatformResource(String name, boolean config,
                                                boolean children)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("platformName", new String[] { name });
        params.put("config", new String[] { Boolean.toString(config) });
        params.put("children", new String[] { Boolean.toString(children)});
        return doGet("resource/get.hqu", params,
                     ResourceResponse.class);
    }

    /**
     * Find the platform {@link Resource}s serviced by the given
     * {@link org.hyperic.hq.hqapi1.types.Agent}.
     *
     * @param agent The {@link org.hyperic.hq.hqapi1.types.Agent} to query.
     * @param config Flag to indicate if resource configuration should be included.
     * @param children Flag to control whether child resources of this resource
     * will be included.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the list of Resources are returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourcesResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     *
     */
    public ResourcesResponse getResources(Agent agent, boolean config,
                                          boolean children)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("agentId", new String[] { Integer.toString(agent.getId()) });
        params.put("config", new String[] { Boolean.toString(config) });
        params.put("children", new String[] { Boolean.toString(children)});
        return doGet("resource/find.hqu", params,
                     ResourcesResponse.class);
    }

    /**
     * Find {@link org.hyperic.hq.hqapi1.types.Resource}s of the
     * given {@link org.hyperic.hq.hqapi1.types.ResourcePrototype}.
     *
     * @param pt The {@link ResourcePrototype} to search for.
     * @param config Flag to indicate if resource configuration should be included.
     * @param children Flag to control whether child resources of this resource
     * will be included.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the list of Resources are returned via
     * {@link org.hyperic.hq.hqapi1.types.ResourcesResponse#getResource()}.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public ResourcesResponse getResources(ResourcePrototype pt, boolean config,
                                          boolean children)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String, String[]>();
        params.put("prototype", new String[] { pt.getName() });
        params.put("config", new String[] { Boolean.toString(config) });
        params.put("children", new String[] { Boolean.toString(children)});
        return doGet("resource/find.hqu", params,
                     ResourcesResponse.class);
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
}
