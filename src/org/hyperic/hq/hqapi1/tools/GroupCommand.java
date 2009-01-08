package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.Arrays;
import java.util.List;

public class GroupCommand extends Command {

    private static String CMD_LIST   = "list";
    private static String CMD_SYNC   = "sync";
    private static String CMD_DELETE = "delete";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC, CMD_DELETE };

    private static String OPT_ID     = "id";
    private static String OPT_COMPAT = "compatible";
    private static String OPT_MIXED  = "mixed";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }

    protected void handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            System.exit(-1);
        }

        if (args[0].equals(CMD_LIST)) {
            list(trim(args));
        } else if (args[0].equals(CMD_SYNC)) {
            sync(trim(args));
        } else if (args[0].equals(CMD_DELETE)) {
            delete(trim(args));
        } else {
            printUsage();
            System.exit(-1);
        }
    }

    private void list(String[] args) throws Exception {

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

    private void sync(String[] args) throws Exception {

        OptionParser p = getOptionParser();
        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        GroupApi groupApi = api.getGroupApi();

        GroupsResponse resp = XmlUtil.deserialize(GroupsResponse.class,
                                                  System.in);
        List<Group> groups = resp.getGroup();

        GroupsResponse syncResponse = groupApi.syncGroups(groups);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + groups.size() + " groups.");
    }

    private void delete(String[] args) throws Exception {

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
}
