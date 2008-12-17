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

    private static String[] ONE_REQUIRED = { OPT_PROTOTYPE };

    private static void listRoles(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_PROTOTYPE, "If specified, return only resources with the " +
                  "specified resource prototype").
                withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi resourceApi = api.getResourceApi();

        ResourcesResponse resources;

        if (options.has(OPT_PROTOTYPE)) {
            String prototype = (String) options.valueOf(OPT_PROTOTYPE);
            ResourcePrototypeResponse protoResponse =
                    resourceApi.getResourcePrototype(prototype);
            checkSuccess(protoResponse);
            resources = resourceApi.getResources(protoResponse.getResourcePrototype());
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
