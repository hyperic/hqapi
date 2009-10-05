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

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.AlertApi;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.AlertResponse;

public class AlertCommand extends Command {

    private static String CMD_LIST   = "list";
    private static String CMD_ACK    = "ack";
    private static String CMD_FIX    = "fix";
    private static String CMD_DELETE = "delete";

    private static String[] COMMANDS = { CMD_LIST, CMD_ACK, CMD_FIX, CMD_DELETE };

    private static String OPT_ID          = "id";
    private static String OPT_RESOURCE_ID = "resourceId";
    private static String OPT_COUNT       = "count";
    private static String OPT_BEGIN       = "begin";
    private static String OPT_END         = "end";
    private static String OPT_INESC[]     = { "inEsc" };
    private static String OPT_NOTFIXED[]  = { "notFixed" };
    private static String OPT_SEVERITY    = "severity";
    private static String OPT_REASON      = "reason";
    private static String OPT_PAUSE       = "pause";

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
        } else if (args[0].equals(CMD_FIX)) {
            fix(trim(args));
        } else if (args[0].equals(CMD_ACK)) {
            ack(trim(args));
        } else if (args[0].equals(CMD_DELETE)) {
            delete(trim(args));
        } else {
            printUsage();
            System.exit(-1);
        }
    }

    private void list(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_RESOURCE_ID, "If specified, only return alerts for the " +
                                   "given resource.").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_SEVERITY, "If specified, the minimum severity of alerts " +
                                "to include. (LOW=1, HIGH=3) Default = 1.").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_BEGIN, "If specified, the begin time in epoch-millis.").
                withRequiredArg().ofType(Long.class);
        p.accepts(OPT_END, "If specified, the end time in epoch-millis.").
                withRequiredArg().ofType(Long.class);
        p.accepts(OPT_COUNT, "The maximum number of alerts to return.")
                .withRequiredArg().ofType(Integer.class);
        p.acceptsAll(Arrays.asList(OPT_INESC),
                    "If specified, only return alerts which are in " +
                    "escalation.");
        p.acceptsAll(Arrays.asList(OPT_NOTFIXED),
                     "If specified, only return alerts which are not fixed.");

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        AlertApi alertApi = api.getAlertApi();

        Integer severity = (Integer)options.valueOf(OPT_SEVERITY);
        if (severity == null) {
            severity = 1;
        }

        Long begin = (Long)options.valueOf(OPT_BEGIN);
        if (begin == null) {
            begin = 0l;
        }
        
        Long end = (Long)options.valueOf(OPT_END);
        if (end == null) {
            end = System.currentTimeMillis();
        }

        // --count is required
        Integer count = (Integer)getRequired(options, OPT_COUNT);

        Boolean inEsc = options.has(OPT_INESC[0]);
        Boolean notFixed = options.has(OPT_NOTFIXED[0]);

        AlertsResponse alerts;
        if (options.has(OPT_RESOURCE_ID)) {
            Integer rid = (Integer)options.valueOf(OPT_RESOURCE_ID);
            ResourceResponse resource = api.getResourceApi().
                    getResource(rid, false, false);
            checkSuccess(resource);
            alerts = alertApi.findAlerts(resource.getResource(), begin,
                                         end, count, severity, inEsc, notFixed);
        } else {
            alerts = alertApi.findAlerts(begin, end, count,
                                         severity, inEsc, notFixed);
        }

        checkSuccess(alerts);
        XmlUtil.serialize(alerts, System.out, Boolean.TRUE);
    }

    private void fix(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "The id of the Alert to fix").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        AlertApi alertApi = api.getAlertApi();

        Integer id = (Integer)getRequired(options, OPT_ID);

        AlertResponse response = alertApi.fixAlert(id);
        checkSuccess(response);

        System.out.println("Successfully fixed alert id " + id);
    }

    private void ack(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "The id of the Alert to acknowledge").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_REASON, "The reason for acknoledging the alert")
                .withRequiredArg().ofType(String.class);
        p.accepts(OPT_PAUSE, "If specified, pause the Escalation for the " +
                             "given number of milliseconds")
                .withRequiredArg().ofType(Long.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        AlertApi alertApi = api.getAlertApi();

        Integer id = (Integer)getRequired(options, OPT_ID);
        String reason = (String)options.valueOf(OPT_REASON);
        long pause;
        if (options.has(OPT_PAUSE)) {
            pause = (Long)options.valueOf(OPT_PAUSE);
        } else {
            pause = 0l;
        }

        AlertResponse response = alertApi.ackAlert(id, reason, pause);
        checkSuccess(response);

        System.out.println("Successfully acknowledged alert id " + id);
    }

    private void delete(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "The id of the Alert to delete").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        AlertApi alertApi = api.getAlertApi();

        Integer id = (Integer)getRequired(options, OPT_ID);

        StatusResponse response = alertApi.delete(id);
        checkSuccess(response);

        System.out.println("Successfully deleted alert id " + id);
    }
}
