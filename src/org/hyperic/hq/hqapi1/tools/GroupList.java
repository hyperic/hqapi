package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.GroupsResponse;

public class GroupList extends ToolsBase {

    private static String OPT_COMPAT = "compatible";
    private static String OPT_MIXED = "mixed";

    private static void listGroups(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_COMPAT, "List only compatible groups");
        p.accepts(OPT_MIXED, "List only mixed groups");

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        GroupApi groupApi = api.getGroupApi();

        if (options.has(OPT_COMPAT) && options.has(OPT_MIXED)) {
            System.err.println("Only one of " + OPT_COMPAT + " and " + OPT_MIXED +
                               " is allowed.");
            System.exit(-1);
        }
         
        GroupsResponse groups;

        if (options.has(OPT_COMPAT)) {
            groups = groupApi.getCompatibleGroups();
        } else if (options.has(OPT_MIXED)) {
            groups = groupApi.getMixedGroups();
        } else {
            groups = groupApi.getGroups();
        }

        XmlUtil.serialize(groups, System.out, Boolean.TRUE);
    }

    public static void main(String[] args) throws Exception {
        try {
            listGroups(args);
        } catch (Exception e) {
            System.err.println("Error listing groups: " + e.getMessage());
            System.exit(-1);
        }
    }
}
