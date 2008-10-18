package org.hyperic.hq.hqapi1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.Config;
import org.hyperic.hq.hqapi1.types.CreateResourceResponse;
import org.hyperic.hq.hqapi1.types.GetResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ListResourcePrototypesResponse;
import org.hyperic.hq.hqapi1.types.Platform;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.SyncEscalationResponse;
import org.hyperic.hq.hqapi1.types.SyncPlatformRequest;
import org.hyperic.hq.hqapi1.types.SyncPlatformResponse;

/**
 * The ResourceApi deals with {@link ResourcePrototype}s and {@link Resource}s.
 *
 * A ResourcePrototype is a class of Resource
 *    ex:  Linux, OS X, FileServer File, Nagios Check
 *
 * A Resource is an instance of a prototype:
 *    ex:  google.com port 80 check,  Local Tomcat Instance   
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
     * Create a Platform {@link Resource} with the given name.
     *
     * @param agent The agent which will service this platform.
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
        Platform plat = new Platform();
        plat.setResourceType(type.getName());
        plat.setAgent(agent);
        plat.setName(name);
        plat.setFqdn(fqdn);
        
        for (Iterator it = configs.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            Config config = new Config();
            config.setKey(entry.getKey().toString());
            config.setValue(entry.getValue().toString());
        }
        
        SyncPlatformRequest req = new SyncPlatformRequest();
        req.setPlatform(plat);
        return doPost("/hqu/hqapi1/resource/syncPlatform.hqu", req,
                      CreateResourceResponse.class);
    }

    /**
     * Create a Server {@link Resource} with the given name.
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
        return null;
    }

    /**
     * Create a Service {@link Resource} with the given name.
     *
     * @param type The resource prototype for the resource to be created.
     * @param parent The parent resource for the created resource.
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
                                                Map config)
        throws IOException
    {
        return null;
    }
}
