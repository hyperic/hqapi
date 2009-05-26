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

package org.hyperic.hq.hqapi1.test;

import java.util.List;

import org.hyperic.hq.hqapi1.ResourceEdgeApi;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class ResourceEdgeDelete_test extends ResourceTestBase {

    public ResourceEdgeDelete_test(String name) {
        super(name);
    }

    public void testDeleteInvalidId() throws Exception {
        ResourceEdgeApi api = getApi().getResourceEdgeApi();       
        StatusResponse response = api.deleteResourceEdges("network", Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }
    
    public void testDeleteInvalidRelation() throws Exception {
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        StatusResponse response = api.deleteResourceEdges("invalid", Integer.MAX_VALUE);
        hqAssertFailureInvalidParameters(response);
    }
    
    public void testDelete() throws Exception {
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        
        // get top-level resources not associated with a network hierarchy 
        ResourcesResponse resourcesResponse = api.getParentResourcesByRelation("network", null, null, false);
        List resources = resourcesResponse.getResource();
        
        if (!resources.isEmpty()) {
            // test delete using a top-level resource not associated with a network hierarchy
            Resource r = (Resource) resources.get(0);
            StatusResponse response = api.deleteResourceEdges("network", r.getId());
            hqAssertSuccess(response);
        }
    }
}
