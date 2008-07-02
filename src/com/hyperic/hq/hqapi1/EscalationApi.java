package com.hyperic.hq.hqapi1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.hyperic.hq.hqapi1.types.DeleteEscalationResponse;
import com.hyperic.hq.hqapi1.types.Escalation;
import com.hyperic.hq.hqapi1.types.GetEscalationResponse;
import com.hyperic.hq.hqapi1.types.SyncEscalationResponse;

/**
 * The Hyperic HQ Escalation API.
 *
 * This class provides access to the escalations within the HQ system.  Each of
 * the methods in this class return response objects that wrap the result of the
 * method with a {@link com.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link com.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 *
 */
public class EscalationApi {

    private HQConnection _connection;

    EscalationApi(HQConnection connection) {
        _connection = connection;
    }
    
    /**
     * Find a {@link com.hyperic.hq.hqapi1.types.Escalation} by name.
     *
     * @param name The escalation name to search for.
     * @return On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Escalation by the given name is returned via
     * {@link com.hyperic.hq.hqapi1.types.GetEscalationResponse#getEscalationByName()}.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given escalation was not found.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public GetEscalationResponse getEscalationByName(String name)
        throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        return _connection.doGet("/hqu/hqapi1/escalation/get.hqu",
                                 params, GetEscalationResponse.class);
    }
    
    /**
     * Sync an {@link Escalation} by creating or updating
     *
     * @param esc The escalation name to sync.
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * escalation was synchronized successfully.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public SyncEscalationResponse syncEscalation(Escalation esc)
        throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        return _connection.doGet("/hqu/hqapi1/escalation/sync.hqu",
                                 params, SyncEscalationResponse.class);
    }

    /**
     * Delete an {@link Escalation}
     *
     * @param name The escalation name to delete.
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * escalation was deleted successfully.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given escalation was not found in the system.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public DeleteEscalationResponse deleteEscalation(String name)
        throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        return _connection.doGet("/hqu/hqapi1/escalation/delete.hqu",
                                 params, DeleteEscalationResponse.class);
    }

}
