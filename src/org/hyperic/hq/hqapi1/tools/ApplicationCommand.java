package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.*;

import java.util.Arrays;

public class ApplicationCommand extends Command {

    private static String CMD_LIST   = "list";
    private static String CMD_DELETE = "delete";

    private static String[] COMMANDS = { CMD_LIST, CMD_DELETE };

    private static String OPT_ID     = "id";

    // Additional sync commands when syncing via command line options.
//    private static String OPT_NAME          = "name";
//    private static String OPT_PROTOTYPE     = "prototype";
//    private static String OPT_REGEX         = "regex";
//    private static String OPT_DELETEMISSING = "deleteMissing";
//    private static String OPT_DESC          = "description";

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

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ApplicationApi groupApi = api.getApplicationApi();

        ApplicationsResponse applications;

        applications = groupApi.listApplications();

        XmlUtil.serialize(applications, System.out, Boolean.TRUE);
    }

    private void delete(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "The resource id to delete").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        if (!options.has(OPT_ID)) {
            System.err.println("Required argument " + OPT_ID + " not given");
            System.exit(-1);
        }

        HQApi api = getApi(options);
        ApplicationApi groupApi = api.getApplicationApi();

        Integer id = (Integer)options.valueOf(OPT_ID);

        StatusResponse response = groupApi.deleteApplication(id);
        checkSuccess(response);

        System.out.println("Successfully deleted application id " + id);
    }
}