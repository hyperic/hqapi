package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.GetRolesResponse;

import java.io.IOException;
import java.util.HashMap;

/**
 * The Hyperic HQ Role API.
 *
 * This class provides access to the roles within the HQ system.  Each of the
 * methods in this class return {@link org.hyperic.hq.hqapi1.types.Response}
 * objects that wrap the result of the method with a
 * {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 *
 */
public class RoleApi {

    private HQConnection _connection;

    public RoleApi(HQConnection connection) {
        _connection = connection;
    }

    /**
     * Find all {@link org.hyperic.hq.hqapi1.types.Role}s in the system.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of roles is returned.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public GetRolesResponse getRoles()
        throws IOException
    {
        return _connection.doGet("/hqu/hqapi1/role/list.hqu",
                                 new HashMap<String, String>(),
                                 GetRolesResponse.class);
    }
}
