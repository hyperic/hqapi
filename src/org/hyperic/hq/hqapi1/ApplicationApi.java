package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.ApplicationsResponse;
import org.hyperic.hq.hqapi1.types.ApplicationResponse;
import org.hyperic.hq.hqapi1.types.ApplicationRequest;
import org.hyperic.hq.hqapi1.types.Application;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * The Hyperic HQ Application API.
 * <br><br>
 * This class provides access to the applications within the HQ system.  Each of
 * the methods in this class return response objects that wrap the result of the
 * method with a {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 */
public class ApplicationApi extends BaseApi {

    ApplicationApi(HQConnection conn) {
        super(conn);
    }

    /**
     * Get all {@link org.hyperic.hq.hqapi1.types.Application}'s.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * Applications are returned via
     * {@link org.hyperic.hq.hqapi1.types.ApplicationsResponse#getApplication()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ApplicationsResponse listApplications()
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        return doGet("application/list.hqu", params, ApplicationsResponse.class);
    }

    /**
     * Create an {@link org.hyperic.hq.hqapi1.types.Application}.
     *
     * @param app The Application to create.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the created Application is returned via
     * {@link org.hyperic.hq.hqapi1.types.ApplicationResponse#getApplication()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ApplicationResponse createApplication(Application app)
        throws IOException
    {
        ApplicationRequest appRequest = new ApplicationRequest();
        appRequest.setApplication(app);
        return doPost("application/create.hqu", appRequest,
                      ApplicationResponse.class);
    }

    /**
     * Update an {@link org.hyperic.hq.hqapi1.types.Application}.
     *
     * @param app The Application to update.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the updated Application is returned via
     * {@link org.hyperic.hq.hqapi1.types.ApplicationResponse#getApplication()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ApplicationResponse updateApplication(Application app)
        throws IOException
    {
        ApplicationRequest appRequest = new ApplicationRequest();
        appRequest.setApplication(app);
        return doPost("application/update.hqu", appRequest,
                      ApplicationResponse.class);
    }

    /**
     * Delete an {@link org.hyperic.hq.hqapi1.types.Application}.
     *
     * @param id The id of the Application to delete.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if
     * the Application was successfully deleted.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse deleteApplication(int id)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(id)});
        return doGet("application/delete.hqu", params, StatusResponse.class);
    }
}
