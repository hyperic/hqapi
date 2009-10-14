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
import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.EscalationsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
@Component
public class EscalationCommand extends AbstractCommand {

    private static String CMD_LIST = "list";
    private static String CMD_SYNC = "sync";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC };

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }
    
    public String getName() {
        return "escalation";
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

    private void list(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        EscalationApi escApi = api.getEscalationApi();

        EscalationsResponse escalations = escApi.getEscalations();
        checkSuccess(escalations);

        XmlUtil.serialize(escalations, System.out, Boolean.TRUE);
    }

    private void sync(String[] args) throws Exception {

        OptionParser p = getOptionParser();
        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        EscalationApi escApi = api.getEscalationApi();

        InputStream is = getInputStream(options);

        EscalationsResponse resp = XmlUtil.deserialize(EscalationsResponse.class,
                                                       is);
        List<Escalation> escalations = resp.getEscalation();

        StatusResponse syncResponse = escApi.syncEscalations(escalations);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + escalations.size() + " " +
                           "escalations.");
    }
}
