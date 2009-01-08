package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RoleResponse;
import org.hyperic.hq.hqapi1.types.RolesResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.Arrays;
import java.util.List;

public class RoleCommand extends Command {

    private static String CMD_LIST = "list";
    private static String CMD_SYNC = "sync";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC };

    private static String OPT_ID   = "id";
    private static String OPT_NAME = "name";

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
        } else {
            printUsage();
            System.exit(-1);
        }
    }

    private void list(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "If specified, return the role with the given id.").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_NAME, "If specified, return the role with the given name").
                withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        RoleApi roleApi = api.getRoleApi();

        RolesResponse roles;

        if (options.has(OPT_ID)) {
            Integer id = (Integer) options.valueOf(OPT_ID);
            RoleResponse role = roleApi.getRole(id);
            checkSuccess(role);
            roles = new RolesResponse();
            roles.setStatus(ResponseStatus.SUCCESS);
            roles.getRole().add(role.getRole());
        } else if (options.has(OPT_NAME)) {
            String name = (String) options.valueOf(OPT_NAME);
            RoleResponse role = roleApi.getRole(name);
            checkSuccess(role);
            roles = new RolesResponse();
            roles.setStatus(ResponseStatus.SUCCESS);
            roles.getRole().add(role.getRole());
        } else {
            roles = roleApi.getRoles();
            checkSuccess(roles);
        }

        XmlUtil.serialize(roles, System.out, Boolean.TRUE);
    }

    private void sync(String[] args) throws Exception {

        OptionParser p = getOptionParser();
        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        RoleApi roleApi = api.getRoleApi();

        RolesResponse resp = XmlUtil.deserialize(RolesResponse.class,
                                                 System.in);
        List<Role> roles = resp.getRole();

        StatusResponse syncResponse = roleApi.syncRoles(roles);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + roles.size() + " roles.");
    }
}
