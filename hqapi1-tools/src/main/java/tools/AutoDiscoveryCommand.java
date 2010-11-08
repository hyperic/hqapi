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
import org.hyperic.hq.hqapi1.AutodiscoveryApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.AIPlatform;
import org.hyperic.hq.hqapi1.types.QueueResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component
public class AutoDiscoveryCommand extends AbstractCommand {

    private static String CMD_LIST    = "list";
    private static String CMD_APPROVE = "approve";

    private static String[] COMMANDS = { CMD_LIST, CMD_APPROVE };

    private static String OPT_REGEX = "regex";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }

    
    public String getName() {
        return "autodiscovery";
     }
    
    public int handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return 1;
        }

        if (args[0].equals(CMD_LIST)) {
            list(trim(args));
        } else if (args[0].equals(CMD_APPROVE)) {
            approve(trim(args));
        } else {
            printUsage();
            return 1;
        }
        return 0;
    }

    private void list(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        AutodiscoveryApi autodiscoveryApi = api.getAutodiscoveryApi();

        QueueResponse queue = autodiscoveryApi.getQueue();
        checkSuccess(queue);

        XmlUtil.serialize(queue, System.out, Boolean.TRUE);
    }

    private void approve(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_REGEX, "If specified, only platforms that match the given " +
                  "regular expression will be approved").
                withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);


        Pattern pattern;
        if (options.has(OPT_REGEX)) {
            pattern = Pattern.compile((String)options.valueOf(OPT_REGEX));
        } else {
            pattern = Pattern.compile(".*");
        }

        HQApi api = getApi(options);
        AutodiscoveryApi autodiscoveryApi = api.getAutodiscoveryApi();

        QueueResponse queue = autodiscoveryApi.getQueue();
        checkSuccess(queue);

        int num = 0;
        for (AIPlatform plat : queue.getAIPlatform()) {
            Matcher m = pattern.matcher(plat.getName());
            if (m.matches()) {
                System.out.println("Approving " + plat.getName());
                StatusResponse approveResponse = autodiscoveryApi.approve(plat.getId());
                checkSuccess(approveResponse);
                num++;
            }
        }
        System.out.println("Approved " + num + " platforms.");
    }
}
