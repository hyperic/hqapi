package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.MetricTemplatesResponse;

public class MetricTemplateList extends ToolsBase {

    private static String OPT_PROTOTYPE  = "prototype";

    private static void listMetricTemplates(String[] args) throws Exception {

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

    public static void main(String[] args) throws Exception {
        try {
            listMetricTemplates(args);
        } catch (Exception e) {
            System.err.println("Error listing resources: " + e.getMessage());
            System.exit(-1);
        }
    }
}
