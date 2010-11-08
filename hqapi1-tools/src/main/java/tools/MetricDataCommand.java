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
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.MetricDataResponse;
import org.hyperic.hq.hqapi1.types.DataPoint;
import org.hyperic.hq.hqapi1.types.MetricDataSummary;
import org.hyperic.hq.hqapi1.types.MetricsDataSummaryResponse;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.MetricResponse;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class MetricDataCommand extends AbstractCommand {

    private static String CMD_LIST = "list";

    private static String[] COMMANDS = { CMD_LIST };

    private static final String OPT_RESOURCE_ID  = "resourceId";
    private static final String OPT_METRIC_ID    = "metricId";
    private static final String OPT_GROUP_ID     = "groupId";
    private static final String OPT_FORMAT_DATES = "formatDates";
    private static final String OPT_HOURS        = "hours";

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }
    
    public String getName() {
        return "metricData";
     }

    public int handleCommand(String[] args) throws Exception {
        
        if (args.length == 0) {
            printUsage();
            return 1;
        }

        if (args[0].equals(CMD_LIST)) {
            list(trim(args));
        } else {
            printUsage();
            return 1;
        }
        return 0;
    }

    private void list(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_RESOURCE_ID, "The resource id to query for metric data")
                .withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_METRIC_ID, "The metric id to query for metric data")
                .withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_GROUP_ID, "The group id to query for metric data")
                .withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_HOURS, "The number of hours of data to query.  Defaults to 8")
                .withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_FORMAT_DATES, "When specified timestamps will be formatted " +
                                    "using the given format.  Defaults to " + FORMAT)
                .withOptionalArg().ofType(String.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ResourceApi resourceApi = api.getResourceApi();
        GroupApi groupApi = api.getGroupApi();
        MetricApi metricApi = api.getMetricApi();
        MetricDataApi dataApi = api.getMetricDataApi();

        final long MS_IN_HOUR = 60l * 60l * 1000l;
        long end = System.currentTimeMillis();
        end = end - (end%60000); // Make sure end is on a 1 minute boundary.
        long start;
        if (options.has(OPT_HOURS)) {
            int hours = (Integer)options.valueOf(OPT_HOURS);
            start = end - (hours * MS_IN_HOUR);
        } else {
            start = end - (8 * MS_IN_HOUR);
        }

        String format = null;
        if (options.has(OPT_FORMAT_DATES)) {
            format = (String)options.valueOf(OPT_FORMAT_DATES);
            if (format == null) {
                format = FORMAT;
            }
        }

        if (options.has(OPT_METRIC_ID)) {
            MetricResponse metric =
                    metricApi.getMetric((Integer)getRequired(options,
                                                             OPT_METRIC_ID));
            checkSuccess(metric);

            MetricDataResponse data =
                    dataApi.getData(metric.getMetric(), start, end);
            checkSuccess(data);

            CsvTable table = new CsvTable(new String[] {"Value"}, format);
            for (DataPoint dp : data.getMetricData().getDataPoint()) {
                table.add(dp.getTimestamp(), 0, dp.getValue());
            }
            table.output();
        } else if (options.has(OPT_RESOURCE_ID)){
            ResourceResponse resource =
                    resourceApi.getResource((Integer)getRequired(options,
                                                                 OPT_RESOURCE_ID),
                                            false, false);
            checkSuccess(resource);

            MetricsResponse metrics = metricApi.getMetrics(resource.getResource(), true);
            checkSuccess(metrics);

            String[] metricNames = new String[metrics.getMetric().size()];
            for (int i = 0; i < metrics.getMetric().size(); i++) {
                metricNames[i] = metrics.getMetric().get(i).getName() + "(id=" +
                        metrics.getMetric().get(i).getId() + ")";
            }

            CsvTable table = new CsvTable(metricNames, format);
            for (int i = 0; i < metrics.getMetric().size(); i++) {
                MetricDataResponse data =
                        dataApi.getData(metrics.getMetric().get(i), start, end);
                checkSuccess(data);

                for (DataPoint dp : data.getMetricData().getDataPoint()) {
                    table.add(dp.getTimestamp(), i, dp.getValue());
                }
            }
            table.output();
        } else if (options.has(OPT_GROUP_ID)) {
            GroupResponse group =
                    groupApi.getGroup((Integer)getRequired(options,
                                                           OPT_GROUP_ID));
            checkSuccess(group);

            if (group.getGroup().getResourcePrototype() == null) {
                System.err.println("Group " + group.getGroup().getName() +
                        " is not a compatible group.");
            }

            MetricsDataSummaryResponse data =
                    dataApi.getSummary(group.getGroup(), start, end);
            checkSuccess(data);

            System.out.println("Metric Name, Template Id, Min, Max, Avg, Last");
            for (MetricDataSummary s : data.getMetricDataSummary()) {
                System.out.println(s.getMetricName() + "," +
                                   s.getMetricTemplateId() + "," +
                                   s.getMinMetric() + "," +
                                   s.getMaxMetric() + "," +
                                   s.getAvgMetric() + "," +
                                   s.getLastMetric());
            }
        }
    }

    private class CsvTable {

        private Map<Long,Row> _rows = new TreeMap<Long,Row>();
        private List<String> _headers = new ArrayList<String>();
        private TimeFormat _tsFormat;

        CsvTable(String[] headers, String dateFormat) {
            _tsFormat = new TimeFormat(dateFormat);
            _headers.addAll(Arrays.asList(headers));
        }

        private class TimeFormat {

            SimpleDateFormat _df;

            TimeFormat(String format) {
                if (format != null) {
                    _df = new SimpleDateFormat(format);
                }
            }

            public String format(Long ts) {
                if (_df != null) {
                    return _df.format(new Date(ts));
                } else {
                    return String.valueOf(ts);
                }
            }
        }

        private class Row {
            private Object[] _values;

            Row(int columns) {
                _values = new Object[columns];
            }

            public void add(int idx, Object val) {
                _values[idx] = val;
            }

            public Object[] getValues() {
                return _values;
            }
        }

        public void add(long ts, int idx, Object value) {
            Row r = _rows.get(ts);
            if (r == null) {
                r = new Row(_headers.size());
                r.add(idx, value);
                _rows.put(ts, r);
            } else {
                r.add(idx, value);
            }
        }

        public void output() {
            for (String header : _headers) {
                System.out.print("," + header);
            }
            System.out.println();

            for (Long ts : _rows.keySet()) {
                Row r = _rows.get(ts);
                System.out.print(_tsFormat.format(ts));
                for (Object val : r.getValues()) {
                    if (val == null) {
                        System.out.print(",");
                    } else {
                        System.out.print("," + val);
                    }
                }
                System.out.println();
            }
        }
    }
}
