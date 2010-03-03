/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;
import org.hyperic.hq.hqapi1.types.UsersRequest;
import org.hyperic.hq.hqapi1.types.UsersResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Hyperic HQ User API.
 * <br><br>
 * This class provides access to the {@link org.hyperic.hq.hqapi1.types.User}s
 * within the HQ system.  Each of the methods in this class return
 * {@link org.hyperic.hq.hqapi1.types.Response} objects that wrap the result
 * of the method with a
 * {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 */
public class UserApi extends BaseApi {

    UserApi(HQConnection connection) {
        super(connection);
    }

    /**
     * Get a {@link User} by name.
     *
     * @param name The user name to search for.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the User by the given name is returned via
     * {@link org.hyperic.hq.hqapi1.types.UserResponse#getUser()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public UserResponse getUser(String name)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("name", new String[] { name });
        return doGet("user/get.hqu", params, 
        		new XmlResponseHandler<UserResponse>(UserResponse.class));
    }

    /**
     * Get a {@link User} by id.
     *
     * @param id The user id to look up.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the User by the given id is returned via
     * {@link org.hyperic.hq.hqapi1.types.UserResponse#getUser()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public UserResponse getUser(int id)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(id) });
        return doGet("user/get.hqu", params, 
        		new XmlResponseHandler<UserResponse>(UserResponse.class));
    }

    /**
     * Find all {@link org.hyperic.hq.hqapi1.types.User}s in the system.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of Users is returned.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public UsersResponse getUsers()
        throws IOException
    {
        return doGet("user/list.hqu", new HashMap<String,String[]>(),
        		new XmlResponseHandler<UsersResponse>(UsersResponse.class));
    }

    /**
     * Create a {@link User}.
     *
     * @param user The user to create.
     * @param password The password for this user.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * the created user is returned via
     * {@link org.hyperic.hq.hqapi1.types.UserResponse#getUser()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public UserResponse createUser(User user, String password)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();

        params.put("name", new String[] { user.getName() });
        params.put("password", new String[] { password });
        params.put("firstName", new String[] { user.getFirstName() });
        params.put("lastName", new String[] { user.getLastName() });
        params.put("emailAddress", new String[] { user.getEmailAddress() });
        params.put("active", new String[] { Boolean.toString(user.isActive()) });
        params.put("department", new String[] { user.getDepartment() });
        params.put("htmlEmail", new String[] { Boolean.toString(user.isActive())});
        params.put("SMSAddress", new String[] { user.getSMSAddress() });

        return doGet("user/create.hqu", params, 
        		new XmlResponseHandler<UserResponse>(UserResponse.class));
    }

    /**
     * Delete a {@link User}
     *
     * @param id The user id to delete.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * user was deleted successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse deleteUser(int id)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();

        params.put("id", new String[] { Integer.toString(id) });

        return doGet("user/delete.hqu", params,
        		new XmlResponseHandler<StatusResponse>(StatusResponse.class));
    }

    /**
     * Update a {@link User}
     *
     * @param user The user to update.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * user was updated successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse updateUser(User user)
        throws IOException
    {
        UsersRequest req = new UsersRequest();
        req.getUser().add(user);

        return doPost("user/sync.hqu", req, 
        		new XmlResponseHandler<StatusResponse>(StatusResponse.class));
    }

    /**
     * Sync a list of {@link User}s.
     *
     * @param users The list of users to sync.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * users were synced successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse syncUsers(List<User> users)
        throws IOException
    {
        UsersRequest request = new UsersRequest();
        request.getUser().addAll(users);

        return doPost("user/sync.hqu", request,
        		new XmlResponseHandler<StatusResponse>(StatusResponse.class));
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
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse changePassword(User user, String password)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();

        params.put("id", new String[] { Integer.toString(user.getId()) });
        params.put("password", new String[] { password });

        return doGet("user/changePassword.hqu", params,
        		new XmlResponseHandler<StatusResponse>(StatusResponse.class));
    }
}
