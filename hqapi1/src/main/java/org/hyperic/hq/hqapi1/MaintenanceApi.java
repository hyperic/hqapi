/*
 * NOTE: This copyright does *not* cover user programs that use Hyperic
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 *
 * Copyright (C) [2004-2011], VMware, Inc.
 * This file is part of Hyperic.
 *
 * Hyperic is free software; you can redistribute it and/or modify
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
 */

package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.GroupsRequest;
import org.hyperic.hq.hqapi1.types.GroupsResponse;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.MaintenanceResponse;
import org.hyperic.hq.hqapi1.types.MaintenancesResponse;
import org.hyperic.hq.hqapi1.types.MaintenanceState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Hyperic HQ Maintenance API.
 * <br><br>
 * This class provides access to HQ's maintenance subsystem.  Each of the methods
 * in this class return
 * {@link org.hyperic.hq.hqapi1.types.Response} objects that wrap the result
 * of the method with a {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 */
public class MaintenanceApi extends BaseApi {

    MaintenanceApi(HQConnection conn) {
        super(conn);
    }

    /**
     * Schedule a {@link org.hyperic.hq.hqapi1.types.Group} for maintenance.
     *
     * @param groupId The group id to schedule.
     * @param start The start time for maintenance, in ephoch-millis.
     * @param end The end time for maintenance, in ephoch-millis.
     * @return The {@link org.hyperic.hq.hqapi1.types.MaintenanceEvent} for
     * this Group.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MaintenanceResponse schedule(int groupId, long start, long end)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("groupId", new String[] { Integer.toString(groupId) });
        params.put("start", new String[] { Long.toString(start) });
        params.put("end", new String[] { Long.toString(end) });
        return doGet("maintenance/schedule.hqu", params,
                     new XmlResponseHandler<MaintenanceResponse>(MaintenanceResponse.class));
    }

    /**
     * Schedule a {@link org.hyperic.hq.hqapi1.types.Resource} for maintenance.
     *
     * @param resource The resource to schedule.
     * @param start The start time for maintenance, in ephoch-millis.
     * @param end The end time for maintenance, in ephoch-millis.
     * @return The {@link org.hyperic.hq.hqapi1.types.MaintenanceEvent} for
     * this Resource.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MaintenanceResponse schedule(Resource resource, long start, long end)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("resourceId", new String[] { resource.getId().toString() });
        params.put("start", new String[] { Long.toString(start) });
        params.put("end", new String[] { Long.toString(end) });
        return doGet("maintenance/schedule.hqu", params,
                     new XmlResponseHandler<MaintenanceResponse>(MaintenanceResponse.class));
    }

    /**
     * Unschedule a {@link org.hyperic.hq.hqapi1.types.Group} from maintenance.
     *
     * @param groupId The group id to unschedule.
     * @return The status repsonse representing success or failure of this unschedule.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse unschedule(int groupId)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("groupId", new String[] { Integer.toString(groupId) });
        return doGet("maintenance/unschedule.hqu", params,
                     new XmlResponseHandler<StatusResponse>(StatusResponse.class));
    }

    /**
     * Unschedule a {@link org.hyperic.hq.hqapi1.types.Resource} from maintenance.
     *
     * @param resource The resource to unschedule.
     * @return The status repsonse representing success or failure of this unschedule.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse unschedule(Resource resource)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("resourceId", new String[] { resource.getId().toString() });
        return doGet("maintenance/unschedule.hqu", params,
                     new XmlResponseHandler<StatusResponse>(StatusResponse.class));
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MaintenanceEvent} for this
     * group.
     *
     * @param groupId The group id to query.
     * @return The {@link org.hyperic.hq.hqapi1.types.MaintenanceEvent} for this
     * group.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MaintenanceResponse get(int groupId)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("groupId", new String[] { Integer.toString(groupId) });
        return doGet("maintenance/get.hqu", params,
                     new XmlResponseHandler<MaintenanceResponse>(MaintenanceResponse.class));
    }
    
    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MaintenanceEvent} for this
     * resource.
     *
     * @param resource The resource to query.
     * @return The {@link org.hyperic.hq.hqapi1.types.MaintenanceEvent} for this
     * resource.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MaintenanceResponse get(Resource resource)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("resourceId", new String[] { resource.getId().toString() });
        return doGet("maintenance/get.hqu", params,
                     new XmlResponseHandler<MaintenanceResponse>(MaintenanceResponse.class));
    }
    
    /**
     * Get all {@link org.hyperic.hq.hqapi1.types.MaintenanceEvent}s
     *
     * @param state The maintenance event state.
     * @return List of {@link org.hyperic.hq.hqapi1.types.MaintenanceEvent}s
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MaintenancesResponse getAll(MaintenanceState state)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        if (state != null) {
        	params.put("state", new String[] { state.value() });
        }
        return doGet("maintenance/getAll.hqu", params,
                     new XmlResponseHandler<MaintenancesResponse>(MaintenancesResponse.class));
    }
}