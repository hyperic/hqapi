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

import java.util.Arrays;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ControlApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.ControlActionResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.ControlHistoryResponse;
import org.hyperic.hq.hqapi1.types.ControlHistory;
import org.springframework.stereotype.Component;
@Component
public class ControlCommand extends AbstractCommand {

    private static String CMD_ACTIONS = "actions";
    private static String CMD_HISTORY = "history";
    private static String CMD_EXECUTE = "execute";

    private static String[] COMMANDS = { CMD_ACTIONS, CMD_HISTORY, CMD_EXECUTE };

    private static final String OPT_RESOURCE_ID = "resourceId";
    private static final String OPT_ACTION      = "action";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }
    
    public String getName() {
        return "control";
     }

    public int handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return 1;
        }

        if (args[0].equals(CMD_ACTIONS)) {
            actions(trim(args));
        } else if (args[0].equals(CMD_HISTORY)){
            history(trim(args));
        } else if (args[0].equals(CMD_EXECUTE)) {
            execute(trim(args));
        } else {
            printUsage();
            return 1;
        }
        return 0;
    }

    private void actions(String[] args) throws Exception {
        OptionParser p = getOptionParser();

        p.accepts(OPT_RESOURCE_ID, "The resource id to get actions for").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi rApi = api.getResourceApi();
        ControlApi cApi = api.getControlApi();

        Integer resourceId = (Integer)getRequired(options, OPT_RESOURCE_ID);
        ResourceResponse resourceResponse = rApi.getResource(resourceId, false, false);
        checkSuccess(resourceResponse);

        ControlActionResponse response = cApi.getActions(resourceResponse.getResource());
        checkSuccess(response);

        System.out.println("Control actions for " + resourceResponse.getResource().getName());
        for (String action : response.getAction()) {
            System.out.println(" - " + action);
        }
    }

    private void history(String[] args) throws Exception {
        OptionParser p = getOptionParser();

        p.accepts(OPT_RESOURCE_ID, "The resource id to get actions for").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi rApi = api.getResourceApi();
        ControlApi cApi = api.getControlApi();

        Integer resourceId = (Integer)getRequired(options, OPT_RESOURCE_ID);
        ResourceResponse resourceResponse = rApi.getResource(resourceId, false, false);
        checkSuccess(resourceResponse);

        ControlHistoryResponse response = cApi.getHistory(resourceResponse.getResource());
        checkSuccess(response);

        System.out.println("Control history for " + resourceResponse.getResource().getName());
        final DateFormat df = SimpleDateFormat.getInstance();
        for (ControlHistory h : response.getControlHistory()) {
            System.out.println(df.format(new Date(h.getStartTime())) +
                    " action=" + h.getAction() +
                    " dur=" + (h.getEndTime() - h.getStartTime()) +
                    " status=" + h.getStatus());
        }
    }

    private void execute(String[] args) throws Exception {
        OptionParser p = getOptionParser();

        p.accepts(OPT_RESOURCE_ID, "The resource id to get actions for").
                withRequiredArg().ofType(Integer.class);

        p.accepts(OPT_ACTION, "The control action to run")
                .withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi rApi = api.getResourceApi();
        ControlApi cApi = api.getControlApi();

        String action = (String)getRequired(options, OPT_ACTION);
        Integer resourceId = (Integer)getRequired(options, OPT_RESOURCE_ID);
        ResourceResponse resourceResponse = rApi.getResource(resourceId, false, false);
        checkSuccess(resourceResponse);

        String[] arguments = options.nonOptionArguments().
                toArray(new String[options.nonOptionArguments().size()]);
        StatusResponse response = cApi.executeAction(resourceResponse.getResource(),
                                                     action, arguments);
        checkSuccess(response);

        System.out.println("Ran action '" + action + "' on " +
                resourceResponse.getResource().getName());
    }
}
