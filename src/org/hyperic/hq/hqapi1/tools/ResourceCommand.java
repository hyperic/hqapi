package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.AgentResponse;
import org.hyperic.hq.hqapi1.types.Ip;

public class ResourceCommand extends Command {

    private static String CMD_LIST            = "list";
    private static String CMD_SYNC            = "sync";
    private static String CMD_DELETE          = "delete";
    private static String CMD_CREATE_PLATFORM = "createPlatform";
    private static String CMD_CREATE_SERVER   = "createServer";
    private static String CMD_CREATE_SERVICE  = "createService";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC, CMD_DELETE,
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

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }

    protected void handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            System.exit(-1);
        }

        if (args[0].equals(CMD_LIST)) {
            list(trim(args));
        } else if (args[0].equals(CMD_SYNC)) {
            sync(trim(args));
        } else if (args[0].equals(CMD_DELETE)) {
            delete(trim(args));
        } else if (args[0].equals(CMD_CREATE_PLATFORM)) {
            createPlatform(trim(args));
        } else if (args[0].equals(CMD_CREATE_SERVER)) {
            createServer(trim(args));
        } else if (args[0].equals(CMD_CREATE_SERVICE)) {
            createService(trim(args));
        } else {
            printUsage();
            System.exit(-1);
        }
    }

    private void list(String[] args) throws Exception {
        String[] ONE_REQUIRED = { OPT_PROTOTYPE, OPT_PLATFORM };

        OptionParser p = getOptionParser();

        p.accepts(OPT_PROTOTYPE, "If specified, return only resources with the " +
                  "specified resource prototype").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_PLATFORM, "If specified, return only resources with the " +
                  "specified platform name").withRequiredArg().ofType(String.class);

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
        } else if (criteria == 0) {
            System.err.println("One of " + Arrays.toString(ONE_REQUIRED) + " required");
            System.exit(-1);
        }

        if (options.has(OPT_PROTOTYPE)) {
            String prototype = (String) options.valueOf(OPT_PROTOTYPE);
            ResourcePrototypeResponse protoResponse =
                    resourceApi.getResourcePrototype(prototype);
            checkSuccess(protoResponse);
            ResourcesResponse resources =
                    resourceApi.getResources(protoResponse.getResourcePrototype(),
                                             verbose, children);
            checkSuccess(resources);
            XmlUtil.serialize(resources, System.out, Boolean.TRUE);
        } else if (options.has(OPT_PLATFORM)) {
            String platform = (String)options.valueOf(OPT_PLATFORM);
            ResourceResponse resource =
                    resourceApi.getPlatformResource(platform, verbose, children);
            checkSuccess(resource);

            ResourcesResponse resources = new ResourcesResponse();
            resources.setStatus(resource.getStatus());
            resources.getResource().add(resource.getResource());
            XmlUtil.serialize(resources, System.out, Boolean.TRUE);
        }
    }

    private void sync(String[] args) throws Exception {
   
        OptionParser p = getOptionParser();
        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        ResourceApi resourceApi = api.getResourceApi();

        ResourcesResponse resp = XmlUtil.deserialize(ResourcesResponse.class,
                                                     System.in);
        List<Resource> resources = resp.getResource();

        StatusResponse syncResponse = resourceApi.syncResources(resources);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + resources.size() + " resources.");
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
