package com.hyperic.hq.hqapi1;

import com.hyperic.hq.hqapi1.jaxb.GetUsersResponse;
import com.hyperic.hq.hqapi1.jaxb.GetUserResponse;
import com.hyperic.hq.hqapi1.jaxb.CreateUserResponse;
import com.hyperic.hq.hqapi1.jaxb.User;
import com.hyperic.hq.hqapi1.jaxb.DeleteUserResponse;
import com.hyperic.hq.hqapi1.jaxb.SyncUserResponse;
import com.hyperic.hq.hqapi1.jaxb.SyncUserRequest;

import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

/**
 * The Hyperic HQ API.
 *
 * <p>
 * The methods in this API return response objects that wrap the result along
 * with a status and error code.  All response objects are guaranteed to have
 * the following 2 methods.
 *
 * <ul>
 *   <li>getStatus() - A {@link com.hyperic.hq.hqapi1.jaxb.ResponseStatus}
 * indicating the success or failure of the call.
 *   <li>getError() - If the return status is failure, a
 * {@link com.hyperic.hq.hqapi1.jaxb.ServiceError} is returned providing the
 * error code and reason for the failure.
 * </ul>
 * </p>
 *
 */
public class HQApi extends HQConnection {

    private static final Map NO_PARAMS = new HashMap();

    /**
     * @param host The hostname of the HQ Server to connect to.
     * @param port The port on the HQ server to connect to.
     * @param isSecure Set to true if connecting via SSL.
     * @param user The user to connect as.
     * @param password The password for the given user.
     */
    public HQApi(String host, int port, boolean isSecure, String user,
                 String password) {
        super(host, port, isSecure, user, password);
    }

    /**
     * Find a {@link User} by name.
     *
     * @param name The user name to search for.
     * @return On {@link com.hyperic.hq.hqapi1.jaxb.ResponseStatus#SUCCESS},
     * the User by the given name is returned.
     *
     * On {@link com.hyperic.hq.hqapi1.jaxb.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>ObjectNotFound - The given user was not found.
     * </ul>
     * </p>
     * @throws IOException
     * @throws JAXBException
     */
    public GetUserResponse getUser(String name)
        throws IOException, JAXBException
    {
        Map params = new HashMap();
        params.put("name", name);
        return (GetUserResponse)doGet("/hqu/hqapi1/user/get.hqu",
                                      params, GetUserResponse.class);
    }

    /**
     * Find all {@link User}s in the system.
     * @return On {@link com.hyperic.hq.hqapi1.jaxb.ResponseStatus#SUCCESS},
     * a list of users is returned.
     *
     * @throws IOException
     * @throws JAXBException
     */
    public GetUsersResponse getUsers()
        throws IOException, JAXBException
    {
        return (GetUsersResponse)doGet("/hqu/hqapi1/user/list.hqu",
                                       NO_PARAMS, GetUsersResponse.class);
    }

    /**
     * Create a {@link User}.
     *
     * @param user The user to create.
     * @param password The password for this user.
     * @return {@link com.hyperic.hq.hqapi1.jaxb.ResponseStatus#SUCCESS} is the
     * user was created successfully.
     *
     * On {@link com.hyperic.hq.hqapi1.jaxb.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>InvalidParameters - All the required parameters in the User object were not supplied.
     *   <li>ObjectExists - The user by the given name already exists.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException
     * @throws JAXBException
     */
    public CreateUserResponse createUser(User user, String password)
        throws IOException, JAXBException
    {
        Map params = new HashMap();

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
     * @return {@link com.hyperic.hq.hqapi1.jaxb.ResponseStatus#SUCCESS} is the
     * user was deleted successfully.
     *
     * On {@link com.hyperic.hq.hqapi1.jaxb.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>ObjectNotFound - The given user was not found in the system.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException
     * @throws JAXBException
     */
    public DeleteUserResponse deleteUser(User user)
        throws IOException, JAXBException
    {
        Map params = new HashMap();

        params.put("Name", user.getName());

        return (DeleteUserResponse)doGet("/hqu/hqapi1/user/delete.hqu",
                                         params, DeleteUserResponse.class);
    }

    /**
     * Sync a {@link User} with HQ.
     *
     * @param user The user to sync
     * @return {@link com.hyperic.hq.hqapi1.jaxb.ResponseStatus#SUCCESS} if the
     * user was synced successfully.
     *
     * On {@link com.hyperic.hq.hqapi1.jaxb.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>ObjectNotFound - The given user was not found in the system.
     *   <li>PermissionDenied - The connected user does not have permission to modify this user.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException
     * @throws JAXBException
     */
    public SyncUserResponse syncUser(User user)
        throws IOException, JAXBException
    {
        SyncUserRequest req = new SyncUserRequest();
        req.setUser(user);

        return (SyncUserResponse)doPost("/hqu/hqapi1/user/sync.hqu",
                                        req, SyncUserResponse.class);
    }
}
