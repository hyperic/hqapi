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
import org.hyperic.hq.hqapi1.types.ResourceEdge;
import org.hyperic.hq.hqapi1.types.ResourceEdgesRequest;
import org.hyperic.hq.hqapi1.types.ResourceEdgesResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Hyperic HQ Resource Edge API.
 * <br><br>
 * This class provides access to relationships between resources in HQ.
 */
public class ResourceEdgeApi extends BaseApi {

    ResourceEdgeApi(HQConnection conn) {
        super(conn);
    }

    public ResourceEdgesResponse getResourceEdges(String resourceRelation,
                                                  Integer resourceId,
                                                  String prototype,
                                                  String name)
        throws IOException {
        
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("resourceRelation", new String[] { resourceRelation});
        params.put("prototype", new String[] { prototype });
        params.put("name", new String[] { name });
        if (resourceId != null) {
            params.put("id", new String[] { resourceId.toString() });
        }
        return doGet("resource/getResourceEdges.hqu", params,
                     new XmlResponseHandler<ResourceEdgesResponse>(ResourceEdgesResponse.class));
    }
    
    public ResourcesResponse getParentResourcesByRelation(String resourceRelation, 
                                                          String prototype,
                                                          String name,
                                                          boolean hasChildren)
        throws IOException {
        
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("prototype", new String[] { prototype });
        params.put("name", new String[] { name });
        params.put("resourceRelation", new String[] { resourceRelation });
        params.put("hasChildren", new String[] { Boolean.valueOf(hasChildren).toString() });
        return doGet("resource/getParentResourcesByRelation.hqu", params,
                     new XmlResponseHandler<ResourcesResponse>(ResourcesResponse.class));
    }
    
    public ResourcesResponse getResourcesByNoRelation(String resourceRelation, 
                                                      String prototype,
                                                      String name)
        throws IOException {

        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("prototype", new String[] { prototype });
        params.put("name", new String[] { name });
        params.put("resourceRelation", new String[] { resourceRelation });
        return doGet("resource/getResourcesByNoRelation.hqu", params,
                     new XmlResponseHandler<ResourcesResponse>(ResourcesResponse.class));
    }

    public StatusResponse syncResourceEdges(List<ResourceEdge> edges)
        throws IOException {
    
        ResourceEdgesRequest request = new ResourceEdgesRequest();
        request.getResourceEdge().addAll(edges);
        return doPost("resource/syncResourceEdges.hqu", request, 
                      new XmlResponseHandler<StatusResponse>(StatusResponse.class));
    }
    
    public StatusResponse createResourceEdges(List<ResourceEdge> edges)
        throws IOException {
        
        ResourceEdgesRequest request = new ResourceEdgesRequest();
        request.getResourceEdge().addAll(edges);
        return doPost("resource/createResourceEdges.hqu", request, 
                      new XmlResponseHandler<StatusResponse>(StatusResponse.class));
    }
    
    public StatusResponse deleteResourceEdges(List<ResourceEdge> edges)
        throws IOException {
    
        ResourceEdgesRequest request = new ResourceEdgesRequest();
        request.getResourceEdge().addAll(edges);
        return doPost("resource/deleteResourceEdges.hqu", request, 
                      new XmlResponseHandler<StatusResponse>(StatusResponse.class));
    }
    
    public StatusResponse deleteResourceEdges(String resourceRelation, int id)
        throws IOException {
        
        Map<String,String[]> params = new HashMap<String, String[]>();
        params.put("resourceRelation", new String[] { resourceRelation });
        params.put("id", new String[] { Integer.toString(id) });
        return doGet("resource/deleteAllResourceEdges.hqu", params,
                     new XmlResponseHandler<StatusResponse>(StatusResponse.class));
    }
}