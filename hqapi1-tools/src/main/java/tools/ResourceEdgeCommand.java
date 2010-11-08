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

package org.hyperic.hq.hqapi1.tools;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceEdgeApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.ResourceEdge;
import org.hyperic.hq.hqapi1.types.ResourceEdgesResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.springframework.stereotype.Component;
@Component
public class ResourceEdgeCommand extends AbstractCommand {

    private static String CMD_LIST   = "list";
    private static String CMD_SYNC   = "sync";
    private static String CMD_DELETE = "delete";
    private static String CMD_SELECT = "select";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC, CMD_DELETE, CMD_SELECT };

    private static String OPT_ID        = "id";
    private static String OPT_NAME      = "name";
    private static String OPT_PROTOTYPE = "prototype";
    private static String OPT_RELATION  = "relation";
    private static String OPT_CHILDREN  = "children";
    private static String OPT_ALL       = "all";
    private static String OPT_ADD       = "add";
    private static String OPT_REMOVE    = "remove";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }
    
    public String getName() {
        return "dependency";
     }

    public int handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return 1;
        }

        if (args[0].equals(CMD_LIST)) {
            list(trim(args));
        } else if (args[0].equals(CMD_SYNC)) {
            sync(trim(args));
        } else if (args[0].equals(CMD_DELETE)) {
            delete(trim(args));
        } else if (args[0].equals(CMD_SELECT)) {
            select(trim(args));
        } else {
            printUsage();
            return 1;
        }
        return 0;
    }

    private void list(String[] args) throws Exception {
        String[] ONE_REQUIRED = { OPT_ID, OPT_NAME };

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "The resource id of the top-level resource").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_NAME, "The regex name of the resource").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_PROTOTYPE, "If specified, return only resources with the " +
                  "specified resource prototype").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_RELATION, "The resource relationship").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_CHILDREN, "Include child resources");

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceEdgeApi edgeApi = api.getResourceEdgeApi();
        Object objectResponse = null;

        Integer id = (Integer)options.valueOf(OPT_ID);
        String name = (String)options.valueOf(OPT_NAME);
        String prototype = (String)options.valueOf(OPT_PROTOTYPE);
        String relation = (String)options.valueOf(OPT_RELATION);
        
        if (options.has(OPT_ID) && options.has(OPT_NAME)) {
            System.err.println("Only one of " + Arrays.toString(ONE_REQUIRED) + " may be specified");
            System.exit(-1);
        }
        
        // default value
        if (relation == null) {
            relation = "network";
        }
        
        if (options.has(OPT_CHILDREN)) {
            if (!options.has(OPT_ID) 
                    && !options.has(OPT_NAME) 
                    && !options.has(OPT_PROTOTYPE)) {
                System.err.println("One of " + Arrays.toString(ONE_REQUIRED) + " is required.");
                System.exit(-1);
            }            
            objectResponse = edgeApi.getResourceEdges(relation, id, prototype, name);
        } else {
            objectResponse = edgeApi.getParentResourcesByRelation(relation, prototype, name, true);
        }

        XmlUtil.serialize(objectResponse, System.out, Boolean.TRUE);
    }
    
    private void select(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_NAME, "The regex name of the resource").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_PROTOTYPE, "If specified, return only resources with the " +
                  "specified resource prototype").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_RELATION, "The resource relationship").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_CHILDREN, "Include child resources");

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceEdgeApi edgeApi = api.getResourceEdgeApi();

        String name = (String)options.valueOf(OPT_NAME);
        String prototype = (String)options.valueOf(OPT_PROTOTYPE);
        String relation = (String)options.valueOf(OPT_RELATION);
        
        // default value
        if (relation == null) {
            relation = "network";
        }
        
        ResourcesResponse response = null;
        if (options.has(OPT_CHILDREN)) {
            response = edgeApi.getResourcesByNoRelation(relation, prototype, name);
        } else {
            response = edgeApi.getParentResourcesByRelation(relation, prototype, name, false);
        }

        XmlUtil.serialize(response, System.out, Boolean.TRUE);
    }

    private void sync(String[] args) throws Exception {
        String[] ONE_REQUIRED = { OPT_ALL, OPT_ADD, OPT_REMOVE};

        OptionParser p = getOptionParser();
        
        p.accepts(OPT_ADD, "Add to an existing hierarchy");
        p.accepts(OPT_REMOVE, "Remove from an existing hierarchy");
        p.accepts(OPT_ALL, "Delete and create a new hierarchy");

        OptionSet options = getOptions(p, args);

        int criteria = 0;
        for (String opt : ONE_REQUIRED) {
            if (options.has(opt)) {
                criteria++;
            }
        }

        if (criteria == 0) {
            System.err.println("One of " + Arrays.toString(ONE_REQUIRED) + " is required.");
            System.exit(-1);
        } else if (criteria > 1) {
            System.err.println("Only one of " + Arrays.toString(ONE_REQUIRED) + " may be specified");
            System.exit(-1);
        }
        
        ResourceEdgeApi api = getApi(options).getResourceEdgeApi();

        InputStream is = getInputStream(options);

        ResourceEdgesResponse resp = XmlUtil.deserialize(ResourceEdgesResponse.class, is);
        
        List<ResourceEdge> edges = resp.getResourceEdge();
        StatusResponse syncResponse = null;       
        
        if (options.has(OPT_ADD)) {
            syncResponse = api.createResourceEdges(edges);
        } else if (options.has(OPT_REMOVE)) {
            syncResponse = api.deleteResourceEdges(edges);
        } else if (options.has(OPT_ALL)) {
            syncResponse = api.syncResourceEdges(edges);
        }
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + edges.size() + " resource relationships.");
    }

    private void delete(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_RELATION, "The resource relationship").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_ID, "The top-level resource id of the network hierarchy to delete").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        if (!options.has(OPT_ID)) {
            System.err.println("Required argument " + OPT_ID + " not given");
            System.exit(-1);
        }

        ResourceEdgeApi api = getApi(options).getResourceEdgeApi();

        Integer id = (Integer)options.valueOf(OPT_ID);
        String relation = (String)options.valueOf(OPT_RELATION);

        // default value
        if (relation == null) {
            relation = "network";
        }
        
        StatusResponse response = api.deleteResourceEdges(relation, id);
        checkSuccess(response);

        System.out.println("Successfully deleted top-level resource id " + id
                               + " from the network hierarchy.");
    }
}
