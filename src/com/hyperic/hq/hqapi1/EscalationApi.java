package com.hyperic.hq.hqapi1;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.hyperic.hq.hqapi1.types.CreateEscalationResponse;
import com.hyperic.hq.hqapi1.types.DeleteEscalationResponse;
import com.hyperic.hq.hqapi1.types.Escalation;
import com.hyperic.hq.hqapi1.types.GetEscalationResponse;
import com.hyperic.hq.hqapi1.types.ListEscalationsResponse;
import com.hyperic.hq.hqapi1.types.SyncEscalationResponse;
import com.hyperic.hq.hqapi1.types.UpdateEscalationResponse;

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
     * Find a {@link com.hyperic.hq.hqapi1.types.Escalation} by ID.
     *
     * @param id The escalation ID to search for.
     * @return On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Escalation by the given name is returned via
     * {@link com.hyperic.hq.hqapi1.types.GetEscalationResponse#getEscalation()}.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given escalation was not found.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public GetEscalationResponse getEscalation(Integer id)
        throws IOException {
        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("id", id);
        return _connection.doGet("/hqu/hqapi1/escalation/get.hqu",
                                 params, GetEscalationResponse.class);
    }

    /**
     * Find a {@link com.hyperic.hq.hqapi1.types.Escalation} by name.
     *
     * @param name The escalation name to search for.
     * @return On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Escalation by the given name is returned via
     * {@link com.hyperic.hq.hqapi1.types.GetEscalationResponse#getEscalation()}.
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
    public GetEscalationResponse getEscalation(String name)
        throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        return _connection.doGet("/hqu/hqapi1/escalation/get.hqu",
                                 params, GetEscalationResponse.class);
    }
    
    /**
     * Create an {@link Escalation}
     *
     * @param esc The escalation to create.
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * escalation was created successfully.
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
    public CreateEscalationResponse createEscalation(Escalation esc)
        throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        return _connection.doGet("/hqu/hqapi1/escalation/update.hqu",
                                 params, CreateEscalationResponse.class);
    }

    /**
     * Update the properties of an {@link Escalation}
     *
     * @param esc The escalation to update.
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * escalation was updated successfully.
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
    public UpdateEscalationResponse updateEscalation(Escalation esc)
        throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        return _connection.doGet("/hqu/hqapi1/escalation/update.hqu",
                                 params, UpdateEscalationResponse.class);
    }

    /**
     * Find all {@link com.hyperic.hq.hqapi1.types.Escalation}s in system.
     *
     * @return On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a collection of Escalations is returned via
     * {@link com.hyperic.hq.hqapi1.types.ListEscalationsResponse#getEscalations()}.
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
    public ListEscalationsResponse listEscalations()
        throws IOException {
        return _connection.doGet("/hqu/hqapi1/escalation/list.hqu",
                                 null, ListEscalationsResponse.class);
    }
    
    /**
     * Sync a collection of {@link Escalation}s by creating or updating
     *
     * @param escs The collections of escalations to sync.
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
    public SyncEscalationResponse syncEscalations(Collection<Escalation> escs)
        throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        return _connection.doGet("/hqu/hqapi1/escalation/sync.hqu",
                                 params, SyncEscalationResponse.class);
    }

    /**
     * Delete an {@link Escalation}
     *
     * @param esc The escalation to delete.
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
    public DeleteEscalationResponse deleteEscalation(Escalation esc)
        throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        return _connection.doGet("/hqu/hqapi1/escalation/delete.hqu",
                                 params, DeleteEscalationResponse.class);
    }

}
