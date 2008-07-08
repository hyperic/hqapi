package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.GetUserResponse;
import org.hyperic.hq.hqapi1.types.GetUsersResponse;
import org.hyperic.hq.hqapi1.types.CreateUserResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.DeleteUserResponse;
import org.hyperic.hq.hqapi1.types.UpdateUserResponse;
import org.hyperic.hq.hqapi1.types.UpdateUserRequest;
import org.hyperic.hq.hqapi1.types.SyncUsersResponse;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.SetRolesResponse;
import org.hyperic.hq.hqapi1.types.GetRolesResponse;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * The Hyperic HQ User API.
 *
 * This class provides access to the users within the HQ system.  Each of the
 * methods in this class return {@link org.hyperic.hq.hqapi1.types.Response}
 * objects that wrap the result of the method with a
 * {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 *
 */
public class UserApi {

    private HQConnection _connection;

    UserApi(HQConnection connection) {
        _connection = connection;
    }

    /**
     * Get a {@link org.hyperic.hq.hqapi1.types.User} by name.
     *
     * @param name The user name to search for.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the User by the given name is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetUserResponse#getUser()}.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given user was not found.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public GetUserResponse getUser(String name)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        return _connection.doGet("/hqu/hqapi1/user/get.hqu",
                                 params, GetUserResponse.class);
    }

    /**
     * Get a {@link org.hyperic.hq.hqapi1.types.User} by id.
     *
     * @param id The user id to look up.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the User by the given id is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetUserResponse#getUser()}.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given user was not found.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public GetUserResponse getUser(Integer id)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id.toString());
        return _connection.doGet("/hqu/hqapi1/user/get.hqu",
                                 params, GetUserResponse.class);
    }

    /**
     * Find all {@link org.hyperic.hq.hqapi1.types.User}s in the system.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of Users is returned.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     * </ul>
     * </p>
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetUsersResponse getUsers()
        throws IOException
    {
        return _connection.doGet("/hqu/hqapi1/user/list.hqu",
                                 new HashMap(), GetUsersResponse.class);
    }

    /**
     * Create a {@link org.hyperic.hq.hqapi1.types.User}.
     *
     * @param user The user to create.
     * @param password The password for this user.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * user was created successfully.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>InvalidParameters - All the required parameters in the User object were not supplied.
     *   <li>ObjectExists - The user by the given name already exists.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public CreateUserResponse createUser(User user, String password)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();

        params.put("name", user.getName());
        params.put("password", password);
        params.put("firstName", user.getFirstName());
        params.put("lastName", user.getLastName());
        params.put("emailAddress", user.getEmailAddress());
        params.put("active", Boolean.valueOf(user.isActive()).toString());
        params.put("department", user.getDepartment());
        params.put("htmlEmail", Boolean.valueOf(user.isActive()).toString());
        params.put("SMSAddress", user.getSMSAddress());

        return _connection.doGet("/hqu/hqapi1/user/create.hqu",
                                 params, CreateUserResponse.class);
    }

    /**
     * Delete a {@link User}
     *
     * @param id The user id to delete.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * user was deleted successfully.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given user was not found in the system.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * 
     * @throws IOException If a network error occurs while making the request.
     */
    public DeleteUserResponse deleteUser(Integer id)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();

        params.put("id", id.toString());

        return _connection.doGet("/hqu/hqapi1/user/delete.hqu",
                                 params, DeleteUserResponse.class);
    }

    /**
     * Update a {@link User}
     *
     * @param user The user to update.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * user was updated successfully.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated. 
     *   <li>ObjectNotFound - The given user was not found in the system.
     *   <li>PermissionDenied - The connected user does not have permission to modify this user.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public UpdateUserResponse updateUser(User user)
        throws IOException
    {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setUser(user);

        return _connection.doPost("/hqu/hqapi1/user/update.hqu",
                                  req, UpdateUserResponse.class);
    }

    /**
     * Sync a list of {@link User}s.
     *
     * @param users The list of users to sync.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * user was updated successfully.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>PermissionDenied - The connected user does not have permission to sync a user.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public SyncUsersResponse syncUsers(List<User> users)
        throws IOException
    {
        return null;
    }

    /**
     * Set a list of {@link Role}s for a {@link User}.  Any previous roles
     * associated with this user will be removed.
     *
     * @param user The user to set roles for.
     * @param roles The list of roles to set.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * user was updated successfully.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - A given user or role was not found in the system. 
     *   <li>PermissionDenied - The connected user does not have permission to modify the user.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public SetRolesResponse setRoles(User user, List<Role> roles)
        throws IOException
    {
        return null;
    }

    /**
     * Get the list of {@link Role}s assigned to a {@link User}.
     *
     * @param user The user to get roles for.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * a list of Roles is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetRolesResponse#getRole()}.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - A given user was not found in the system.
     *   <li>PermissionDenied - The connected user does not have permission to list roles for this user.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public GetRolesResponse getRoles(User user)
        throws IOException
    {
        return null;
    }
}
