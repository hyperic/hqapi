package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.GetRolesResponse;
import org.hyperic.hq.hqapi1.types.GetRoleResponse;
import org.hyperic.hq.hqapi1.types.CreateRoleResponse;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.DeleteRoleResponse;
import org.hyperic.hq.hqapi1.types.UpdateRoleResponse;
import org.hyperic.hq.hqapi1.types.SyncRolesResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.SetUsersResponse;
import org.hyperic.hq.hqapi1.types.CreateRoleRequest;
import org.hyperic.hq.hqapi1.types.UpdateRoleRequest;
import org.hyperic.hq.hqapi1.types.SetUsersRequest;
import org.hyperic.hq.hqapi1.types.GetUsersResponse;
import org.hyperic.hq.hqapi1.types.SyncRolesRequest;

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
public class RoleApi {

    private HQConnection _connection;

    public RoleApi(HQConnection connection) {
        _connection = connection;
    }

    /**
     * Find all {@link Role}s in the system.
     *
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

    /**
     * Get a {@link Role} by name.
     *
     * @param name The role name to search for.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Role by the given name is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetRoleResponse#getRole()}.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetRoleResponse getRole(String name)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        return _connection.doGet("/hqu/hqapi1/role/get.hqu",
                                 params, GetRoleResponse.class);
    }

    /**
     * Get a {@link Role} by id.
     *
     * @param id The role id to look up.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Role by the given id is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetRoleResponse#getRole()}.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetRoleResponse getRole(int id)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", String.valueOf(id));
        return _connection.doGet("/hqu/hqapi1/role/get.hqu",
                                 params, GetRoleResponse.class);
    }

    /**
     * Create a {@link Role}.
     *
     * @param role The role to create
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the Role by the given id is returned via
     * {@link org.hyperic.hq.hqapi1.types.CreateRoleResponse#getRole()}.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @see org.hyperic.hq.hqapi1.ErrorCode#PERMISSION_DENIED
     * @see org.hyperic.hq.hqapi1.ErrorCode#UNEXPECTED_ERROR
     *
     * @throws IOException If a network error occurs while making the request.
     */

    public CreateRoleResponse createRole(Role role)
        throws IOException
    {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setRole(role);
        return _connection.doPost("/hqu/hqapi1/role/create.hqu", request,
                                  CreateRoleResponse.class);
    }

    /**
     * Delete a {@link Role}.
     *
     * @param id The role id to delete
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if
     * the role was successfully removed.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#UNEXPECTED_ERROR
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public DeleteRoleResponse deleteRole(int id)
        throws IOException
    {
        Map<String, String> params = new HashMap<String,String>();
        params.put("id", String.valueOf(id));
        return _connection.doGet("/hqu/hqapi1/role/delete.hqu", params,
                                 DeleteRoleResponse.class);
    }

    /**
     * Update a {@link Role}.
     *
     * @param role The role to update
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if
     * the role was successfully updated.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_EXISTS
     * @see org.hyperic.hq.hqapi1.ErrorCode#PERMISSION_DENIED 
     * @see org.hyperic.hq.hqapi1.ErrorCode#UNEXPECTED_ERROR
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public UpdateRoleResponse updateRole(Role role)
        throws IOException
    {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setRole(role);
        return _connection.doPost("/hqu/hqapi1/role/update.hqu", request,
                                  UpdateRoleResponse.class);
    }

    /**
     * Sync a List of {@link Role}s.
     *
     * @param roles The List of roles to sync.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if
     * the roles were successuflly synced.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public SyncRolesResponse syncRoles(List<Role> roles)
        throws IOException
    {
        SyncRolesRequest request = new SyncRolesRequest();
        request.getRole().addAll(roles);
        return _connection.doPost("/hqu/hqapi1/role/sync.hqu", request,
                                  SyncRolesResponse.class);
    }

    /**
     * Set the {@link User}s for the given {@link Role}.
     *
     * @param role The role to add users for.
     * @param users The list of Users to add to the Role.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if
     * the users were sucessfully assigned to the role.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @see org.hyperic.hq.hqapi1.ErrorCode#PERMISSION_DENIED
     * @see org.hyperic.hq.hqapi1.ErrorCode#UNEXPECTED_ERROR
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public SetUsersResponse setUsers(Role role, List<User> users)
        throws IOException
    {
        SetUsersRequest request = new SetUsersRequest();
        request.setRole(role);
        request.getUser().addAll(users);
        return _connection.doPost("/hqu/hqapi1/role/setUsers.hqu", request,
                                  SetUsersResponse.class);
    }

    /**
     * Get the {@link User}s for the given {@link Role}.
     *
     * @param role The role to get users for.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * the list of users is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetUsersResponse#getUser()}
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetUsersResponse getUsers(Role role)
        throws IOException
    {
        Map<String, String> params = new HashMap<String,String>();
        params.put("id", String.valueOf(role.getId()));
        return _connection.doGet("/hqu/hqapi1/role/getUsers.hqu", params,
                                 GetUsersResponse.class);
    }
}
