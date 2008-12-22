package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.types.EscalationsResponse;

public class EscalationList extends ToolsBase {

    private static void listEscalations(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        EscalationApi escApi = api.getEscalationApi();

        EscalationsResponse escalations = escApi.getEscalations();
        checkSuccess(escalations);

        XmlUtil.serialize(escalations, System.out, Boolean.TRUE);
    }

    public static void main(String[] args) throws Exception {
        try {
            listEscalations(args);
        } catch (Exception e) {
            System.err.println("Error listing escalations: " + e.getMessage());
            System.exit(-1);
        }
    }
}
