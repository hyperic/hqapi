package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;

import java.util.Arrays;

public class ResourceList extends ToolsBase {

    private static String OPT_PROTOTYPE = "prototype";
    private static String OPT_CONFIG = "config";
    private static String OPT_CHILDREN = "children";

    private static String[] ONE_REQUIRED = { OPT_PROTOTYPE };

    private static void listRoles(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_PROTOTYPE, "If specified, return only resources with the " +
                  "specified resource prototype").
                withRequiredArg().ofType(String.class);
        
        p.accepts(OPT_CONFIG, "Include resource configuration");
        p.accepts(OPT_CHILDREN, "Include child resources");

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi resourceApi = api.getResourceApi();

        ResourcesResponse resources;

        boolean config = false;
        if (options.has(OPT_CONFIG)) {
            config = true;
        }

        boolean children = false;
        if (options.has(OPT_CHILDREN)) {
            children = true;
        }

        if (options.has(OPT_PROTOTYPE)) {
            String prototype = (String) options.valueOf(OPT_PROTOTYPE);
            ResourcePrototypeResponse protoResponse =
                    resourceApi.getResourcePrototype(prototype);
            checkSuccess(protoResponse);
            resources = resourceApi.getResources(protoResponse.getResourcePrototype(),
                                                 config, children);
            checkSuccess(resources);
            XmlUtil.serialize(resources, System.out, Boolean.TRUE);            
        } else {
            System.err.println("One of " + Arrays.toString(ONE_REQUIRED) + " required");
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            listRoles(args);
        } catch (Exception e) {
            System.err.println("Error listing resources: " + e.getMessage());
            System.exit(-1);
        }
    }
}
