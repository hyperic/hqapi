package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsRequest;
import org.hyperic.hq.hqapi1.types.Escalation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
     * @param alertNameFilter Filter returned definitions by definition name
     * using the given regular expression.  A value of null will result in no
     * filtering being performed.
     * @param resourceNameFilter Filter returned definitions by resource name
     * using the given regular expression.  A value of null will result in no
     * filtering being performed.
     * @param groupName Filter returned definitions such that only definitions
     * on resources belonging to the given group are returned.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of AlertDefinitions are returned.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public AlertDefinitionsResponse getAlertDefinitions(boolean excludeTypeBased,
                                                        String alertNameFilter,
                                                        String resourceNameFilter,
                                                        String groupName)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("excludeTypeBased", new String[] { Boolean.toString(excludeTypeBased)});
        if (alertNameFilter != null) {
            params.put("alertNameFilter", new String[] { alertNameFilter });
        }
        if (resourceNameFilter != null) {
            params.put("resourceNameFilter", new String[] { resourceNameFilter });
        }
        if (groupName != null) {
            params.put("groupName", new String[] { groupName });
        }

        return doGet("alertdefinition/listDefinitions.hqu", params,
                     AlertDefinitionsResponse.class);
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
        return getAlertDefinitions(excludeTypeBased, null, null, null);
    }

    /**
     * Find all {@link org.hyperic.hq.hqapi1.types.AlertDefinition}s based on
     * the given parent resource type alert
     *
     * @param parent The parent AlertDefinition
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of AlertDefinitions are returned.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public AlertDefinitionsResponse getAlertDefinitions(AlertDefinition parent)
        throws IOException {

        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("parentId", new String[] { Integer.toString(parent.getId()) });

        return doGet("alertdefinition/listDefinitions.hqu", params,
                     AlertDefinitionsResponse.class); 
    }


    /**
     * Find all {@link org.hyperic.hq.hqapi1.types.AlertDefinition}s that use
     * the given escalation.
     *
     * @param e The {@link Escalation} to filter by
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of AlertDefinitions are returned.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public AlertDefinitionsResponse getAlertDefinitions(Escalation e)
        throws IOException {

        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("escalationId", new String[] { Integer.toString(e.getId()) });

        return doGet("alertdefinition/listDefinitions.hqu", params,
                     AlertDefinitionsResponse.class);
    }

    /**
     * Find all type based {@link org.hyperic.hq.hqapi1.types.AlertDefinition}s in the system.
     *
     * @param excludeIds If specified the returned {@link org.hyperic.hq.hqapi1.types.AlertDefinition}s
     * will not have id's included.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of AlertDefinitions are returned.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public AlertDefinitionsResponse getTypeAlertDefinitions(boolean excludeIds)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("excludeIds", new String[] { Boolean.toString(excludeIds)});

        return doGet("alertdefinition/listTypeDefinitions.hqu", params,
                     AlertDefinitionsResponse.class);
    }

    /**
     * Delete an {@link org.hyperic.hq.hqapi1.types.AlertDefinition}
     *
     * @param id The alert definition id to delete.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * definition was deleted successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse deleteAlertDefinition(int id)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();

        params.put("id", new String[] { Integer.toString(id) });

        return doGet("alertdefinition/delete.hqu", params, StatusResponse.class);
    }

    /**
     * Sync a list of {@link org.hyperic.hq.hqapi1.types.AlertDefinition}s.
     *
     * @param definitions The list of alert definitions to sync.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * this list of synced AlertDefinitions are returned.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public AlertDefinitionsResponse syncAlertDefinitions(List<AlertDefinition> definitions)
        throws IOException
    {
        AlertDefinitionsRequest request = new AlertDefinitionsRequest();
        request.getAlertDefinition().addAll(definitions);

        return doPost("alertdefinition/sync.hqu", request,
                      AlertDefinitionsResponse.class);
    }
}
