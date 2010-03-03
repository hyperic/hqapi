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

import org.hyperic.hq.hqapi1.types.ControlHistoryResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.QueueResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ControlActionResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * The Hyperic HQ Control API.
 * <br><br>
 * This class provides access to the control actions within the HQ system.  Each of
 * the methods in this class return response objects that wrap the result of the
 * method with a {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 */
public class ControlApi extends BaseApi {

    ControlApi(HQConnection conn) {
        super(conn);
    }

    /**
     * Get the Control history for the given Resource.
     *
     * @param r The {@link org.hyperic.hq.hqapi1.types.Resource} to query.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of {@link org.hyperic.hq.hqapi1.types.ControlHistory}'s are returned via
     * {@link org.hyperic.hq.hqapi1.types.ControlHistoryResponse#getControlHistory()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ControlHistoryResponse getHistory(Resource r)
        throws IOException
    {
        return getHistory(r.getId());
    }

    /**
     * Get the Control history for the given Group.
     *
     * @param g The {@link org.hyperic.hq.hqapi1.types.Group} to query.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of {@link org.hyperic.hq.hqapi1.types.ControlHistory}'s are returned via
     * {@link org.hyperic.hq.hqapi1.types.ControlHistoryResponse#getControlHistory()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ControlHistoryResponse getHistory(Group g)
        throws IOException
    {
        return getHistory(g.getResourceId());
    }
    
    private ControlHistoryResponse getHistory(Integer resourceId)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("resourceId", new String[] { Integer.toString(resourceId)});

        return doGet("control/history.hqu", params, 
        		new XmlResponseHandler<ControlHistoryResponse>(ControlHistoryResponse.class));
    }
    
    /**
     * Get the Control actions for the given Resource.
     *
     * @param r The {@link org.hyperic.hq.hqapi1.types.Resource} to query.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of actions are returned via
     * {@link org.hyperic.hq.hqapi1.types.ControlActionResponse#getAction()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ControlActionResponse getActions(Resource r)
        throws IOException
    {
        return getActions(r.getId());
    }

    /**
     * Get the Control actions for the given Group.
     *
     * @param g The {@link org.hyperic.hq.hqapi1.types.Group} to query.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of actions are returned via
     * {@link org.hyperic.hq.hqapi1.types.ControlActionResponse#getAction()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ControlActionResponse getActions(Group g)
        throws IOException
    {
        return getActions(g.getResourceId());
    }
    
    private ControlActionResponse getActions(Integer resourceId)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("resourceId", new String[] { Integer.toString(resourceId)});

        return doGet("control/actions.hqu", params, 
        		new XmlResponseHandler<ControlActionResponse>(ControlActionResponse.class));
    }
    
    /**
     * Execute a Control action on the given Resource.
     *
     * @param r The {@link org.hyperic.hq.hqapi1.types.Resource} to execute the action on.
     * @param action The action to run.
     * @param arguments An array of arguments to pass to the action.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the action was executed successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse executeAction(Resource r, String action,
                                        String[] arguments)
        throws IOException
    {
        return executeAction(r.getId(), action, arguments);
    }
    
    /**
     * Execute a Control action on the given Resource.
     *
     * @param g The {@link org.hyperic.hq.hqapi1.types.Group} to execute the action on.
     * @param action The action to run.
     * @param arguments An array of arguments to pass to the action.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the action was executed successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse executeAction(Group g, String action,
                                        String[] arguments)
        throws IOException
    {
        return executeAction(g.getResourceId(), action, arguments);
    }
    
    private StatusResponse executeAction(Integer resourceId, String action,
                                         String[] arguments)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("resourceId", new String[] { Integer.toString(resourceId)});
        params.put("action", new String[] { action });
        params.put("arguments", arguments);

        return doGet("control/execute.hqu", params, 
        		new XmlResponseHandler<StatusResponse>(StatusResponse.class));
    }
}
