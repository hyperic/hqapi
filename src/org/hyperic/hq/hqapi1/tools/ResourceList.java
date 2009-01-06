package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;

import java.util.Arrays;

public class ResourceList extends ToolsBase {

    private static String OPT_PROTOTYPE = "prototype";
    private static String OPT_PLATFORM = "platform";

    private static String OPT_VERBOSE = "verbose";
    private static String OPT_CHILDREN = "children";

    private static String[] ONE_REQUIRED = { OPT_PROTOTYPE, OPT_PLATFORM };

    private static void listResources(String[] args) throws Exception {

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

    public static void main(String[] args) throws Exception {
        try {
            listResources(args);
        } catch (Exception e) {
            System.err.println("Error listing resources: " + e.getMessage());
            System.exit(-1);
        }
    }
}
