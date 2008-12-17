package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.RolesResponse;
import org.hyperic.hq.hqapi1.types.Role;

import java.util.List;

public class RoleSync extends ToolsBase {

    private static void syncRoles(String[] args) throws Exception {

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

    public static void main(String[] args) throws Exception {
        try {
            syncRoles(args);
        } catch (Exception e) {
            System.err.println("Error syncing roles: " + e.getMessage());
            System.exit(-1);
        }
    }
}


