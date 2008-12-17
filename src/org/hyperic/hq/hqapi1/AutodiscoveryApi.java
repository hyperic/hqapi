package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.QueueResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.io.IOException;
import java.util.HashMap;

/**
 * The Hyperic HQ Autodiscovery API.
 * <br><br>
 * This class provides access to the auto discovery queue.  Each of the
 * methods in this class return {@link org.hyperic.hq.hqapi1.types.Response}
 * objects that wrap the result of the method with a
 * {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 *
 */
public class AutodiscoveryApi extends BaseApi {

    public AutodiscoveryApi(HQConnection connection) {
        super(connection);
    }

    /**
     * Get all the entries in the auto-discovery queue.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * a list of {@link org.hyperic.hq.hqapi1.types.AIPlatform} objects is
     * returned via {@link org.hyperic.hq.hqapi1.types.QueueResponse#getAIPlatform()}
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public QueueResponse getQueue()
        throws IOException
    {
        return doGet("autodiscovery/getQueue.hqu", new HashMap<String,String[]>(),
                     QueueResponse.class);
    }

    /**
     * Approve a {@link org.hyperic.hq.hqapi1.types.AIPlatform} into the HQ
     * inventory.
     *
     * @param id The {@link org.hyperic.hq.hqapi1.types.AIPlatform#getId()}
     * to approve.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the platform was approved into the inventory.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public StatusResponse approve(int id)
        throws IOException
    {
        HashMap<String, String[]> params = new HashMap<String, String[]>();

        params.put("id", new String[] { String.valueOf(id) });

        return doGet("autodiscovery/approve.hqu", params, StatusResponse.class);
    }
}
