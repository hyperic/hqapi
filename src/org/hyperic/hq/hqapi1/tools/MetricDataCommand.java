/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.MetricDataResponse;
import org.hyperic.hq.hqapi1.types.DataPoint;
import org.hyperic.hq.hqapi1.types.LastMetricsDataResponse;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.LastMetricData;

import java.util.Arrays;
import java.util.Date;

public class MetricDataCommand extends Command {

    private static String CMD_LIST = "list";

    private static String[] COMMANDS = { CMD_LIST };

    private static final String OPT_RESOURCE_ID = "resourceId";
    private static final String OPT_METRIC_ID   = "metricId";
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

        p.accepts(OPT_RESOURCE_ID, "The resource id to query for metric data")
                .withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_METRIC_ID, "The metric id to query for data")
                .withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_HOURS, "The number of hours of data to query.  Defaults to 8")
                .withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi resourceApi = api.getResourceApi();
        MetricApi metricApi = api.getMetricApi();
        MetricDataApi dataApi = api.getMetricDataApi();

        long end = System.currentTimeMillis();
        long start;
        if (options.has(OPT_HOURS)) {
            Integer hours = (Integer)options.valueOf(OPT_HOURS);
            start = end - (hours * 60 * 60 * 1000);
        } else {
            start = end - 8 * 60 * 60 * 1000;
        }

        if (options.has(OPT_METRIC_ID)) {

            MetricDataResponse data =
                    dataApi.getData((Integer)getRequired(options, OPT_METRIC_ID),
                                    start, end);
            checkSuccess(data);

            System.out.println("Values for " + data.getMetricData().getMetricName() +
                               " on " + data.getMetricData().getResourceName());
            for (DataPoint dp : data.getMetricData().getDataPoint()) {
                System.out.println("    " + new Date(dp.getTimestamp()) +
                                   " = " + dp.getValue());
            }
        } else if (options.has(OPT_RESOURCE_ID)){
            ResourceResponse resource =
                    resourceApi.getResource((Integer)getRequired(options,
                                                                 OPT_RESOURCE_ID),
                                            false, false);
            checkSuccess(resource);

            MetricsResponse metrics = metricApi.getMetrics(resource.getResource(), true);
            checkSuccess(metrics);

            int[] ids = new int[metrics.getMetric().size()];
            for (int i = 0; i < metrics.getMetric().size(); i++) {
                ids[i] = metrics.getMetric().get(i).getId();
            }

            LastMetricsDataResponse data = dataApi.getData(ids);
            checkSuccess(data);

            System.out.println("Last metric values for " + resource.getResource().getName());
            for (LastMetricData d : data.getLastMetricData()) {
                String value;
                if (d.getDataPoint() != null) {
                    value = d.getDataPoint().getValue() + " (at " +
                            new Date(d.getDataPoint().getTimestamp()) + ")";
                } else {
                    value = "No data";
                }
                System.out.println("    " + d.getMetricName() + " = " + value);
            }
        }
    }
}
