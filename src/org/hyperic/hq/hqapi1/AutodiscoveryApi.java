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

import org.hyperic.hq.hqapi1.types.ApplicationsResponse;
import org.hyperic.hq.hqapi1.types.QueueResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.io.IOException;
import java.util.HashMap;

/**
 * The Hyperic HQ Autodiscovery API.
 * <br><br>
 * This class provides access to the auto discovery queue.  Each of the
 * methods in this class return {@link org.hyperic.hq.hqapi1.types.Response}
 * objects that wrap the result of the method with a
 * {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 *
 */
public class AutodiscoveryApi extends BaseApi {

    public AutodiscoveryApi(HQConnection connection) {
        super(connection);
    }

    /**
     * Get all the entries in the auto-discovery queue.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * a list of {@link org.hyperic.hq.hqapi1.types.AIPlatform} objects is
     * returned via {@link org.hyperic.hq.hqapi1.types.QueueResponse#getAIPlatform()}
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public QueueResponse getQueue()
        throws IOException
    {
        return doGet("autodiscovery/getQueue.hqu", new HashMap<String,String[]>(),
                     new XmlResponseHandler<QueueResponse>(QueueResponse.class));
    }

    /**
     * Approve a {@link org.hyperic.hq.hqapi1.types.AIPlatform} into the HQ
     * inventory.
     *
     * @param id The {@link org.hyperic.hq.hqapi1.types.AIPlatform#getId()}
     * to approve.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the platform was approved into the inventory.
     *
     * @throws java.io.IOException If a network error occurs while making the request.
     */
    public StatusResponse approve(int id)
        throws IOException
    {
        HashMap<String, String[]> params = new HashMap<String, String[]>();

        params.put("id", new String[] { String.valueOf(id) });

        return doGet("autodiscovery/approve.hqu", params,
                     new XmlResponseHandler<StatusResponse>(StatusResponse.class));
    }
}
