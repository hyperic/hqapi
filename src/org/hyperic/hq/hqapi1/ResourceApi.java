package org.hyperic.hq.hqapi1;

import java.io.IOException;
import java.util.HashMap;

import org.hyperic.hq.hqapi1.types.ListResourcePrototypesResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResponseStatus;

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
}
