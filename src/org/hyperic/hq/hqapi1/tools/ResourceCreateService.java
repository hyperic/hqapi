package org.hyperic.hq.hqapi1.tools;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.GetResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.GetResourceResponse;
import org.hyperic.hq.hqapi1.types.CreateResourceResponse;
import jargs.gnu.CmdLineParser;

import java.util.HashMap;
import java.util.Map;

public class ResourceCreateService extends ToolsBase {

    private static CmdLineParser.Option OPT_PROTOTYPE =
            new CmdLineParser.Option.StringOption("prototype");
    private static CmdLineParser.Option OPT_RESOURCEID =
            new CmdLineParser.Option.IntegerOption("resourceId");
    private static CmdLineParser.Option OPT_NAME =
            new CmdLineParser.Option.StringOption("name");
    
    private static void createService(String[] args) throws Exception {

        Parser p = getParser();

        p.addOption(OPT_PROTOTYPE,  true, "The resource prototype to create.");
        p.addOption(OPT_RESOURCEID, true, "The parent resource id.");
        p.addOption(OPT_NAME, true, "The service name to create");

        try {
            p.parse(args);
        } catch (Exception e) {
            System.err.println("Error parsing command line: " + e.getMessage());
            System.exit(-1);
        }

        if (Boolean.TRUE.equals(p.getOptionValue(OPT_HELP))) {
            p.printUsage();
            System.exit(0);
        }

        HQApi api = getApi(p);
        ResourceApi resourceApi = api.getResourceApi();

        GetResourcePrototypeResponse protoResponse =
                resourceApi.getResourcePrototype((String)p.getRequiredOptionValue(OPT_PROTOTYPE));
        if (!isSuccess(protoResponse)) {
            System.exit(-1);
        }

        GetResourceResponse resourceResponse =
                resourceApi.getResource((Integer)p.getRequiredOptionValue(OPT_RESOURCEID));
        if (!isSuccess(resourceResponse)) {
            System.exit(-1);
        }

        String name = (String)p.getRequiredOptionValue(OPT_NAME);

        Map<String,String> config = new HashMap<String,String>();

        for (String arg : p.getRemainingArgs()) {
            System.out.println("Processing.. " + arg);
            if (arg.startsWith("-D")) {
                int idx = arg.indexOf("=");
                String key = arg.substring(0, idx);
                String val = arg.substring(idx+1);
                System.out.println("Adding key=" + key + " val" + val);
                config.put(key, val);
            }
        }

        CreateResourceResponse createResponse =
                resourceApi.createService(protoResponse.getResourcePrototype(),
                                          resourceResponse.getResource(),
                                          name, config);

        if (!isSuccess(createResponse)) {
            System.exit(-1);
        }

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
