package com.hyperic.hq.hqapi1;

import com.hyperic.hq.hqapi1.types.GetUserResponse;
import com.hyperic.hq.hqapi1.types.GetUsersResponse;
import com.hyperic.hq.hqapi1.types.CreateUserResponse;
import com.hyperic.hq.hqapi1.types.User;
import com.hyperic.hq.hqapi1.types.DeleteUserResponse;
import com.hyperic.hq.hqapi1.types.UpdateUserResponse;
import com.hyperic.hq.hqapi1.types.UpdateUserRequest;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * The Hyperic HQ User API.
 *
 * This class provides access to the users within the HQ system.  Each of the
 * methods in this class return response objects that wrap the result of the
 * method with a {@link com.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link com.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 *
 */
public class UserApi extends HQConnection {

    /**
     * @param host     The hostname of the HQ Server to connect to.
     * @param port     The port on the HQ server to connect to.
     * @param isSecure Set to true if connecting via SSL.
     * @param user     The user to connect as.
     * @param password The password for the given user.
     */
    UserApi(String host, int port, boolean isSecure, String user,
                   String password) {
        super(host, port, isSecure, user, password);
    }

    /**
     * Find a {@link com.hyperic.hq.hqapi1.types.User} by name.
     *
     * @param name The user name to search for.
     * @return On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the User by the given name is returned via
     * {@link com.hyperic.hq.hqapi1.types.GetUserResponse#getUser()}.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
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
        return (GetUserResponse)doGet("/hqu/hqapi1/user/get.hqu",
                                      params, GetUserResponse.class);
    }

    /**
     * Find all {@link com.hyperic.hq.hqapi1.types.User}s in the system.
     * @return On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of Users is returned.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
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
        return (GetUsersResponse)doGet("/hqu/hqapi1/user/list.hqu",
                                       NO_PARAMS, GetUsersResponse.class);
    }

    /**
     * Create a {@link com.hyperic.hq.hqapi1.types.User}.
     *
     * @param user The user to create.
     * @param password The password for this user.
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} is the
     * user was created successfully.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
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

        params.put("Name", user.getName());
        params.put("Password", password);
        params.put("FirstName", user.getFirstName());
        params.put("LastName", user.getLastName());
        params.put("EmailAddress", user.getEmailAddress());
        params.put("Active", Boolean.valueOf(user.isActive()).toString());
        params.put("Department", user.getDepartment());
        params.put("HtmlEmail", Boolean.valueOf(user.isActive()).toString());
        params.put("SMSAddress", user.getSMSAddress());

        return (CreateUserResponse)doGet("/hqu/hqapi1/user/create.hqu",
                                         params, CreateUserResponse.class);
    }

    /**
     * Delete a {@link User}
     *
     * @param user The user to delete.
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} is the
     * user was deleted successfully.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given user was not found in the system.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public DeleteUserResponse deleteUser(User user)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();

        params.put("Name", user.getName());

        return (DeleteUserResponse)doGet("/hqu/hqapi1/user/delete.hqu",
                                         params, DeleteUserResponse.class);
    }

    /**
     * Update a {@link User}
     *
     * @param user The user to update.
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * user was updated successfully.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
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

        return (UpdateUserResponse)doPost("/hqu/hqapi1/user/update.hqu",
                                          req, UpdateUserResponse.class);
    }
}
