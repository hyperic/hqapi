package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.MetricTemplatesResponse;
import org.hyperic.hq.hqapi1.types.MetricTemplate;

import java.util.List;

public class MetricTemplateSync extends ToolsBase {

    private static void syncTemplates(String[] args) throws Exception {

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

    public static void main(String[] args) throws Exception {
        try {
            syncTemplates(args);
        } catch (Exception e) {
            System.err.println("Error syncing templates: " + e.getMessage());
            System.exit(-1);
        }
    }
}
