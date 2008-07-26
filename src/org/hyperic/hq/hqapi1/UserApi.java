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
import org.hyperic.hq.hqapi1.types.SyncUsersRequest;
import org.hyperic.hq.hqapi1.types.ChangePasswordResponse;

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
     * Get a {@link User} by name.
     *
     * @param name The user name to search for.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the User by the given name is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetUserResponse#getUser()}.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     *
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
     * Get a {@link User} by id.
     *
     * @param id The user id to look up.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the User by the given id is returned via
     * {@link org.hyperic.hq.hqapi1.types.GetUserResponse#getUser()}.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     *
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
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of Users is returned.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetUsersResponse getUsers()
        throws IOException
    {
        return _connection.doGet("/hqu/hqapi1/user/list.hqu",
                                 new HashMap<String,String>(),
                                 GetUsersResponse.class);
    }

    /**
     * Create a {@link User}.
     *
     * @param user The user to create.
     * @param password The password for this user.
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * the created user is returned via
     * {@link org.hyperic.hq.hqapi1.types.CreateUserResponse#getUser()}.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_EXISTS
     *
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
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
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
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#PERMISSION_DENIED
     *
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
     * users were updated successfully.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @see org.hyperic.hq.hqapi1.ErrorCode#PERMISSION_DENIED
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public SyncUsersResponse syncUsers(List<User> users)
        throws IOException
    {
        SyncUsersRequest request = new SyncUsersRequest();
        request.getUser().addAll(users);

        return _connection.doPost("/hqu/hqapi1/user/sync.hqu", request,
                                  SyncUsersResponse.class);
    }

    /**
     * Change the password for the given {@link User}.
     *
     * @param user The user's password to change.
     * @param password The new password.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * user's password was changed successfully.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#PERMISSION_DENIED
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ChangePasswordResponse changePassword(User user, String password)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();

        params.put("id", String.valueOf(user.getId()));
        params.put("password", password);

        return _connection.doGet("/hqu/hqapi1/user/changePassword.hqu",
                                 params, ChangePasswordResponse.class);
    }
}
