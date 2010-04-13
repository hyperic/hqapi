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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.AgentResponse;
import org.hyperic.hq.hqapi1.types.Ip;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
@Component
public class ResourceCommand extends AbstractCommand {

    private static String CMD_LIST            = "list";
    private static String CMD_SYNC            = "sync";
    private static String CMD_DELETE          = "delete";
    private static String CMD_MOVE            = "move";
    private static String CMD_CREATE_PLATFORM = "createPlatform";
    private static String CMD_CREATE_SERVER   = "createServer";
    private static String CMD_CREATE_SERVICE  = "createService";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC, CMD_DELETE,
                                         CMD_MOVE,
                                         CMD_CREATE_PLATFORM,
                                         CMD_CREATE_SERVER,
                                         CMD_CREATE_SERVICE };

    private static String OPT_PROTOTYPE   = "prototype";
    private static String OPT_RESOURCE_ID = "resourceId";
    private static String OPT_NAME        = "name";
    private static String OPT_ID          = "id";
    private static String OPT_PLATFORM    = "platform";
    private static String OPT_VERBOSE     = "verbose";
    private static String OPT_CHILDREN    = "children";
    private static String OPT_FQDN        = "fqdn";
    private static String OPT_IP          = "ip";
    private static String OPT_AGENT_ID    = "agentId";
    private static String OPT_BATCH_SIZE  = "batchSize";
    private static String OPT_TO          = "to";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }
    
    public String getName() {
        return "resource";
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
        } else if (args[0].equals(CMD_MOVE)) {
            move(trim(args));
        } else if (args[0].equals(CMD_CREATE_PLATFORM)) {
            createPlatform(trim(args));
        } else if (args[0].equals(CMD_CREATE_SERVER)) {
            createServer(trim(args));
        } else if (args[0].equals(CMD_CREATE_SERVICE)) {
            createService(trim(args));
        } else {
            printUsage();
            return 1;
        }
        return 0;
    }

    private void list(String[] args) throws Exception {
        String[] ONE_REQUIRED = { OPT_PROTOTYPE, OPT_PLATFORM, OPT_ID };

        OptionParser p = getOptionParser();

        p.accepts(OPT_PROTOTYPE, "If specified, return only resources with the " +
                  "specified resource prototype").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_PLATFORM, "If specified, return only resources with the " +
                  "specified platform name").withRequiredArg().ofType(String.class);
        p.accepts(OPT_ID, "If specified, return the resource with the given id.").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_NAME, "If specified, return resources that match the " +
                            "given regex.").withRequiredArg().ofType(String.class);

        p.accepts(OPT_VERBOSE, "Include resource configuration and properties");
        p.accepts(OPT_CHILDREN, "Include child resources");

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi resourceApi = api.getResourceApi();

        boolean verbose = false;
        if (options.has(OPT_VERBOSE)) {
            verbose = true;
        }

        boolean children = false;
        if (options.has(OPT_CHILDREN)) {
            children = true;
        }

        int criteria = 0;
        for (String opt : ONE_REQUIRED) {
            if (options.has(opt)) {
                criteria++;
            }
        }

        if (criteria > 1) {
            System.err.println("Only one of " + Arrays.toString(ONE_REQUIRED) + " may be specified");
            System.exit(-1);
        }

        ResourcesResponse resources;
        if (options.has(OPT_PROTOTYPE)) {
            String prototype = (String) options.valueOf(OPT_PROTOTYPE);
            ResourcePrototypeResponse protoResponse =
                    resourceApi.getResourcePrototype(prototype);
            checkSuccess(protoResponse);
            resources = resourceApi.getResources(protoResponse.getResourcePrototype(),
                                                 verbose, children);
            checkSuccess(resources);
        } else if (options.has(OPT_PLATFORM)) {
            String platform = (String)options.valueOf(OPT_PLATFORM);
            ResourceResponse resource =
                    resourceApi.getPlatformResource(platform, verbose, children);
            checkSuccess(resource);

            resources = new ResourcesResponse();
            resources.setStatus(resource.getStatus());
            resources.getResource().add(resource.getResource());
        } else if (options.has(OPT_ID)) {
            Integer id = (Integer)options.valueOf(OPT_ID);
            ResourceResponse resource = resourceApi.getResource(id, verbose, children);
            checkSuccess(resource);

            resources = new ResourcesResponse();
            resources.setStatus(resource.getStatus());
            resources.getResource().add(resource.getResource());
        } else {
            System.err.println("One of " + Arrays.toString(ONE_REQUIRED) + " required");
            return;
        }

        // Optional filtering by name
        if (options.has(OPT_NAME)) {
            Pattern pattern = Pattern.compile((String)options.valueOf(OPT_NAME));

            for (Iterator<Resource> i = resources.getResource().iterator(); i.hasNext();) {
                Resource r = i.next();
                Matcher m = pattern.matcher(r.getName());
                if (!m.matches()) {
                    i.remove();
                }
            }
        }

        XmlUtil.serialize(resources, System.out, Boolean.TRUE);
    }

    private void sync(String[] args) throws Exception {
   
        OptionParser p = getOptionParser();

        p.accepts(OPT_BATCH_SIZE, "Process the sync in batches of the given size").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        ResourceApi resourceApi = api.getResourceApi();

        InputStream is = getInputStream(options);
        ResourcesResponse resp = XmlUtil.deserialize(ResourcesResponse.class, is);
        List<Resource> resources = resp.getResource();


        System.out.println("Syncing " + resources.size() + " resources");

        int numSynced = 0;
        if (options.has(OPT_BATCH_SIZE)) {
            int batchSize = (Integer)options.valueOf(OPT_BATCH_SIZE);
            int numBatches = (int)Math.ceil(resources.size()/((double)batchSize));

            for (int i = 0; i < numBatches; i++) {
                System.out.println("Syncing batch " + (i + 1) + " of " + numBatches);
                int fromIndex = i * batchSize;
                int toIndex = (fromIndex + batchSize) > resources.size() ?
                              resources.size() : (fromIndex + batchSize);
                StatusResponse syncResponse =
                        resourceApi.syncResources(resources.subList(fromIndex,
                                                                    toIndex));
                checkSuccess(syncResponse);
                numSynced += (toIndex - fromIndex);
            }
        } else {
            StatusResponse syncResponse = resourceApi.syncResources(resources);
            checkSuccess(syncResponse);
            numSynced = resources.size();
        }

        System.out.println("Successfully synced " + numSynced + " resources");
    }

    private void delete(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "The resource id to delete").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        if (!options.has(OPT_ID)) {
            System.err.println("Required argument " + OPT_ID + " not given");
            System.exit(-1);
        }

        ResourceApi resourceapi = getApi(options).getResourceApi();

        Integer id = (Integer)options.valueOf(OPT_ID);

        StatusResponse response = resourceapi.deleteResource(id);
        checkSuccess(response);

        System.out.println("Successfully deleted resource id " + id);
    }

    private void move(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "The resource id to move").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_TO, "The destination resource id").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi resourceApi = api.getResourceApi();

        Integer targetId = (Integer)getRequired(options, OPT_ID);
        Integer destinationId = (Integer)getRequired(options, OPT_TO);

        ResourceResponse targetResource = resourceApi.getResource(targetId, false, false);
        checkSuccess(targetResource);

        ResourceResponse destResource = resourceApi.getResource(destinationId, false, false);
        checkSuccess(destResource);
        
        StatusResponse response = resourceApi.moveResource(targetResource.getResource(),
                                                           destResource.getResource());
        checkSuccess(response);

        System.out.println("Sucessfully moved " + targetResource.getResource().getName() +
                           " to " + destResource.getResource().getName());
    }

    private void createPlatform(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_PROTOTYPE, "The resource prototype to create").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_NAME, "The name of the platform to create").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_FQDN, "The FQDN of the platform to create").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_IP, "The Ip address of the platform to create").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_AGENT_ID, "The id of the Agent which will service this platform").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi resourceApi = api.getResourceApi();
        AgentApi agentApi = api.getAgentApi();

        ResourcePrototypeResponse protoResponse =
                resourceApi.getResourcePrototype((String)getRequired(options,
                                                                     OPT_PROTOTYPE));
        checkSuccess(protoResponse);

        AgentResponse agentResponse =
                agentApi.getAgent((Integer)getRequired(options,
                                                       OPT_AGENT_ID));
        checkSuccess(agentResponse);

        String fqdn = (String)getRequired(options, OPT_FQDN);
        String address = (String)getRequired(options, OPT_IP);
        List<Ip> ips = new ArrayList<Ip>();
        Ip ip = new Ip();
        ip.setAddress(address);
        ips.add(ip);

        String name = (String)getRequired(options ,OPT_NAME);

        Map<String,String> config = new HashMap<String,String>();
        for (String opt : options.nonOptionArguments()) {
            int idx;
            if ((idx = opt.indexOf("=")) != -1) {
                String key = opt.substring(0, idx);
                String val = opt.substring(idx+1);
                config.put(key, val);
            }
        }

        ResourceResponse createResponse =
                resourceApi.createPlatform(agentResponse.getAgent(),
                                           protoResponse.getResourcePrototype(),
                                           name, fqdn, ips, config);

        checkSuccess(createResponse);

        System.out.println("Successfully created '" +
                           createResponse.getResource().getName() + "' (id=" +
                           createResponse.getResource().getId() + ")");
    }

    private void createServer(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_PROTOTYPE, "The resource prototype to create").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_RESOURCE_ID, "The parent resource id").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_NAME, "The name of the server to create").
                withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi resourceApi = api.getResourceApi();

        ResourcePrototypeResponse protoResponse =
                resourceApi.getResourcePrototype((String)getRequired(options,
                                                                     OPT_PROTOTYPE));
        checkSuccess(protoResponse);

        ResourceResponse resourceResponse =
                resourceApi.getResource((Integer)getRequired(options,
                                                             OPT_RESOURCE_ID),
                                        false, true);
        checkSuccess(resourceResponse);

        String name = (String)getRequired(options ,OPT_NAME);

        Map<String,String> config = new HashMap<String,String>();
        for (String opt : options.nonOptionArguments()) {
            int idx;
            if ((idx = opt.indexOf("=")) != -1) {
                String key = opt.substring(0, idx);
                String val = opt.substring(idx+1);
                config.put(key, val);
            }
        }

        ResourceResponse createResponse =
                resourceApi.createServer(protoResponse.getResourcePrototype(),
                                         resourceResponse.getResource(),
                                         name, config);

        checkSuccess(createResponse);

        System.out.println("Successfully created '" +
                           createResponse.getResource().getName() + "' (id=" +
                           createResponse.getResource().getId() + ")");
    }
    
    private void createService(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_PROTOTYPE, "The resource prototype to create").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_RESOURCE_ID, "The parent resource id").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_NAME, "The name of the service to create").
                withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi resourceApi = api.getResourceApi();

        ResourcePrototypeResponse protoResponse =
                resourceApi.getResourcePrototype((String)getRequired(options,
                                                                     OPT_PROTOTYPE));
        checkSuccess(protoResponse);

        ResourceResponse resourceResponse =
                resourceApi.getResource((Integer)getRequired(options,
                                                             OPT_RESOURCE_ID),
                                        false, true);
        checkSuccess(resourceResponse);

        String name = (String)getRequired(options ,OPT_NAME);

        Map<String,String> config = new HashMap<String,String>();
        for (String opt : options.nonOptionArguments()) {
            int idx;
            if ((idx = opt.indexOf("=")) != -1) {
                String key = opt.substring(0, idx);
                String val = opt.substring(idx+1);
                config.put(key, val);
            }
        }

        ResourceResponse createResponse =
                resourceApi.createService(protoResponse.getResourcePrototype(),
                                          resourceResponse.getResource(),
                                          name, config);

        checkSuccess(createResponse);

        System.out.println("Successfully created '" +
                           createResponse.getResource().getName() + "' (id=" +
                           createResponse.getResource().getId() + ")");
    }
}
