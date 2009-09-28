package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.*;

import java.util.Arrays;
import java.util.List;
import java.io.InputStream;

public class ApplicationCommand extends Command {

    private static String CMD_LIST   = "list";
    private static String CMD_SYNC   = "sync";
    private static String CMD_DELETE = "delete";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC, CMD_DELETE };

    private static String OPT_ID     = "id";

    // Additional sync commands when syncing via command line options.
    private static String OPT_NAME          = "name";
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
        } else if (args[0].equals(CMD_SYNC)) {
            sync(trim(args));
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

    private void sync(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_NAME, "The application name to sync").
                withRequiredArg().ofType(String.class);
//        p.accepts(OPT_PROTOTYPE, "The resource type to query for group membership").
//                withRequiredArg().ofType(String.class);
//        p.accepts(OPT_REGEX, "The regular expression to apply to the " + OPT_PROTOTYPE +
//                  " flag").withRequiredArg().ofType(String.class);
//        p.accepts(OPT_DELETEMISSING, "Remove resources in the group not included in " +
//                  "the " + OPT_PROTOTYPE + " and " + OPT_REGEX);
//        p.accepts(OPT_COMPAT, "If specified, attempt to make the group compatible");
//        p.accepts(OPT_DESC, "If specified, set the description for the group").
//                withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);

        if (options.hasArgument(OPT_NAME)) {
            syncViaCommandLineArgs(options);
            return;
        }

        HQApi api = getApi(options);
        System.out.println("api: " + api);

        ApplicationApi applicationApi = api.getApplicationApi();
        System.out.println("applicationApi: " + applicationApi);

        InputStream is = getInputStream(options);
        System.out.println("is: " + is);

        ApplicationsResponse resp = XmlUtil.deserialize(ApplicationsResponse.class, is);
        System.out.println("resp: " + resp);
        List<Application> applications = resp.getApplication();
        System.out.println("->" + applications);

        ApplicationsResponse syncResponse = applicationApi.syncApplications(applications);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + applications.size() + " applications.");
    }

    private void syncViaCommandLineArgs(OptionSet s) throws Exception
    {
        System.out.println("Feature not implemented.");
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