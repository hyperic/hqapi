package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.Arrays;
import java.util.List;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.EscalationsResponse;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class EscalationCommand extends Command {

    private static String CMD_LIST = "list";
    private static String CMD_SYNC = "sync";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC };

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
        } else {
            printUsage();
            System.exit(-1);
        }
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

        EscalationsResponse resp = XmlUtil.deserialize(EscalationsResponse.class,
                                                       System.in);
        List<Escalation> escalations = resp.getEscalation();

        StatusResponse syncResponse = escApi.syncEscalations(escalations);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + escalations.size() + " " +
                           "escalations.");
    }
}
