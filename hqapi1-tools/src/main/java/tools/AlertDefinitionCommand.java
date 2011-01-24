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
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.types.AlertAction;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionResponse;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RoleResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.EscalationResponse;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.springframework.stereotype.Component;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AlertDefinitionCommand extends AbstractCommand {

    private static String CMD_LIST   = "list";
    private static String CMD_SYNC   = "sync";
    private static String CMD_DELETE = "delete";
    private static String CMD_CREATE = "create";

    private static String[] COMMANDS = { CMD_LIST, CMD_DELETE, CMD_SYNC, CMD_CREATE };

    private static String OPT_TYPEALERTS = "typeAlerts";
    private static String OPT_EXCLUDE_TYPEALERTS = "excludeTypeAlerts";
    private static String OPT_EXCLUDE_IDS = "excludeTypeIds";
    private static String OPT_GROUP = "group";
    private static String OPT_RESOURCE_NAME = "resourceName";
    private static String OPT_RESOURCE_DESC = "resourceDescription";
    private static String OPT_ALERT_NAME = "alertName";
    private static String OPT_ALERT_PRIORITY = "alertPriority";
    private static String OPT_ID   = "id";
    private static String OPT_BATCH_SIZE = "batchSize";
    private static String OPT_ESCLATION = "escalation";
    private static String OPT_COND_COUNT = "conditionCount";
    private static String OPT_COND_INCLUDE = "conditionTypeInclude";
    private static String OPT_COND_EXCLUDE = "conditionTypeExclude";
    private static String OPT_PLATFORM = "platform";

    // Command line syncing options
    private static String OPT_ASSIGN_ESC = "assignEscalation";
    private static String OPT_ASSIGN_SCRIPTACTION = "assignScriptAction";
    private static String OPT_ASSIGN_CONTROLACTION = "assignControlAction";
    private static String OPT_CLEAR_ESC = "clearEscalation";
    private static String OPT_CLEAR_ACTIONS = "clearActions";
    private static String OPT_ASSIGN_USER_NOTIFICATION = "assignUserNotification";
    private static String OPT_ASSIGN_ROLE_NOTIFICATION = "assignRoleNotification";
    private static String OPT_ASSIGN_OTHER_NOTIFICATION = "assignOtherNotification";
    private static String OPT_REGEX = "regex";
    private static String OPT_PROTOTYPE = "prototype";
    
    // Options for create command
    private static String OPT_TEMPLATEDEFINITIONID = "templateDefinition";
    private static String OPT_RESOURCEID = "resourceid";
    private static String OPT_METRIC = "metric";
    private static String OPT_EQUALS = "equals";
    private static String OPT_NOTEQUALTO = "notequalto";
    private static String OPT_LESSTHAN = "lessthan";
    private static String OPT_GREATERTHAN = "greaterthan";
    private static String OPT_RECOVERYEQUALS = "recoveryequals";
    private static String OPT_RECOVERYNOTEQUALTO = "recoverynotequalto";
    private static String OPT_RECOVERYLESSTHAN = "recoverylessthan";
    private static String OPT_RECOVERYGREATERTHAN = "recoverygreaterthan";
    private static String OPT_RECOVERYNAME = "recoveryname";
    private static String OPT_NAME = "name";
    private static String OPT_PRIORITY = "priority";
    private static String OPT_WILLRECOVER = "willrecover";

    private static Integer DEFAULT_PRIORITY = 2; // Assume medium if not specified
    
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
        } else if (args[0].equals(CMD_CREATE)) {
            create(trim(args));
        } else {
            printUsage();
            return 1;
        }
        return 0;
    }

    private void list(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "If specified, return the alert definition with the given id.").
                withRequiredArg().ofType(Integer.class);
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
        p.accepts(OPT_PLATFORM, "Return all alerts on the given platform and " +
                                "all descendant children").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_RESOURCE_NAME, "If specified, only show alert definitions " +
                                     "belonging to a resource with the given " +
                                     "resource name regex.").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_RESOURCE_DESC, "If specified, only show alert definitions " +
                                     "belonging to a resource with a description " +
                                     "matching in whole or part the given description").
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
        p.accepts(OPT_ALERT_PRIORITY, "If specified, only include alerts with " +
                                      "the given priority.  3 = high, 2 = medium, " +
                                      "1 = low")
                .withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        AlertDefinitionApi definitionApi = api.getAlertDefinitionApi();
        EscalationApi escalationApi = api.getEscalationApi();
        ResourceApi rApi = api.getResourceApi();

        AlertDefinitionsResponse alertDefs;
        
        if (options.has(OPT_TYPEALERTS)) {
            boolean excludeIds = false;
            if (options.has(OPT_EXCLUDE_IDS)) {
                excludeIds = true;
            }
            
            alertDefs = definitionApi.getTypeAlertDefinitions(excludeIds);
        } else if (options.has(OPT_PLATFORM)) {
            String platformName = (String)getRequired(options, OPT_PLATFORM);
            ResourceResponse resourceResponse =
                    rApi.getPlatformResource(platformName, false, false);
            checkSuccess(resourceResponse);

            alertDefs = definitionApi.getAlertDefinitions(resourceResponse.getResource(), true);
        } else if (options.has(OPT_RESOURCE_DESC)) {
            String description = (String)getRequired(options, OPT_RESOURCE_DESC);
            ResourcesResponse resourcesResponse =
                    rApi.getResources(description, false, false);
            checkSuccess(resourcesResponse);
            alertDefs = definitionApi.getAlertDefinitions(resourcesResponse.getResource());
        } else if (options.has(OPT_ID)) {
            Integer id = (Integer)getRequired(options, OPT_ID);
            AlertDefinitionResponse response =
                    definitionApi.getAlertDefinition(id);
            checkSuccess(response);
            XmlUtil.serialize(response, System.out, Boolean.TRUE);
            return;
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

        if (options.has(OPT_ALERT_PRIORITY)) {
            Integer priority = (Integer)getRequired(options, OPT_ALERT_PRIORITY);
            for (Iterator<AlertDefinition> i =
                    alertDefs.getAlertDefinition().iterator(); i.hasNext(); ) {
                AlertDefinition d = i.next();
                if (d.getPriority() != priority) {
                    i.remove();
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
        p.accepts(OPT_ASSIGN_SCRIPTACTION, "If specified, assign the given script action " +
                                           "to all alert definitions in this sync").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_ASSIGN_CONTROLACTION, "If specified, assign the given Control Action " +
                                            "to all alert definitions in this sync").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_CLEAR_ESC, "If specified, clear the assigned escalation from " +
                                 "all alert definitions in this sync");
        p.accepts(OPT_CLEAR_ACTIONS, "If specified, clear alert actions from " +
                                     "all alert definitions in this sync");
        p.accepts(OPT_ASSIGN_USER_NOTIFICATION, "If specified, assign notification to the given " +
                                                "comma separated list of users").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_ASSIGN_ROLE_NOTIFICATION, "If specified, assign notification to the given " +
                                                "comma separated list of roles").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_ASSIGN_OTHER_NOTIFICATION, "If specified, assign notification to the given " +
                                                "comma separated list of email addresses").
                withRequiredArg().ofType(String.class);

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

        if (options.has(OPT_ASSIGN_USER_NOTIFICATION)) {
            String[] users = ((String)getRequired(options, OPT_ASSIGN_USER_NOTIFICATION)).split(",");
            List<User> checkedUsers = new ArrayList<User>();
            for (String u : users) {
                UserResponse uResponse = api.getUserApi().getUser(u);
                checkSuccess(uResponse);
                checkedUsers.add(uResponse.getUser());
            }

            for (AlertDefinition def : definitions) {
                AlertDefinitionBuilder.addEmailAction(def, checkedUsers.toArray(new User[checkedUsers.size()]));
            }
        }

        if (options.has(OPT_ASSIGN_ROLE_NOTIFICATION)) {
            String[] roles = ((String)getRequired(options, OPT_ASSIGN_ROLE_NOTIFICATION)).split(",");
            ArrayList<Role> checkedRoles = new ArrayList<Role>();
            for (String r : roles) {
                RoleResponse rResponse = api.getRoleApi().getRole(r);
                checkSuccess(rResponse);
                checkedRoles.add(rResponse.getRole());
            }

            for (AlertDefinition def : definitions) {
                AlertDefinitionBuilder.addEmailAction(def, checkedRoles.toArray(new Role[checkedRoles.size()]));
            }
        }

        if (options.has(OPT_ASSIGN_OTHER_NOTIFICATION)) {
            String[] emails = ((String)getRequired(options, OPT_ASSIGN_OTHER_NOTIFICATION)).split(",");

            for (AlertDefinition def : definitions) {
                AlertDefinitionBuilder.addEmailAction(def, emails);
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

    private void create(String[] args) throws Exception {
        String[] ONE_CMD_REQUIRED = { OPT_GROUP, OPT_NAME, OPT_TYPEALERTS };
        String[] TEMPLATE_REQUIRED = { OPT_TEMPLATEDEFINITIONID };
        String[] ONE_COMP_REQUIRED = { OPT_EQUALS, OPT_LESSTHAN, OPT_GREATERTHAN, OPT_NOTEQUALTO };
        String[] ONE_COMP_RECOVERYREQUIRED = { OPT_RECOVERYEQUALS, OPT_RECOVERYLESSTHAN, 
        		OPT_RECOVERYGREATERTHAN, OPT_RECOVERYNOTEQUALTO };
        String[] NEW_REQUIRED = { OPT_NAME, OPT_METRIC }; 
        
        OptionParser p = getOptionParser();

        p.accepts(OPT_BATCH_SIZE, "Process the create in batches of the given size").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_ASSIGN_ESC, "If specified, assign the given Escalation " +
                "to all alert definitions in this create").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_ASSIGN_SCRIPTACTION, "If specified, assign the given script action " +
                "to all alert definitions in this create").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_ASSIGN_CONTROLACTION, "If specified, assign the given Control Action " +
                "to all alert definitions in this create").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_REGEX, "Use pattern to find matching resources").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_GROUP, "Create only for resources in the specified group").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_PROTOTYPE, "Specifies the prototype to find. ").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_TEMPLATEDEFINITIONID, "The id of the alert definition to use as a template").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_NAME, "The name of the alert definition to create." + 
        		"Not valid with --" + OPT_TEMPLATEDEFINITIONID ).
        		withRequiredArg().ofType(String.class);
        p.accepts(OPT_METRIC, "The metric name to use. " +
        		"Not valid with --" + OPT_TEMPLATEDEFINITIONID ).
        		withRequiredArg().ofType(String.class);
        p.accepts(OPT_RECOVERYNAME, "Optionally specify name of recovery alert. If not specified" + 
        		" then \"fixed\" is added to  alert definition name").
        		withRequiredArg().ofType(String.class);
        p.accepts(OPT_RECOVERYEQUALS, "Sets the recovery alert to if metric value = threshold").
        		withRequiredArg().ofType(Double.class);
        p.accepts(OPT_EQUALS, "Sets condition to if metric value = threshold").
        		withRequiredArg().ofType(Double.class);
        p.accepts(OPT_RECOVERYLESSTHAN, "Sets the recovery alert to if metric value < threshold").
        		withRequiredArg().ofType(Double.class);
        p.accepts(OPT_LESSTHAN, "Sets condition to if metric value < threshold").
        		withRequiredArg().ofType(Double.class);
        p.accepts(OPT_RECOVERYGREATERTHAN, "Sets the recovery alert to if metric value > threshold").
        		withRequiredArg().ofType(Double.class);
        p.accepts(OPT_GREATERTHAN, "Sets condition to if metric value > threshold").
				withRequiredArg().ofType(Double.class);
        p.accepts(OPT_RECOVERYNOTEQUALTO, "Sets the recovery alert to if metric value != threshold").
				withRequiredArg().ofType(Double.class);
        p.accepts(OPT_NOTEQUALTO, "Sets condition to if metric value != threshold").
				withRequiredArg().ofType(Double.class);
        p.accepts(OPT_PRIORITY, "Sets priority. 1 = low, 2 = medium(default), 3 = high").
        		withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_TYPEALERTS, "If specified only resource type " +
        		"alerts will be created.");
        p.accepts(OPT_WILLRECOVER, "If specified sets the willRecover flag." +
        		"Equivilent to \'Generate one alert and then disable alert definition until fixed\"");
        
        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        AlertDefinitionApi adApi = api.getAlertDefinitionApi();
        EscalationApi escApi = api.getEscalationApi();
        ResourceApi resourceApi = api.getResourceApi();
        GroupApi groupApi = api.getGroupApi();
        ResourcesResponse resources = new ResourcesResponse();
        AlertDefinition tmpl = null;
        AlertDefinition recoverytmpl = null;
        boolean verbose = false;
        boolean children = false;
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();

        // Build the template to use. If we're not basing it off an existing definition
        // we need to build it from scratch
        if (options.has(OPT_TEMPLATEDEFINITIONID)) {
            AlertDefinitionResponse alertTemplateResponse = adApi.getAlertDefinition((Integer) options.valueOf(OPT_TEMPLATEDEFINITIONID));
            checkSuccess(alertTemplateResponse);
            tmpl = alertTemplateResponse.getAlertDefinition();       	
        } else if (options.has(OPT_NAME) && options.has(OPT_METRIC)) {
        	Double threshold = 0.0d;
        	Integer priority; 
        	AlertDefinitionBuilder.AlertComparator comparator = null;
        	Double recoverythreshold = 0.0d;
        	AlertDefinitionBuilder.AlertComparator recoverycomparator = null;
            String recoveryname = new String();
            boolean willrecover = false;
            
            if (options.has(OPT_WILLRECOVER)) {
            	willrecover = true;
            }
            
        	if (options.has(OPT_EQUALS)) {
        		threshold = (Double)options.valueOf(OPT_EQUALS);
        		comparator = AlertDefinitionBuilder.AlertComparator.valueOf("EQUALS");
        	} else if (options.has(OPT_LESSTHAN)) {
        		threshold = (Double)options.valueOf(OPT_LESSTHAN);
        		comparator = AlertDefinitionBuilder.AlertComparator.valueOf("LESS_THAN");
        	} else if (options.has(OPT_GREATERTHAN)) {
        		threshold = (Double)options.valueOf(OPT_GREATERTHAN);
        		comparator = AlertDefinitionBuilder.AlertComparator.valueOf("GREATER_THAN");
        	} else if (options.has(OPT_NOTEQUALTO)) {
        		threshold = (Double)options.valueOf(OPT_NOTEQUALTO);
        		comparator = AlertDefinitionBuilder.AlertComparator.valueOf("NOT_EQUALS");
        	} else {
        		System.err.println("Only one of " + Arrays.toString(ONE_COMP_REQUIRED) + " may be specified");
        		System.exit(-1);
        	}
        	
        	if (options.has(OPT_PRIORITY)) {
        		priority = (Integer) options.valueOf(OPT_PRIORITY);
        	} else {
        		priority = DEFAULT_PRIORITY; 	
        	}
        	
        	tmpl = buildThresholdAlertDefinition(options.valueOf(OPT_NAME).toString(), 
        			options.valueOf(OPT_METRIC).toString(), comparator, threshold, priority, willrecover);
        	
        	if (options.has(OPT_RECOVERYEQUALS)) {
        		recoverythreshold = (Double)options.valueOf(OPT_RECOVERYEQUALS);
        		recoverycomparator = AlertDefinitionBuilder.AlertComparator.valueOf("EQUALS");
        	} else if (options.has(OPT_RECOVERYLESSTHAN)) {
        		recoverythreshold = (Double)options.valueOf(OPT_RECOVERYLESSTHAN);
        		recoverycomparator = AlertDefinitionBuilder.AlertComparator.valueOf("LESS_THAN");
        	} else if (options.has(OPT_RECOVERYGREATERTHAN)) {
        		recoverythreshold = (Double)options.valueOf(OPT_RECOVERYGREATERTHAN);
        		recoverycomparator = AlertDefinitionBuilder.AlertComparator.valueOf("LESS_THAN");
        	} else if (options.has(OPT_RECOVERYNOTEQUALTO)) {
        		threshold = (Double)options.valueOf(OPT_RECOVERYNOTEQUALTO);
        		recoverycomparator = AlertDefinitionBuilder.AlertComparator.valueOf("LESS_THAN");
        	} 
        	
        	if (options.has(OPT_RECOVERYNAME)) {
        		recoveryname = options.valueOf(OPT_RECOVERYNAME).toString();
        	} else { // we probably should only do this if we are creating a recovery alert
        		recoveryname = options.valueOf(OPT_NAME).toString() + " fixed";
        	} 

        	if (recoverycomparator != null) {
        		recoverytmpl = buildThresholdAlertDefinition(recoveryname, 
        			options.valueOf(OPT_METRIC).toString(), recoverycomparator, recoverythreshold, priority, false);

        		recoverytmpl.getAlertCondition().add(AlertDefinitionBuilder.createRecoveryCondition(true, tmpl));
        	}

        } else {
            System.err.println("required option missing");
            System.exit(-1);        	
        	
        }
        
        if (options.has(OPT_REGEX)) {
            String prototype = (String) options.valueOf(OPT_PROTOTYPE);
            ResourcePrototypeResponse protoResponse =
                    resourceApi.getResourcePrototype(prototype);
            checkSuccess(protoResponse);
            resources = resourceApi.getResources(protoResponse.getResourcePrototype(),
                    verbose, children);
            checkSuccess(resources);

            Pattern pattern = Pattern.compile((String) options.valueOf(OPT_REGEX));

            for (Iterator<Resource> i = resources.getResource().iterator(); i.hasNext();) {
                Resource r = i.next();
                Matcher m = pattern.matcher(r.getName());
                System.out.println("Found " + r.getName());
                if (!m.matches()) {
                    i.remove();
                }
            }
            for (Iterator<Resource> it = resources.getResource().iterator(); it.hasNext();) {
                Resource res = it.next();
                System.out.println("Adding alert definition for " + res.getName());
                definitions.add(cloneAlertDefinitionForResource(tmpl, res));
                if (recoverytmpl != null) {
                	definitions.add(cloneAlertDefinitionForResource(recoverytmpl, res));
                }
            }

        } else if (options.has(OPT_GROUP)) {
            String prototype = (String) options.valueOf(OPT_PROTOTYPE);
            ResourcePrototypeResponse protoResponse =
                    resourceApi.getResourcePrototype(prototype);
            checkSuccess(protoResponse);

            // We don't care here if it's Mixed or Comp Groups.
            // We're going to match up by prototype here in a sec
            String name = (String) getRequired(options, OPT_GROUP);
            GroupResponse groupResponse = groupApi.getGroup(name);
            checkSuccess(groupResponse);
            Group group = groupResponse.getGroup();
            for (Iterator<Resource> i = group.getResource().iterator(); i.hasNext();) {
                Resource r = i.next();
                // The Resource object retrieved from above doesn't include the
                // ResourcePrototype so here we're getting the full resource
                // so we can compare the Prototype.
                Resource resource = resourceApi.getResource(r.getId(), true, false).getResource();
                if (resource.getResourcePrototype().getName().equals(protoResponse.getResourcePrototype().getName())) {
                    System.out.println("Adding " + tmpl.getName() + " to " + resource.getName());
                    definitions.add(cloneAlertDefinitionForResource(tmpl, resource));
                    if (recoverytmpl != null) {
                    	definitions.add(cloneAlertDefinitionForResource(recoverytmpl, resource));
                    }
                }
            }

        } else if (options.has(OPT_TYPEALERTS) && options.has(OPT_PROTOTYPE)) {
        	ResourcePrototype prototype = new ResourcePrototype();
 
        	ResourcePrototypeResponse protoResponse =
                resourceApi.getResourcePrototype(options.valueOf(OPT_PROTOTYPE).toString());
            checkSuccess(protoResponse);
            prototype = protoResponse.getResourcePrototype();

            definitions.add(cloneAlertDefinitionForPrototype(tmpl, prototype));
 
            if (recoverytmpl != null) {
            	definitions.add(cloneAlertDefinitionForPrototype(recoverytmpl, prototype));
            }
            
        } else {
            // Print usage and exit
        	// TODO: Create Resource Type Alert	
        	System.err.println("Only one of " + Arrays.toString(ONE_CMD_REQUIRED) + " may be specified");
            System.exit(-1);
        } 

        if (options.has(OPT_ASSIGN_ESC)) {
            String esc = (String) getRequired(options, OPT_ASSIGN_ESC);
            EscalationResponse escResponse = escApi.getEscalation(esc);
            checkSuccess(escResponse);
            System.out.println("Assigning escalation '" + esc + "' to all alert definitions");

            for (AlertDefinition a : definitions) {
                a.setEscalation(escResponse.getEscalation());
            }
        }

        if (options.has(OPT_ASSIGN_SCRIPTACTION)) {
            String script = (String) getRequired(options, OPT_ASSIGN_SCRIPTACTION);
            AlertAction a = AlertDefinitionBuilder.createScriptAction(script);
            System.out.println("Assigning script action '" + script + "' to all alert definitions");

            for (AlertDefinition def : definitions) {
                def.getAlertAction().add(a);
            }
        }

        if (options.has(OPT_ASSIGN_CONTROLACTION)) {
            String action = (String) getRequired(options, OPT_ASSIGN_CONTROLACTION);
            System.out.println("Assigning control action '" + action + "' to all alert definitions");

            for (AlertDefinition def : definitions) {
                AlertAction a = AlertDefinitionBuilder.createControlAction(def.getResource(), action);
                def.getAlertAction().add(a);
            }
        }

        int numSynced = 0;
        if (options.has(OPT_BATCH_SIZE)) {
            int batchSize = (Integer) options.valueOf(OPT_BATCH_SIZE);
            int numBatches = (int) Math.ceil(definitions.size() / ((double) batchSize));

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

    private AlertDefinition cloneAlertDefinitionForResource(AlertDefinition alertTemplate, Resource resource) {
    	AlertDefinition clone = cloneAlertDefinition(alertTemplate);
    	clone.setResource(resource);
    	return clone;
    }
    
    private AlertDefinition cloneAlertDefinitionForPrototype(AlertDefinition alertTemplate, ResourcePrototype prototype) {
    	AlertDefinition clone = cloneAlertDefinition(alertTemplate);
    	clone.setResourcePrototype(prototype);
    	return clone;
    }
    
    private AlertDefinition cloneAlertDefinition(AlertDefinition alertTemplate) {
        AlertDefinition alertClone = new AlertDefinition();
        alertClone.setName(alertTemplate.getName());
        alertClone.setCount(alertTemplate.getCount());
        alertClone.setDescription(alertTemplate.getDescription());
        alertClone.setEscalation(alertTemplate.getEscalation());
        alertClone.setFrequency(alertTemplate.getFrequency());
        alertClone.setName(alertTemplate.getName());
        alertClone.setPriority(alertTemplate.getPriority());
        alertClone.setRange(alertTemplate.getRange());
        alertClone.setActive(alertTemplate.isActive());
        alertClone.setControlFiltered(alertTemplate.isControlFiltered());
        alertClone.setEnabled(alertTemplate.isEnabled());
        alertClone.setNotifyFiltered(alertTemplate.isNotifyFiltered());
        alertClone.setWillRecover(alertTemplate.isWillRecover());
        alertClone.setId(null); // Set to null to create new alert def
      
        // Special handling for AlertCondition and AlertAction
        List<AlertCondition> alertTemplateCondition = alertTemplate.getAlertCondition();
        List<AlertCondition> alertCloneCondition = alertClone.getAlertCondition();
        for (Iterator<AlertCondition> condition = alertTemplateCondition.iterator(); condition.hasNext();) {
            alertCloneCondition.add(condition.next());
        }
        List<AlertAction> alertTemplateAction = alertTemplate.getAlertAction();
        List<AlertAction> alertCloneAction = alertClone.getAlertAction();
        for (Iterator<AlertAction> action = alertTemplateAction.iterator(); action.hasNext();) {
            alertCloneAction.add(action.next());
        }
        return alertClone;
    }
        
    private AlertDefinition buildThresholdAlertDefinition(String name, String metric, AlertDefinitionBuilder.AlertComparator comparator, Double threshold, Integer priority, boolean willrecover){
    	AlertDefinition def = new AlertDefinition();
    	def.setName(name);
    	def.setPriority(priority);
    	def.setActive(true);
    	def.setWillRecover(willrecover);
    	def.getAlertCondition().add(AlertDefinitionBuilder.
    			createThresholdCondition(true, 
    					metric, 
    					comparator, 
    					threshold));
    	return def;
    }
}
