package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class ResourceDelete extends ToolsBase {

    private static final String OPT_ID = "id";

    private static void deleteResource(String[] args) throws Exception {

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

    public static void main(String[] args) throws Exception {
        try {
            deleteResource(args);
        } catch (Exception e) {
            System.err.println("Error deleting resource: " + e.getMessage());
            System.exit(-1);
        }
    }
}
