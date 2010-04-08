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

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.ResourceEdgeApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourceEdgesResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;

public class ResourceEdgeGet_test extends ResourceTestBase {

    public ResourceEdgeGet_test(String name) {
        super(name);
    }

    public void testGetInvalidResourceEdgesById() throws Exception {
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourceEdgesResponse response = api.getResourceEdges("network", Integer.MAX_VALUE, null, null);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetResourceEdgesById() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        Resource r = getLocalPlatformResource(false, false);
        ResourceEdgesResponse response = api.getResourceEdges("network", r.getId(), null, null);
        hqAssertSuccess(response);
    }

    public void testGetResourceEdgesByName() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourceEdgesResponse response = api.getResourceEdges("network", null, null, "local");
        hqAssertSuccess(response);
    }

    public void testGetResourceEdgesByPrototype() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourceEdgesResponse response = api.getResourceEdges("network", null, "MacOSX", null);
        hqAssertSuccess(response);
    }
    
    public void testGetInvalidParentResources() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getParentResourcesByRelation("invalid", null, null, true);       
        hqAssertFailureInvalidParameters(response);
    }
    
    public void testGetParentResources() throws Exception {
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getParentResourcesByRelation("network", null, null, true);       
        hqAssertSuccess(response);
    }

    public void testGetParentResourcesByPrototype() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getParentResourcesByRelation("network", "Cisco IOS", null, true);
        hqAssertSuccess(response);
    }
    
    public void testGetParentResourcesByName() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getParentResourcesByRelation("network", null, "cisco", true);
        hqAssertSuccess(response);
    }
    
    public void testGetParentResourcesByPrototypeAndName() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getParentResourcesByRelation("network", "Cisco IOS", "cisco", true);
        hqAssertSuccess(response);
    }
}