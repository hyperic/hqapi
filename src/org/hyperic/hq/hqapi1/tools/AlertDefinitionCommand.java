package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.EscalationResponse;
import org.hyperic.hq.hqapi1.types.Escalation;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class AlertDefinitionCommand extends Command {

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
        } else if (args[0].equals(CMD_DELETE)) {
            delete(trim(args));
        } else if (args[0].equals(CMD_SYNC)) {
            sync(trim(args));
        } else {
            printUsage();
            System.exit(-1);
        }
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

        OptionSet options = getOptions(p, args);

        AlertDefinitionApi api = getApi(options).getAlertDefinitionApi();

        InputStream is = getInputStream(options);

        AlertDefinitionsResponse resp =
                XmlUtil.deserialize(AlertDefinitionsResponse.class, is);
        
        List<AlertDefinition> definitions = resp.getAlertDefinition();

        System.out.println("Syncing " + definitions.size() + " alert definitions");

        int numSynced = 0;
        if (options.has(OPT_BATCH_SIZE)) {
            int batchSize = (Integer)options.valueOf(OPT_BATCH_SIZE);
            int numBatches = (int)Math.ceil(definitions.size()/((double)batchSize));

            for (int i = 0; i < numBatches; i++) {
                System.out.println("Syncing batch " + (i + 1) + " of " + numBatches);
                int fromIndex = i * batchSize;
                int toIndex = (fromIndex + batchSize) > definitions.size() ? 
                              definitions.size() : (fromIndex + batchSize);
                AlertDefinitionsResponse syncResponse =
                        api.syncAlertDefinitions(definitions.subList(fromIndex,
                                                                     toIndex));
                checkSuccess(syncResponse);
                numSynced += (toIndex - fromIndex);
            }
        } else {
            AlertDefinitionsResponse syncResponse = api.syncAlertDefinitions(definitions);
            checkSuccess(syncResponse);
            numSynced = definitions.size();
        }

        System.out.println("Successfully synced " + numSynced + " alert definitions.");
    }
}
