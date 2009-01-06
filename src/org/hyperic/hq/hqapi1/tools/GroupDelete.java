package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class GroupDelete extends ToolsBase {

    private static final String OPT_ID = "id";

    private static void deleteGroup(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "The resource id to delete").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        if (!options.has(OPT_ID)) {
            System.err.println("Required argument " + OPT_ID + " not given");
            System.exit(-1);
        }

        GroupApi groupApi = getApi(options).getGroupApi();

        Integer id = (Integer)options.valueOf(OPT_ID);

        StatusResponse response = groupApi.deleteGroup(id);
        checkSuccess(response);

        System.out.println("Successfully deleted group id " + id);
    }

    public static void main(String[] args) throws Exception {
        try {
            deleteGroup(args);
        } catch (Exception e) {
            System.err.println("Error deleting group: " + e.getMessage());
            System.exit(-1);
        }
    }
}
