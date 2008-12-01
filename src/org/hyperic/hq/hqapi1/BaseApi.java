package org.hyperic.hq.hqapi1;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public abstract class BaseApi {
    private static final String BASE_URI = "/hqu/hqapi1/";

    private final HQConnection _conn;

    BaseApi(HQConnection conn) {
        _conn = conn;
    }
    
    /**
     * Call {@link HQConnection#doGet(String, Map, Class)} for the
     * specified controller/action
     * 
     * @param action  The name of the controller/action to GET from.  This is
     *  appended on to the BASE_URI and results in a path like
     * '/hqu/hqapi1/user/listUsers.hqu'
     * @param params  A map parameters to pass to the action.  Each HTTP
     * parameter may have multiple values.
     */
    protected <T> T doGet(String action, Map<String, String[]> params, 
                          Class<T> resultClass)
        throws IOException
    {
        return _conn.doGet(BASE_URI + action, params, resultClass);
    }

    /**
     * Call {@link HQConnection#doPost(String, Object, Class)} for the specified
     * controller/action via Post
     *
     * @param action  The name of the controller/action to POST to.  This is
     *                tacked on to the BASE_URI and results in a path
     *                like:  '/hqu/hqapi1/user/syncUsers.hqu'
     *                ex:  'resource/syncResources.hqu'
     */
    protected <T> T doPost(String action, Object o, Class<T> resultClass)
        throws IOException
    {
        return _conn.doPost(BASE_URI + action, o, resultClass);
    }
}
