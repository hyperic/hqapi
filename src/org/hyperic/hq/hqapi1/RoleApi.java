package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.RoleRequest;
import org.hyperic.hq.hqapi1.types.SetUsersRequest;
import org.hyperic.hq.hqapi1.types.RolesRequest;
import org.hyperic.hq.hqapi1.types.UsersResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.RoleResponse;
import org.hyperic.hq.hqapi1.types.RolesResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
public class RoleApi extends BaseApi {

    public RoleApi(HQConnection connection) {
        super(connection);
    }

    /**
     * Find all {@link Role}s in the system.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of roles is returned.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public RolesResponse getRoles()
        throws IOException
    {
        return doGet("role/list.hqu", new HashMap<String, String[]>(),
                     RolesResponse.class);
    }

    /**
     * Get a {@link Role} by name.
     *
     * @param name The role name to search for.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Role by the given name is returned via
     * {@link org.hyperic.hq.hqapi1.types.RoleResponse#getRole()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public RoleResponse getRole(String name)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("name", new String[] { name });
        return doGet("role/get.hqu", params, RoleResponse.class);
    }

    /**
     * Get a {@link Role} by id.
     *
     * @param id The role id to look up.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Role by the given id is returned via
     * {@link org.hyperic.hq.hqapi1.types.RoleResponse#getRole()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public RoleResponse getRole(int id)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { String.valueOf(id) });
        return doGet("role/get.hqu", params, RoleResponse.class);
    }

    /**
     * Create a {@link Role}.
     *
     * @param role The {@link org.hyperic.hq.hqapi1.types.Role} to create.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Role by the given id is returned via
     * {@link org.hyperic.hq.hqapi1.types.RoleResponse#getRole()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */

    public RoleResponse createRole(Role role)
        throws IOException
    {
        RoleRequest request = new RoleRequest();
        request.setRole(role);
        return doPost("role/create.hqu", request, RoleResponse.class);
    }

    /**
     * Delete a {@link Role}.
     *
     * @param id The role id to delete
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if
     * the role was successfully removed.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse deleteRole(int id)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String,String[]>();
        params.put("id", new String[] { String.valueOf(id) });
        return doGet("role/delete.hqu", params, StatusResponse.class);
    }

    /**
     * Update a {@link Role}.
     *
     * @param role The role to update
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if
     * the role was successfully updated.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse updateRole(Role role)
        throws IOException
    {
        RoleRequest request = new RoleRequest();
        request.setRole(role);
        return doPost("role/update.hqu", request, StatusResponse.class);
    }

    /**
     * Sync a List of {@link Role}s.
     *
     * @param roles The List of roles to sync.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if
     * the roles were successuflly synced.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse syncRoles(List<Role> roles)
        throws IOException
    {
        RolesRequest request = new RolesRequest();
        request.getRole().addAll(roles);
        return doPost("role/sync.hqu", request, StatusResponse.class);
    }
}
