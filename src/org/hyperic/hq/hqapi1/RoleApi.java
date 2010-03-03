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

import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RoleRequest;
import org.hyperic.hq.hqapi1.types.RoleResponse;
import org.hyperic.hq.hqapi1.types.RolesRequest;
import org.hyperic.hq.hqapi1.types.RolesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        		new XmlResponseHandler<RolesResponse>(RolesResponse.class));
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
        return doGet("role/get.hqu", params, 
        		new XmlResponseHandler<RoleResponse>(RoleResponse.class));
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
        return doGet("role/get.hqu", params, 
        		new XmlResponseHandler<RoleResponse>(RoleResponse.class));
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
        return doPost("role/create.hqu", request, 
        		new XmlResponseHandler<RoleResponse>(RoleResponse.class));
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
        return doGet("role/delete.hqu", params, 
        		new XmlResponseHandler<StatusResponse>(StatusResponse.class));
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
        return doPost("role/update.hqu", request, 
        		new XmlResponseHandler<StatusResponse>(StatusResponse.class));
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
        return doPost("role/sync.hqu", request,
        		new XmlResponseHandler<StatusResponse>(StatusResponse.class));
    }
}
