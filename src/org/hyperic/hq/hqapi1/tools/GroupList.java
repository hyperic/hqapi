package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.GroupsResponse;

public class GroupList extends ToolsBase {

    private static void listGroups(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        GroupApi groupApi = api.getGroupApi();

        GroupsResponse groups = groupApi.getGroups();

        XmlUtil.serialize(groups, System.out, Boolean.TRUE);
    }

    public static void main(String[] args) throws Exception {
        try {
            listGroups(args);
        } catch (Exception e) {
            System.err.println("Error listing users: " + e.getMessage());
            System.exit(-1);
        }
    }
}
