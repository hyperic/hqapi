package org.hyperic.hq.hqapi1;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hyperic.hq.hqapi1.types.CreateEscalationResponse;
import org.hyperic.hq.hqapi1.types.DeleteEscalationResponse;
import org.hyperic.hq.hqapi1.types.EmailAction;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.FullEscalationAction;
import org.hyperic.hq.hqapi1.types.GetEscalationResponse;
import org.hyperic.hq.hqapi1.types.ListEscalationsResponse;
import org.hyperic.hq.hqapi1.types.SuppressAction;
import org.hyperic.hq.hqapi1.types.SyncEscalationResponse;
import org.hyperic.hq.hqapi1.types.SyncEscalationsRequest;
import org.hyperic.hq.hqapi1.types.UpdateEscalationResponse;

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
     * {@link org.hyperic.hq.hqapi1.types.GetEscalationResponse#getEscalation()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetEscalationResponse getEscalation(int id)
        throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", Integer.toString(id));
        return doGet("escalation/get.hqu", params, GetEscalationResponse.class);
    }

    /**
     * Find a {@link org.hyperic.hq.hqapi1.types.Escalation} by name.
     *
     * @param name The escalation name to search for.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Escalation by the given name is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetEscalationResponse#getEscalation()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetEscalationResponse getEscalation(String name)
        throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        return doGet("escalation/get.hqu",
                     params, GetEscalationResponse.class);
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
    public CreateEscalationResponse createEscalation(Escalation esc)
        throws IOException {
        
        SyncEscalationsRequest req = new SyncEscalationsRequest();
        req.getEscalation().add(esc);
        return doPost("escalation/create.hqu", req,
                      CreateEscalationResponse.class);
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
    public UpdateEscalationResponse updateEscalation(Escalation esc)
        throws IOException {
        SyncEscalationsRequest req = new SyncEscalationsRequest();
        req.getEscalation().add(esc);
        return doPost("escalation/update.hqu",
                      req, UpdateEscalationResponse.class);
    }

    /**
     * Find all {@link org.hyperic.hq.hqapi1.types.Escalation}s in system.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a collection of Escalations is returned via
     * {@link org.hyperic.hq.hqapi1.types.ListEscalationsResponse#getEscalation()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ListEscalationsResponse listEscalations()
        throws IOException {
        return doGet("escalation/list.hqu", new HashMap<String,String>(),
                     ListEscalationsResponse.class);
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
    public SyncEscalationResponse syncEscalations(Collection<Escalation> escs)
        throws IOException {
        SyncEscalationsRequest req = new SyncEscalationsRequest();
        req.getEscalation().addAll(escs);
        return doPost("escalation/sync.hqu", req, SyncEscalationResponse.class);
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
    public DeleteEscalationResponse deleteEscalation(int id)
        throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", Integer.toString(id));
        return doGet("escalation/delete.hqu",
                     params, DeleteEscalationResponse.class);
    }

    /**
     * Create a new EmailAction
     * 
     * @return an EmailAction
     */
    public FullEscalationAction createEmailAction() {
        FullEscalationAction ea = new FullEscalationAction();
        ea.setActionType(new EmailAction().getTypeName());
        return ea;
    }
    
    /**
     * Create a new SuppressAction
     *
     * @return a SuppressAction
     */
    public FullEscalationAction createSuppressAction() {
        FullEscalationAction sa = new FullEscalationAction();
        sa.setActionType(new SuppressAction().getTypeName());
        return sa;
    }
}
