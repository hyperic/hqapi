package com.hyperic.hq.hqapi1;

import com.hyperic.hq.hqapi1.types.GetQueueResponse;
import com.hyperic.hq.hqapi1.types.QueueApproveResponse;

import java.io.IOException;

/**
 * The Hyperic HQ Autodiscovery API.
 *
 * This class provides access to the auto discovery queue.  Each of the
 * methods in this class return {@link com.hyperic.hq.hqapi1.types.Response}
 * objects that wrap the result of the method with a
 * {@link com.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link com.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 *
 */
public class AutodiscoveryApi {

    private HQConnection _connection;

    public AutodiscoveryApi(HQConnection connection) {
        _connection = connection;
    }

    /**
     * Get all the entries in the auto-discovery queue.
     *
     * @return On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * a list of {@link com.hyperic.hq.hqapi1.types.AIPlatform} objects is
     * returned.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>PermissionDenied - The connected user does not have permission to sync a user.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public GetQueueResponse getQueue()
        throws IOException
    {
        return null;
    }

    /**
     * Approve a {@link com.hyperic.hq.hqapi1.types.AIPlatform} into the HQ
     * inventory.
     *
     * @param fqdn The {@link com.hyperic.hq.hqapi1.types.AIPlatform#getFqdn()}
     * to approve.
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the platform was approved into the inventory.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>PermissionDenied - The connected user does not have permission to sync a user.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public QueueApproveResponse approve(String fqdn)
        throws IOException
    {
        return null;
    }
}
