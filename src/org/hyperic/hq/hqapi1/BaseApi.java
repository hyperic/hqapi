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

import java.io.IOException;
import java.util.Map;

abstract class BaseApi {
    private static final String BASE_URI = "/hqu/hqapi1/";

    private final HQConnection _conn;

    BaseApi(HQConnection conn) {
        _conn = conn;
    }
    
    /**
     * Issue a GET for the specified controller/action.
     * 
     * @param action  The name of the controller/action to GET from.  This is
     *  appended on to the BASE_URI and results in a path like
     * '/hqu/hqapi1/user/listUsers.hqu'
     * @param params  A map parameters to pass to the action.  Each HTTP
     * parameter may have multiple values.
     */
    <T> T doGet(String action, Map<String, String[]> params,
    		ResponseHandler<T> responseHandler)
        throws IOException
    {
        return _conn.doGet(BASE_URI + action, params, responseHandler);
    }

    /**
     * Issue a POST for the specified controller/action.
     *
     * @param action  The name of the controller/action to POST to.  This is
     *                tacked on to the BASE_URI and results in a path
     *                like:  '/hqu/hqapi1/user/syncUsers.hqu'
     *                ex:  'resource/syncResources.hqu'
     */
    <T> T doPost(String action, Object o, ResponseHandler<T> responseHandler)
        throws IOException
    {
        return _conn.doPost(BASE_URI + action, o, responseHandler);
    }
}
