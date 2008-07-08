package org.hyperic.hq.hqapi1;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.types.CreateEscalationResponse;
import org.hyperic.hq.hqapi1.types.DeleteEscalationResponse;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.GetEscalationResponse;
import org.hyperic.hq.hqapi1.types.ListEscalationsResponse;
import org.hyperic.hq.hqapi1.types.SyncEscalationResponse;
import org.hyperic.hq.hqapi1.types.UpdateEscalationResponse;

/**
 * The Hyperic HQ Escalation API.
 *
 * This class provides access to the escalations within the HQ system.  Each of
 * the methods in this class return response objects that wrap the result of the
 * method with a {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 *
 */
public class EscalationApi {

    private HQConnection _connection;

    EscalationApi(HQConnection connection) {
        _connection = connection;
    }
    
    /**
     * Find a {@link org.hyperic.hq.hqapi1.types.Escalation} by ID.
     *
     * @param id The escalation ID to search for.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Escalation by the given name is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetEscalationResponse#getEscalation()}.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
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
     * Find a {@link org.hyperic.hq.hqapi1.types.Escalation} by name.
     *
     * @param name The escalation name to search for.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Escalation by the given name is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetEscalationResponse#getEscalation()}.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
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
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * escalation was created successfully.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
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
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * escalation was updated successfully.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
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
     * Find all {@link org.hyperic.hq.hqapi1.types.Escalation}s in system.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a collection of Escalations is returned via
     * {@link org.hyperic.hq.hqapi1.types.ListEscalationsResponse#getEscalation()}.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
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
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * escalation was synchronized successfully.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
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
     * @param id The ID of the escalation to delete.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * escalation was deleted successfully.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
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
    public DeleteEscalationResponse deleteEscalation(Integer id)
        throws IOException {
        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("id", id);
        return _connection.doGet("/hqu/hqapi1/escalation/delete.hqu",
                                 params, DeleteEscalationResponse.class);
    }

}
