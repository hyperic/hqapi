package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.RolesResponse;
import org.hyperic.hq.hqapi1.types.RoleResponse;

public class RoleList extends ToolsBase {

    private static String OPT_ID = "id";
    private static String OPT_NAME = "name";

    private static void listRoles(String[] args) throws Exception {

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

    public static void main(String[] args) throws Exception {
        try {
            listRoles(args);
        } catch (Exception e) {
            System.err.println("Error listing roles: " + e.getMessage());
            System.exit(-1);
        }
    }
}

