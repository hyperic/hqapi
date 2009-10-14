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
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.MetricTemplate;
import org.hyperic.hq.hqapi1.types.MetricTemplatesResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
@Component
public class MetricTemplateCommand extends AbstractCommand {

    private static String CMD_LIST = "list";
    private static String CMD_SYNC = "sync";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC };

    private static String OPT_PROTOTYPE  = "prototype";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }
    
    public String getName() {
        return "metricTemplate";
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

        p.accepts(OPT_PROTOTYPE, "The resource prototype to query for metric " +
                  "templates").
                withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        if (!options.has(OPT_PROTOTYPE)) {
            System.err.println("No resource type given.");
            System.exit(-1);
        }

        String prototype = (String) options.valueOf(OPT_PROTOTYPE);
        ResourcePrototypeResponse protoResponse =
                api.getResourceApi().getResourcePrototype(prototype);
        checkSuccess(protoResponse);

        MetricTemplatesResponse templates =
                api.getMetricApi().getMetricTemplates(protoResponse.getResourcePrototype());
        checkSuccess(templates);
        XmlUtil.serialize(templates, System.out, Boolean.TRUE);
    }

    private void sync(String[] args) throws Exception {

        OptionParser p = getOptionParser();
        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        MetricApi metricApi = api.getMetricApi();

        InputStream is = getInputStream(options);

        MetricTemplatesResponse resp =
                XmlUtil.deserialize(MetricTemplatesResponse.class, is);

        List<MetricTemplate> metricTemplates = resp.getMetricTemplate();

        StatusResponse syncResponse =
                metricApi.syncMetricTemplates(metricTemplates);

        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + metricTemplates.size() +
                           " templates.");
    }
}
