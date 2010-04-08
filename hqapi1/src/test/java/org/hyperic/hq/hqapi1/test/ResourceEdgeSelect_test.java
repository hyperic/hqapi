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
import org.hyperic.hq.hqapi1.types.ResourcesResponse;

public class ResourceEdgeSelect_test extends ResourceTestBase {

    public ResourceEdgeSelect_test(String name) {
        super(name);
    }

    public void testSelectResources() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getResourcesByNoRelation("network", null, null);
        hqAssertSuccess(response);
    }

    public void testSelectInvalidResources() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getResourcesByNoRelation("invalid", null, null);
        hqAssertFailureInvalidParameters(response);
    }
    
    public void testSelectParentResources() throws Exception {
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getParentResourcesByRelation("network", null, null, false);
        hqAssertSuccess(response);        
    }

    public void testSelectInvalidParentResources() throws Exception {
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getParentResourcesByRelation("invalid", null, null, false);
        hqAssertFailureInvalidParameters(response);
    }
    
    public void testSelectResourcesByPrototype() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getResourcesByNoRelation("network", "MacOSX", null);
        hqAssertSuccess(response);
    }

    public void testSelectParentResourcesByPrototype() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getParentResourcesByRelation("network", "Xen Host", null, false);
        hqAssertSuccess(response);
    }

    public void testSelectResourcesByName() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getResourcesByNoRelation("network", null, "local");
        hqAssertSuccess(response);
    }
    
    public void testSelectParentResourcesByName() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getParentResourcesByRelation("network", null, "xen", false);
        hqAssertSuccess(response);
    }
    
    public void testSelectResourcesByPrototypeAndName() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getResourcesByNoRelation("network", "Win32", "local");
        hqAssertSuccess(response);
    }
    
    public void testSelectParentResourcesByPrototypeAndName() throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        ResourcesResponse response = api.getParentResourcesByRelation("network", "Xen Host", "xen", false);
        hqAssertSuccess(response);
    }
}