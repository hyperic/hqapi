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

import java.util.Arrays;
import java.util.List;

public class MetricTemplateCommand extends Command {

    private static String CMD_LIST = "list";
    private static String CMD_SYNC = "sync";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC };

    private static String OPT_PROTOTYPE  = "prototype";

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

        MetricTemplatesResponse resp =
                XmlUtil.deserialize(MetricTemplatesResponse.class, System.in);

        List<MetricTemplate> metricTemplates = resp.getMetricTemplate();

        StatusResponse syncResponse =
                metricApi.syncMetricTemplates(metricTemplates);

        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + metricTemplates.size() +
                           " templates.");
    }
}
