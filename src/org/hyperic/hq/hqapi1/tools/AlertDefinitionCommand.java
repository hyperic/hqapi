package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.Arrays;

public class AlertDefinitionCommand extends Command {

    private static String CMD_LIST = "list";
    private static String CMD_DELETE = "delete";

    private static String[] COMMANDS = { CMD_LIST, CMD_DELETE };

    private static String OPT_TYPEALERTS = "typeAlerts";
    private static String OPT_EXCLUDE_TYPEALERTS = "excludeTypeAlerts";
    private static String OPT_ID   = "id";

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
                                          "be excluded");

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        AlertDefinitionApi definitionApi = api.getAlertDefinitionApi();

        AlertDefinitionsResponse alertDefs;

        if (options.has(OPT_TYPEALERTS)) {
            alertDefs = definitionApi.getTypeAlertDefinitions();
        } else {
            boolean excludeTypeAlerts = false;
            if (options.has(OPT_EXCLUDE_TYPEALERTS)) {
                excludeTypeAlerts = true;
            }

            alertDefs = definitionApi.getAlertDefinitions(excludeTypeAlerts);
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
}
