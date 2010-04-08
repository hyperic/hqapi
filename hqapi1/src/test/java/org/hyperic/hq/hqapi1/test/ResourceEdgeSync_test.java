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
import java.util.ArrayList;

import org.hyperic.hq.hqapi1.ResourceEdgeApi;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourceEdge;
import org.hyperic.hq.hqapi1.types.ResourceFrom;
import org.hyperic.hq.hqapi1.types.ResourceTo;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class ResourceEdgeSync_test extends ResourceTestBase {

    private static final String SYNC_ALL = "all";
    private static final String SYNC_ADD = "add";
    private static final String SYNC_REMOVE = "remove";
    
    public ResourceEdgeSync_test(String name) {
        super(name);
    }
    
    public void testSyncAllInvalidRelation() throws Exception {
        ResourceEdgeApi api = getApi().getResourceEdgeApi();   
        List<ResourceEdge> edges = new ArrayList<ResourceEdge>();
        ResourceEdge edge = new ResourceEdge();
        ResourceFrom from = new ResourceFrom();
        Resource parent = getLocalPlatformResource(false, false);
        
        from.setResource(parent);
        edge.setResourceFrom(from);
        edge.setRelation("invalid");
        edges.add(edge);
        
        StatusResponse response = api.syncResourceEdges(edges);        
        hqAssertFailureInvalidParameters(response);
    }
    
    public void testSyncAllInvalidParentResource() throws Exception {
        ResourceEdgeApi api = getApi().getResourceEdgeApi();   
        List<ResourceEdge> edges = new ArrayList<ResourceEdge>();
        ResourceEdge edge = new ResourceEdge();
        ResourceFrom from = new ResourceFrom();
        Resource parent = new Resource();
        parent.setId(Integer.MAX_VALUE);
        
        from.setResource(parent);
        edge.setResourceFrom(from);
        edge.setRelation("network");
        edges.add(edge);
        
        StatusResponse response = api.syncResourceEdges(edges);        
        hqAssertFailureObjectNotFound(response);
    }
    
    public void testSyncAll() throws Exception {
        testSync(SYNC_ALL);
    }

    public void testSyncAdd() throws Exception {
        testSync(SYNC_ADD);
    }
    
    public void testSyncRemove() throws Exception {
        testSync(SYNC_REMOVE);
    }
    
    private void testSync(String syncType) throws Exception {        
        ResourceEdgeApi api = getApi().getResourceEdgeApi();
        
        // get top-level resources not associated with a network hierarchy 
        ResourcesResponse parentResponse = api.getParentResourcesByRelation("network", null, null, false);
        List<Resource> parentResources = parentResponse.getResource();

        // get resources not  associated with a top-level resource in a network hierarchy
        ResourcesResponse childrenResponse = api.getResourcesByNoRelation("network", null, null);
        List<Resource> childrenResources = childrenResponse.getResource();

        if (!parentResources.isEmpty()) {
            List<ResourceEdge> edges = new ArrayList<ResourceEdge>();
            ResourceEdge edge = new ResourceEdge();
            ResourceFrom from = new ResourceFrom();
            ResourceTo to = null;
            Resource parent = (Resource) parentResources.get(0);
            Resource child = null;

            if (!childrenResources.isEmpty()) {
                to = new ResourceTo();
                child = (Resource) childrenResources.get(0);
                to.getResource().add(child);
                edge.setResourceTo(to);
            }
            from.setResource(parent);
            edge.setResourceFrom(from);
            edge.setRelation("network");
            edges.add(edge);
            
            // create initial hierarchy for all sync types
            StatusResponse syncResponse = api.syncResourceEdges(edges);
            hqAssertSuccess(syncResponse);
            
            if (syncType.equals(SYNC_REMOVE)) {
                // remove the child resource that was just added above
                StatusResponse removeResponse = api.deleteResourceEdges(edges);
                hqAssertSuccess(removeResponse);
            } else if (syncType.equals(SYNC_ADD)) {
                if (childrenResources.size() >= 2) {
                    // get and add next child resource
                    child = (Resource) childrenResources.get(1);
                    to.getResource().clear();
                    to.getResource().add(child);
                    
                    StatusResponse addResponse = api.createResourceEdges(edges);
                    hqAssertSuccess(addResponse);                    
                }
            }
            
            // cleanup
            StatusResponse deleteResponse = api.deleteResourceEdges("network", parent.getId());
            hqAssertSuccess(deleteResponse);
        }
    }
}
