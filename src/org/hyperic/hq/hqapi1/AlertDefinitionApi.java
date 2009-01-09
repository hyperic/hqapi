package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;

import java.io.IOException;
import java.util.HashMap;

/**
 * The Hyperic HQ Alert Definition API.
 * <br><br>
 * This class provides access to the alert definitions within the HQ system.  Each of the
 * methods in this class return {@link org.hyperic.hq.hqapi1.types.Response}
 * objects that wrap the result of the method with a
 * {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 */
public class AlertDefinitionApi extends BaseApi {

    AlertDefinitionApi(HQConnection conn) {
        super(conn);
    }

    /**
     * Find all {@link org.hyperic.hq.hqapi1.types.AlertDefinition}s in the system.
     *
     * @param excludeTypeBased Flag to control whether instances of type based
     * alerts will be included.
     * 
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of AlertDefinitions are returned.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public AlertDefinitionsResponse getAlertDefinitions(boolean excludeTypeBased)
        throws IOException
    {
        HashMap<String,String[]> params = new HashMap<String,String[]>();
        params.put("excludeTypeBased", new String[] { Boolean.toString(excludeTypeBased)});

        return doGet("alertdefinition/listDefinitions.hqu", params,
                     AlertDefinitionsResponse.class);
    }

    /**
     * Find all type based {@link org.hyperic.hq.hqapi1.types.AlertDefinition}s in the system.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of AlertDefinitions are returned.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public AlertDefinitionsResponse getTypeAlertDefinitions()
        throws IOException
    {
        HashMap<String,String[]> params = new HashMap<String,String[]>();

        return doGet("alertdefinition/listTypeDefinitions.hqu", params,
                     AlertDefinitionsResponse.class);
    }
}
