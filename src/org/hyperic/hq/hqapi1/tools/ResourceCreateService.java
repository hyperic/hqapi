package org.hyperic.hq.hqapi1.tools;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;

import java.util.HashMap;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class ResourceCreateService extends ToolsBase {

    private static String OPT_PROTOTYPE  = "prototype";
    private static String OPT_RESOURCEID = "resourceId";
    private static String OPT_NAME       = "name";

    private static void createService(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_PROTOTYPE, "The resource prototype to create").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_RESOURCEID, "The parent resource id").
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
                                                             OPT_RESOURCEID),
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

    public static void main(String[] args) throws Exception {
        try {
            createService(args);
        } catch (Exception e) {
            System.err.println("Error creating service: " + e.getMessage());
            System.exit(-1);
        }
    }
}
