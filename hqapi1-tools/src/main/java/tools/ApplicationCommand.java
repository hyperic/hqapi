package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.ApplicationApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.io.InputStream;
@Component
public class ApplicationCommand extends AbstractCommand {

    private static String CMD_LIST   = "list";
    private static String CMD_SYNC   = "sync";
    private static String CMD_DELETE = "delete";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC, CMD_DELETE };

    private static String OPT_ID     = "id";
    private static String OPT_BATCH_SIZE = "batchSize";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }

    public String getName() {
        return "application";
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
        } else if (args[0].equals(CMD_DELETE)) {
            delete(trim(args));
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
        ApplicationApi groupApi = api.getApplicationApi();

        ApplicationsResponse applications;

        applications = groupApi.listApplications();

        XmlUtil.serialize(applications, System.out, Boolean.TRUE);
    }

    private void sync(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_BATCH_SIZE, "Process the sync in batches of the given size").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        ApplicationApi api = getApi(options).getApplicationApi();

        InputStream is = getInputStream(options);
        ApplicationsResponse resp = XmlUtil.deserialize(ApplicationsResponse.class, is);
        List<Application> applications = resp.getApplication();

        int numSynced = 0;
        if (options.has(OPT_BATCH_SIZE)) {
            int batchSize = (Integer)options.valueOf(OPT_BATCH_SIZE);
            int numBatches = (int)Math.ceil(applications.size()/((double)batchSize));

            for (int i = 0; i < numBatches; i++) {
                System.out.println("Syncing batch " + (i + 1) + " of " + numBatches);
                int fromIndex = i * batchSize;
                int toIndex = (fromIndex + batchSize) > applications.size() ?
                              applications.size() : (fromIndex + batchSize);
                ApplicationsResponse syncResponse =
                        api.syncApplications(applications.subList(fromIndex,
                                                                  toIndex));
                checkSuccess(syncResponse);
                numSynced += (toIndex - fromIndex);
            }
        } else {
            ApplicationsResponse syncResponse = api.syncApplications(applications);
            checkSuccess(syncResponse);
            numSynced = applications.size();
        }

        System.out.println("Successfully synced " + numSynced + " applications.");
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