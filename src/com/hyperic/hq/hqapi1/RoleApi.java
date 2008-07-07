package com.hyperic.hq.hqapi1;

/**
 * The Hyperic HQ Role API.
 *
 * This class provides access to the roles within the HQ system.  Each of the
 * methods in this class return {@link com.hyperic.hq.hqapi1.types.Response}
 * objects that wrap the result of the method with a
 * {@link com.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link com.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 *
 */
public class RoleApi {

    private HQConnection _connection;

    public RoleApi(HQConnection connection) {

    }
}
