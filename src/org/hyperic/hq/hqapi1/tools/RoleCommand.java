/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

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
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
@Component
public class RoleCommand extends AbstractCommand {

    private static String CMD_LIST = "list";
    private static String CMD_SYNC = "sync";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC };

    private static String OPT_ID   = "id";
    private static String OPT_NAME = "name";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }

    public int handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return 1;
        }

        if (args[0].equals(CMD_LIST)) {
            list(trim(args));
        } else if (args[0].equals(CMD_SYNC)) {
            sync(trim(args));
        } else {
            printUsage();
            return 1;
        }
        return 0;
    }
    
    public String getName() {
        return "role";
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

        InputStream is = getInputStream(options);
        RolesResponse resp = XmlUtil.deserialize(RolesResponse.class, is);
        List<Role> roles = resp.getRole();

        StatusResponse syncResponse = roleApi.syncRoles(roles);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + roles.size() + " roles.");
    }
}
