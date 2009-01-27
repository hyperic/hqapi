package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.EscalationResponse;
import org.hyperic.hq.hqapi1.types.EscalationsRequest;
import org.hyperic.hq.hqapi1.types.EscalationsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The Hyperic HQ Escalation API.
 * <br><br>
 * This class provides access to the escalations within the HQ system.  Each of
 * the methods in this class return response objects that wrap the result of the
 * method with a {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 */
public class EscalationApi extends BaseApi {

    EscalationApi(HQConnection connection) {
        super(connection);
    }
    
    /**
     * Find a {@link org.hyperic.hq.hqapi1.types.Escalation} by ID.
     *
     * @param id The escalation ID to search for.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Escalation by the given name is returned via
     * {@link org.hyperic.hq.hqapi1.types.EscalationResponse#getEscalation()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public EscalationResponse getEscalation(int id)
        throws IOException {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(id) });
        return doGet("escalation/get.hqu", params, EscalationResponse.class);
    }

    /**
     * Find a {@link org.hyperic.hq.hqapi1.types.Escalation} by name.
     *
     * @param name The escalation name to search for.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Escalation by the given name is returned via
     * {@link org.hyperic.hq.hqapi1.types.EscalationResponse#getEscalation()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public EscalationResponse getEscalation(String name)
        throws IOException {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("name", new String[] { name });
        return doGet("escalation/get.hqu",
                     params, EscalationResponse.class);
    }
    
    /**
     * Create an {@link Escalation}
     *
     * @param esc The escalation to create.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * escalation was created successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public EscalationResponse createEscalation(Escalation esc)
        throws IOException {
        
        EscalationsRequest req = new EscalationsRequest();
        req.getEscalation().add(esc);
        return doPost("escalation/create.hqu", req,
                      EscalationResponse.class);
    }

    /**
     * Update the properties of an {@link Escalation}
     *
     * @param esc The escalation to update.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * escalation was updated successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public EscalationResponse updateEscalation(Escalation esc)
        throws IOException {
        EscalationsRequest req = new EscalationsRequest();
        req.getEscalation().add(esc);
        return doPost("escalation/update.hqu",
                      req, EscalationResponse.class);
    }

    /**
     * Find all {@link org.hyperic.hq.hqapi1.types.Escalation}s in system.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a collection of Escalations is returned via
     * {@link org.hyperic.hq.hqapi1.types.EscalationsResponse#getEscalation()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public EscalationsResponse getEscalations()
        throws IOException {
        return doGet("escalation/list.hqu", new HashMap<String,String[]>(),
                     EscalationsResponse.class);
    }
    
    /**
     * Sync a collection of {@link Escalation}s by creating or updating
     *
     * @param escs The collections of escalations to sync.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * escalation was synchronized successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse syncEscalations(Collection<Escalation> escs)
        throws IOException {
        EscalationsRequest req = new EscalationsRequest();
        req.getEscalation().addAll(escs);
        return doPost("escalation/sync.hqu", req, StatusResponse.class);
    }

    /**
     * Delete an {@link Escalation}
     *
     * @param id The id of the escalation to delete.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * escalation was deleted successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse deleteEscalation(int id)
        throws IOException {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(id) });
        return doGet("escalation/delete.hqu",
                     params, StatusResponse.class);
    }
}
