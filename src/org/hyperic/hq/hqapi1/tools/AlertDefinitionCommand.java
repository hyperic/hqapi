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
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.types.AlertAction;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.EscalationResponse;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
@Component
public class AlertDefinitionCommand extends AbstractCommand {

    private static String CMD_LIST   = "list";
    private static String CMD_SYNC   = "sync";
    private static String CMD_DELETE = "delete";

    private static String[] COMMANDS = { CMD_LIST, CMD_DELETE, CMD_SYNC };

    private static String OPT_TYPEALERTS = "typeAlerts";
    private static String OPT_EXCLUDE_TYPEALERTS = "excludeTypeAlerts";
    private static String OPT_EXCLUDE_IDS = "excludeTypeIds";
    private static String OPT_GROUP = "group";
    private static String OPT_RESOURCE_NAME = "resourceName";
    private static String OPT_ALERT_NAME = "alertName";
    private static String OPT_ID   = "id";
    private static String OPT_BATCH_SIZE = "batchSize";
    private static String OPT_ESCLATION = "escalation";
    private static String OPT_COND_COUNT = "conditionCount";
    private static String OPT_COND_INCLUDE = "conditionTypeInclude";
    private static String OPT_COND_EXCLUDE = "conditionTypeExclude";

    // Command line syncing options
    private static String OPT_ASSIGN_ESC = "assignEscalation";
    private static String OPT_ASSIGN_SCRIPTACTION = "assignScriptAction";
    private static String OPT_ASSIGN_CONTROLACTION = "assignControlAction";
    private static String OPT_CLEAR_ESC = "clearEscalation";
    private static String OPT_CLEAR_ACTIONS = "clearActions";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }
    
    public String getName() {
        return "alertdefinition";
    }
    
    public int handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return 1;
        }

        if (args[0].equals(CMD_LIST)) {
            list(trim(args));
        } else if (args[0].equals(CMD_DELETE)) {
            delete(trim(args));
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

        p.accepts(OPT_TYPEALERTS, "If specified, only parent resource type " +
                                  "alerts will be returned.");
        p.accepts(OPT_EXCLUDE_TYPEALERTS, "If specified, individual alerts " +
                                          "based on resource type alerts will " +
                                          "be excluded.");
        p.accepts(OPT_EXCLUDE_IDS, "If specified, parent alert definitions will " +
                                   "not include alert ids.");
        p.accepts(OPT_GROUP, "If specified, only show alert definitions for " +
                             "resources that belong to the specified group.").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_RESOURCE_NAME, "If specified, only show alert definitions " +
                                     "belonging to a resource with the given " +
                                     "resource name regex.").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_ALERT_NAME, "If specified, only show alert definitions " +
                                   "with names that match the given regex.").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_ESCLATION, "If specified, only show alert definitions " +
                                 "that are tied to the named escalation.").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_COND_COUNT, "If specified, only show alert definitions " +
                                  "which have the number of conditions " +
                                  "specified")
                .withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_COND_INCLUDE, "If specified, only show alert definitions " +
                                    "which have at least one condition of the " +
                                    "given type")
                .withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_COND_EXCLUDE, "If specified, exclude alert definitions " +
                                    "which have at least one condition of the " +
                                    "given type")
                .withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        AlertDefinitionApi definitionApi = api.getAlertDefinitionApi();
        EscalationApi escalationApi = api.getEscalationApi();

        AlertDefinitionsResponse alertDefs;
        
        if (options.has(OPT_TYPEALERTS)) {
            boolean excludeIds = false;
            if (options.has(OPT_EXCLUDE_IDS)) {
                excludeIds = true;
            }
            
            alertDefs = definitionApi.getTypeAlertDefinitions(excludeIds);
        } else {
            boolean excludeTypeAlerts = false;
            if (options.has(OPT_EXCLUDE_TYPEALERTS)) {
                excludeTypeAlerts = true;
            }
            if (options.has(OPT_EXCLUDE_IDS)) {
                System.err.println("Option " + OPT_EXCLUDE_IDS + " only valid " +
                                   " when " + OPT_TYPEALERTS + " is specified.");
                System.exit(-1);
            }

            String alertNameFilter = (String)options.valueOf(OPT_ALERT_NAME);
            String resourceNameFilter = (String)options.valueOf(OPT_RESOURCE_NAME);
            String groupNameFilter = (String)options.valueOf(OPT_GROUP);
            Escalation escalation = null;

            if (options.has(OPT_ESCLATION)) {
                EscalationResponse escalationResponse = escalationApi.
                        getEscalation((String)getRequired(options, OPT_ESCLATION));
                checkSuccess(escalationResponse);
                escalation = escalationResponse.getEscalation();
            }

            alertDefs = definitionApi.getAlertDefinitions(excludeTypeAlerts,
                                                          escalation,
                                                          alertNameFilter,
                                                          resourceNameFilter,
                                                          groupNameFilter);
        }

        if (options.has(OPT_COND_COUNT)) {
            Integer count = (Integer)getRequired(options, OPT_COND_COUNT);
            for (Iterator<AlertDefinition> i =
                    alertDefs.getAlertDefinition().iterator(); i.hasNext(); ) {
                AlertDefinition d = i.next();
                if (d.getAlertCondition().size() != count) {
                    i.remove();
                }
            }
        }

        if (options.has(OPT_COND_INCLUDE)) {
            Integer type = (Integer)getRequired(options, OPT_COND_INCLUDE);
            for (Iterator<AlertDefinition> i =
                    alertDefs.getAlertDefinition().iterator(); i.hasNext(); ) {
                AlertDefinition d = i.next();
                boolean hasType = false;
                for (AlertCondition c : d.getAlertCondition()) {
                    if (c.getType() == type) {
                        hasType = true;
                    }
                }
                if (!hasType) {
                    i.remove();
                }
            }
        }

        if (options.has(OPT_COND_EXCLUDE)) {
            Integer type = (Integer)getRequired(options, OPT_COND_EXCLUDE);
            for (Iterator<AlertDefinition> i =
                    alertDefs.getAlertDefinition().iterator(); i.hasNext(); ) {
                AlertDefinition d = i.next();
                for (AlertCondition c : d.getAlertCondition()) {
                    if (c.getType() == type) {
                        i.remove();
                        break;
                    }
                }
            }
        }

        checkSuccess(alertDefs);
        XmlUtil.serialize(alertDefs, System.out, Boolean.TRUE);        
    }

    private void delete(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "The alert definition id to delete").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        AlertDefinitionApi definitionApi = api.getAlertDefinitionApi();

        Integer id = (Integer)getRequired(options, OPT_ID);
        StatusResponse deleteResponse =
                definitionApi.deleteAlertDefinition(id);
        checkSuccess(deleteResponse);

        System.out.println("Successfully deleted alert definition id " + id);
    }

    private void sync(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_BATCH_SIZE, "Process the sync in batches of the given size").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_ASSIGN_ESC, "If specified, assign the given Escalation " +
                                   "to all alert definitions in this sync").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_ASSIGN_SCRIPTACTION, "If specified, assign the given Escalation " +
                                           "to all alert definitions in this sync").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_ASSIGN_CONTROLACTION, "If specified, assign the given Escalation " +
                                            "to all alert definitions in this sync").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_CLEAR_ESC, "If specified, clear the assigned escalation from " +
                                 "all alert definitions in this sync");
        p.accepts(OPT_CLEAR_ACTIONS, "If specified, clear alert actions from " +
                                     "all alert definitions in this sync");

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        AlertDefinitionApi adApi = api.getAlertDefinitionApi();
        EscalationApi escApi = api.getEscalationApi();

        InputStream is = getInputStream(options);

        AlertDefinitionsResponse resp =
                XmlUtil.deserialize(AlertDefinitionsResponse.class, is);
        
        List<AlertDefinition> definitions = resp.getAlertDefinition();

        if (options.has(OPT_ASSIGN_ESC)) {
            String esc = (String)getRequired(options, OPT_ASSIGN_ESC);
            EscalationResponse escResponse = escApi.getEscalation(esc);
            checkSuccess(escResponse);
            System.out.println("Assigning escalation '" + esc + "' to all alert definitions");

            for (AlertDefinition a : definitions) {
                a.setEscalation(escResponse.getEscalation());
            }
        }

        if (options.has(OPT_ASSIGN_SCRIPTACTION)) {
            String script = (String)getRequired(options, OPT_ASSIGN_SCRIPTACTION);
            AlertAction a = AlertDefinitionBuilder.createScriptAction(script);
            System.out.println("Assigning script action '" + script + "' to all alert definitions");

            for (AlertDefinition def : definitions) {
                def.getAlertAction().add(a);
            }
        }

        if (options.has(OPT_ASSIGN_CONTROLACTION)) {
            String action = (String)getRequired(options, OPT_ASSIGN_CONTROLACTION);
            System.out.println("Assigning control action '" + action + "' to all alert definitions");

            for (AlertDefinition def : definitions) {
                AlertAction a = AlertDefinitionBuilder.createControlAction(def.getResource(), action);
                def.getAlertAction().add(a);
            }
        }

        if (options.has(OPT_CLEAR_ESC)) {
            System.out.println("Clearing escalations for all alert definitions");

            for (AlertDefinition def : definitions) {
                def.setEscalation(null);
            }
        }

        if (options.has(OPT_CLEAR_ACTIONS)) {
            System.out.println("Clearing alert actions for all alert definitions");

            for (AlertDefinition def : definitions) {
                def.getAlertAction().clear();
            }
        }

        System.out.println("Syncing " + definitions.size() + " alert definitions");

        int numSynced = 0;
        if (options.has(OPT_BATCH_SIZE)) {
            int batchSize = (Integer)options.valueOf(OPT_BATCH_SIZE);
            int numBatches = (int)Math.ceil(definitions.size()/((double)batchSize));

            for (int i = 0; i < numBatches; i++) {
                long start = System.currentTimeMillis();
                int fromIndex = i * batchSize;
                int toIndex = (fromIndex + batchSize) > definitions.size() ? 
                              definitions.size() : (fromIndex + batchSize);
                AlertDefinitionsResponse syncResponse =
                        adApi.syncAlertDefinitions(definitions.subList(fromIndex,
                                                                       toIndex));
                checkSuccess(syncResponse);
                numSynced += (toIndex - fromIndex);
                System.out.println("Synced batch " + (i + 1) + " of " + numBatches + " in " +
                                   (System.currentTimeMillis() - start) + " ms");

            }
        } else {
            AlertDefinitionsResponse syncResponse = adApi.syncAlertDefinitions(definitions);
            checkSuccess(syncResponse);
            numSynced = definitions.size();
        }

        System.out.println("Successfully synced " + numSynced + " alert definitions.");
    }
}
