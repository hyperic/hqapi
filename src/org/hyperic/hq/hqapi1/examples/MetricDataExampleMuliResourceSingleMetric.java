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

package org.hyperic.hq.hqapi1.examples;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.MetricData;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.MetricTemplate;
import org.hyperic.hq.hqapi1.types.Response;
import org.hyperic.hq.hqapi1.types.MetricTemplatesResponse;
import org.hyperic.hq.hqapi1.types.MetricsDataResponse;
import org.hyperic.hq.hqapi1.types.DataPoint;

import java.util.Date;
import java.util.List;

/**
 * This example gathers a series of {@link org.hyperic.hq.hqapi1.types.DataPoint}s
 * from a set of compatible {@link org.hyperic.hq.hqapi1.types.Resource}s.
 */
public class MetricDataExampleMuliResourceSingleMetric {

    private static final String TYPE = "FileServer Mount";
    private static final String ALIAS = "UsePercent";
    private static final long RANGE = 8 * 60 * 60 * 1000; // 8 hours.

    private static void checkSuccess(Response r) {
        if (!r.getStatus().equals(ResponseStatus.SUCCESS)) {
            System.err.println("API error: " + r.getError().getReasonText());
            System.exit(-1);
        }
    }

    public MetricDataExampleMuliResourceSingleMetric() {
    }

    public static void main(String[] args) throws Exception {

        HQApi api = new HQApi("localhost", 7443, true, "hqadmin", "hqadmin");
        ResourceApi resourceApi = api.getResourceApi();
        MetricApi metricApi = api.getMetricApi();

        // Find resource type
        ResourcePrototypeResponse prototypeResponse =
                resourceApi.getResourcePrototype(TYPE);
        checkSuccess(prototypeResponse);

        // Find the template we want to lookup
        MetricTemplate t = null;
        MetricTemplatesResponse templatesResponse =
                metricApi.getMetricTemplates(prototypeResponse.getResourcePrototype());
        checkSuccess(templatesResponse);

        for (MetricTemplate tmpl : templatesResponse.getMetricTemplate()) {
            if (tmpl.getAlias().equals(ALIAS)) {
                t = tmpl;
            }
        }

        if (t == null) {
            System.err.println("Unable to find alias " + ALIAS + " for type " +
                               TYPE);
            return;
        }

        // Find resources of that type
        ResourcesResponse resourcesResponse =
                resourceApi.getResources(prototypeResponse.getResourcePrototype(),
                                         false, false);
        checkSuccess(resourcesResponse);

        List<Resource> resources = resourcesResponse.getResource();
        System.out.println("Found " + resources.size() + " matching resources.");

        int resourceIds[] = new int[resources.size()];
        for (int i = 0; i < resources.size(); i++) {
            resourceIds[i] = resources.get(i).getId();
        }

        long end = System.currentTimeMillis();
        MetricsDataResponse dataResponse = metricApi.getMetricData(resourceIds, t.getId(),
                                                                   end - RANGE, end);
        checkSuccess(dataResponse);

        for (MetricData d : dataResponse.getMetricData()) {
            System.out.println("Data for resource " + d.getResourceName() +
                               " metric " + d.getMetricName());
            for (DataPoint p : d.getDataPoint()) {
                System.out.println("  time=" + new Date(p.getTimestamp()).toString() + " data=" + p.getValue());
            }
        }
    }
}
