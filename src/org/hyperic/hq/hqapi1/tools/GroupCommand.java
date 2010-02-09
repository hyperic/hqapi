package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourceResponse;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class GroupCommand extends Command {

    private static String CMD_LIST   = "list";
    private static String CMD_SYNC   = "sync";
    private static String CMD_DELETE = "delete";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC, CMD_DELETE };

    private static String OPT_ID     = "id";
    private static String OPT_COMPAT = "compatible";
    private static String OPT_MIXED  = "mixed";

    // Additional sync commands when syncing via command line options.
    private static String OPT_NAME          = "name";
    private static String OPT_PROTOTYPE     = "prototype";
    private static String OPT_PLATFORM      = "platform";
    private static String OPT_REGEX         = "regex";
    private static String OPT_DELETEMISSING = "deleteMissing";
    private static String OPT_DESC          = "description";
    private static String OPT_CHILDREN      = "children";

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

        p.accepts(OPT_NAME, "The group name to sync").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_PROTOTYPE, "The resource type to query for group membership").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_PLATFORM, "The platform to query for group membership").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_REGEX, "The regular expression to apply to the " + OPT_PROTOTYPE +
                  " flag").withRequiredArg().ofType(String.class);
        p.accepts(OPT_DELETEMISSING, "Remove resources in the group not included in " +
                  "the " + OPT_PROTOTYPE + " and " + OPT_REGEX);
        p.accepts(OPT_COMPAT, "If specified, attempt to make the group compatible");
        p.accepts(OPT_DESC, "If specified, set the description for the group").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_CHILDREN, "If specified, include child resources of the " +
                  "specified prototype and regex");

        OptionSet options = getOptions(p, args);

        if (options.hasArgument(OPT_NAME)) {
            syncViaCommandLineArgs(options);
            return;
        }

        HQApi api = getApi(options);

        GroupApi groupApi = api.getGroupApi();

        InputStream is = getInputStream(options);

        GroupsResponse resp = XmlUtil.deserialize(GroupsResponse.class, is);
        List<Group> groups = resp.getGroup();

        GroupsResponse syncResponse = groupApi.syncGroups(groups);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + groups.size() + " groups.");
    }

    // Helper function to unroll a resource and it's children into a single list.
    private List<Resource> getFlattenResources(List<Resource> resources) {
        List<Resource> result = new ArrayList<Resource>();

        for (Resource r : resources) {
            result.add(r);
            if (r.getResource().size() > 0) {
                result.addAll(getFlattenResources(r.getResource()));
            }
        }
        return result;
    }

    private void syncViaCommandLineArgs(OptionSet s) throws Exception
    {
        // Required args
        String name = (String)getRequired(s, OPT_NAME);
        String prototype = (String)s.valueOf(OPT_PROTOTYPE);
        String platform = (String)s.valueOf(OPT_PLATFORM);

        // Optional
        String regex = (String)s.valueOf(OPT_REGEX);
        boolean deleteMissing = s.has(OPT_DELETEMISSING);
        boolean compatible = s.has(OPT_COMPAT);
        boolean children = s.has(OPT_CHILDREN);

        HQApi api = getApi(s);

        List<Resource> resources;

        if (prototype != null && platform != null) {
            System.err.println("Only one of " + OPT_PROTOTYPE + " or " +
                               OPT_PLATFORM + " is allowed.");
            return;
        }

        if (prototype != null) {
            // Get prototype
            ResourcePrototypeResponse protoResponse =
                    api.getResourceApi().getResourcePrototype(prototype);
            checkSuccess(protoResponse);

            // Query resources
            ResourcesResponse resourcesResponse = api.getResourceApi().
                    getResources(protoResponse.getResourcePrototype(), false, children);
            checkSuccess(resourcesResponse);
            resources = resourcesResponse.getResource();
        } else if (platform != null) {
            ResourceResponse resourceResponse = api.getResourceApi().
                    getPlatformResource(platform, false, children);
            checkSuccess(resourceResponse);
            resources = new ArrayList<Resource>();
            resources.add(resourceResponse.getResource());
        } else {
            System.err.println("One of " + OPT_PROTOTYPE + " or " +
                               OPT_PLATFORM + " is required.");
            return;
        }

        // Filter based on regex, if given.
        if (regex != null) {
            Pattern pattern = Pattern.compile(regex);
            for (Iterator<Resource> i = resources.iterator(); i.hasNext(); ) {
                Resource r = i.next();
                Matcher m = pattern.matcher(r.getName());
                if (!m.matches()) {
                    i.remove();
                }
            }
        }

        System.out.println(name + ": Found " + resources.size() + " matching resources");

        // Check for existing group
        Group group;
        GroupResponse groupResponse = api.getGroupApi().getGroup(name);
        if (groupResponse.getStatus().equals(ResponseStatus.SUCCESS)) {
            group = groupResponse.getGroup();
            System.out.println(name + ": Syncing existing group (" +
                               group.getResource().size() + " members)");

            if (deleteMissing) {
                System.out.println(name + ": Clearing existing members");
                group.getResource().clear();
            }
        } else {
            group = new Group();
            group.setName(name);
            if (prototype != null && compatible) {
                ResourcePrototypeResponse protoResponse =
                        api.getResourceApi().getResourcePrototype(prototype);
                checkSuccess(protoResponse);
                group.setResourcePrototype(protoResponse.getResourcePrototype());
            }
            System.out.println(name + ": Creating new group");
        }

        if (s.hasArgument(OPT_DESC)) {
            group.setDescription((String)s.valueOf(OPT_DESC));
        }

        List<Resource> flattenedResources = getFlattenResources(resources);
        group.getResource().addAll(flattenedResources);
        List<Group> groups = new ArrayList<Group>();
        groups.add(group);
        GroupsResponse syncResponse = api.getGroupApi().syncGroups(groups);
        checkSuccess(syncResponse);

        System.out.println(name + ": Success (" +
                           syncResponse.getGroup().get(0).getResource().size() + " members)");
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
