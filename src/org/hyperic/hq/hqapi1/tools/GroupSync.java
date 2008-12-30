package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.GroupsResponse;
import org.hyperic.hq.hqapi1.types.Group;

import java.util.List;

public class GroupSync extends ToolsBase {

    private static void syncRoles(String[] args) throws Exception {

        OptionParser p = getOptionParser();
        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        GroupApi groupApi = api.getGroupApi();

        GroupsResponse resp = XmlUtil.deserialize(GroupsResponse.class,
                                                  System.in);
        List<Group> groups = resp.getGroup();

        StatusResponse syncResponse = groupApi.syncGroups(groups);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + groups.size() + " groups.");
    }

    public static void main(String[] args) throws Exception {
        try {
            syncRoles(args);
        } catch (Exception e) {
            System.err.println("Error syncing groups: " + e.getMessage());
            System.exit(-1);
        }
    }
}
