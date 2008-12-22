package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.EscalationsResponse;
import org.hyperic.hq.hqapi1.types.Escalation;

import java.util.List;

public class EscalationSync extends ToolsBase {

    private static void syncEscalations(String[] args) throws Exception {

        OptionParser p = getOptionParser();
        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        EscalationApi escApi = api.getEscalationApi();

        EscalationsResponse resp = XmlUtil.deserialize(EscalationsResponse.class,
                                                       System.in);
        List<Escalation> escalations = resp.getEscalation();

        StatusResponse syncResponse = escApi.syncEscalations(escalations);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + escalations.size() + " escalations.");
    }

    public static void main(String[] args) throws Exception {
        try {
            syncEscalations(args);
        } catch (Exception e) {
            System.err.println("Error syncing escalations: " + e.getMessage());
            System.exit(-1);
        }
    }
}
