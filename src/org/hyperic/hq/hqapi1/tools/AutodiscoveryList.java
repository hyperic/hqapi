package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.AutodiscoveryApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.QueueResponse;

public class AutodiscoveryList extends ToolsBase {

    public static void listQueue(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        AutodiscoveryApi autodiscoveryApi = api.getAutodiscoveryApi();

        QueueResponse queue = autodiscoveryApi.getQueue();

        XmlUtil.serialize(queue, System.out, Boolean.TRUE);  
    }

    public static void main(String[] args) throws Exception {
        try {
            listQueue(args);
        } catch (Exception e) {
            System.err.println("Error listing resources: " + e.getMessage());
            System.exit(-1);
        }
    }
}
