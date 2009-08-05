package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.Arrays;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.EventApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.EventsResponse;

public class EventCommand extends Command {

    private static String CMD_LIST = "list";

    private static String[] COMMANDS = { CMD_LIST };

    private static final String OPT_RESOURCE_ID = "resourceId";
    private static final String OPT_HOURS       = "hours";

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
        } else {
            printUsage();
            System.exit(-1);
        }
    }

    private void list(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_RESOURCE_ID, "If specified, only return events for the" +
                                   "given Resource id").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_HOURS, "The number of hours of data to query.  Defaults to 8")
                .withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        EventApi eventApi = api.getEventApi();
        ResourceApi resourceApi = api.getResourceApi();

        long end = System.currentTimeMillis();
        long start;
        if (options.has(OPT_HOURS)) {
            Integer hours = (Integer)options.valueOf(OPT_HOURS);
            start = end - (hours * 60 * 60 * 1000);
        } else {
            start = end - 8 * 60 * 60 * 1000;
        }

        EventsResponse response;
        if (options.has(OPT_RESOURCE_ID)) {
            Integer id = (Integer)options.valueOf(OPT_RESOURCE_ID);

            ResourceResponse resourceResponse =
                    resourceApi.getResource(id, false, false);
            checkSuccess(resourceResponse);

            response = eventApi.findEvents(resourceResponse.getResource(),
                                           start, end);
        } else {
            response = eventApi.findEvents(start, end,
                                           EventApi.EventType.ANY,
                                           EventApi.EventStatus.ANY,
                                           Integer.MAX_VALUE);
        }

        checkSuccess(response);
        XmlUtil.serialize(response, System.out, Boolean.TRUE);
    }
}
