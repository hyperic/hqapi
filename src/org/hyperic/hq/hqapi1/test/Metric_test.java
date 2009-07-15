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

package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.MetricResponse;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.ArrayList;
import java.util.List;

public class Metric_test extends MetricTestBase {

    public Metric_test(String name) {
        super(name);
    }

    protected void validateMetric(Metric m) throws Exception {
        assertNotNull(m);
        assertTrue(m.getId() > 0);
        assertNotNull(m.getInterval());
        assertNotNull(m.isEnabled());
        assertTrue(m.getName().length() > 0);
        assertNotNull(m.isDefaultOn());
        assertNotNull(m.isIndicator());
        assertTrue(m.getCollectionType().length() > 0);
    }
        
    public void testListMetrics() throws Exception {

        MetricApi api = getApi().getMetricApi();
        Resource r = getLocalPlatformResource(false, false);
        MetricsResponse resp = api.getMetrics(r, false);
        hqAssertSuccess(resp);

        int numNotDefaultOn = 0;
        for (Metric m : resp.getMetric()) {
            validateMetric(m);
            if (!m.isDefaultOn()) {
                numNotDefaultOn++;
            }
        }
        assertTrue("All metrics are defaultOn", numNotDefaultOn > 0);
    }

    public void testListEnabledMetrics() throws Exception {

        MetricApi api = getApi().getMetricApi();
        Resource r = getLocalPlatformResource(false, false);
        MetricsResponse resp = api.getMetrics(r, true);
        hqAssertSuccess(resp);

        for (Metric m : resp.getMetric()) {
            validateMetric(m);
            assertTrue("Metric " + m.getName() + " is not enabled",
                       m.isEnabled());
        }
    }

    public void testMetricListBadResourceId() throws Exception {
        
        MetricApi api = getApi().getMetricApi();
        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);
        MetricsResponse resp = api.getMetrics(r, true);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testMetricById() throws Exception {

        MetricApi api = getApi().getMetricApi();
        Resource r = getLocalPlatformResource(false, false);
        MetricsResponse resp = api.getMetrics(r, true);
        hqAssertSuccess(resp);

        assertFalse("Resource " + r.getName() + " has no metrics",
                   resp.getMetric().size() == 0);

        Metric m = resp.getMetric().get(0);
        MetricResponse metricResponse = api.getMetric(m.getId());
        hqAssertSuccess(metricResponse);
        validateMetric(metricResponse.getMetric());  
    }

    public void testMetricByBadId() throws Exception {

        MetricApi api = getApi().getMetricApi();
        MetricResponse metricResponse = api.getMetric(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(metricResponse);
    }

    public void testSyncUpdateInterval() throws Exception {

        HQApi api = getApi();
        MetricApi metricApi = api.getMetricApi();
        Resource r = getLocalPlatformResource(false, false);

        MetricsResponse metrics = metricApi.getMetrics(r, true);
        hqAssertSuccess(metrics);

        Metric enabledMetric = null;

        for (Metric m : metrics.getMetric()) {
            if (m.isEnabled() && !m.getName().equals("Availability")) {
                enabledMetric = m;
                break;
            }
        }

        assertNotNull("Unable to find enabled metric for " + r.getName(),
                      enabledMetric);

        final long interval = enabledMetric.getInterval();
        final long newInterval = interval * 2;
        enabledMetric.setInterval(newInterval);

        List<Metric> syncMetrics = new ArrayList<Metric>();
        syncMetrics.add(enabledMetric);
        StatusResponse syncResponse = metricApi.syncMetrics(syncMetrics);
        hqAssertSuccess(syncResponse);

        MetricResponse metric = metricApi.getMetric(enabledMetric.getId());
        hqAssertSuccess(metric);
        assertTrue("Interval for metric " + enabledMetric.getName() + " not " +
                   "updated.", metric.getMetric().getInterval() == newInterval);

        // Reset
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            // Cannot reschedule so quickly without getting ObjectNotFoundException
            // for ScheduleRevNum
        }

        enabledMetric.setInterval(interval);
        syncResponse = metricApi.syncMetrics(syncMetrics);
        hqAssertSuccess(syncResponse);
    }

    public void testSyncEnable() throws Exception {

        HQApi api = getApi();
        MetricApi metricApi = api.getMetricApi();
        Resource r = getLocalPlatformResource(false, false);

        // Keep a copy of the old metrics.
        MetricsResponse orignalMetrics = metricApi.getMetrics(r, false);

        MetricsResponse metrics = metricApi.getMetrics(r, false);
        hqAssertSuccess(metrics);

        Metric disabledMetric = null;

        for (Metric m : metrics.getMetric()) {
            if (!m.isEnabled()) {
                disabledMetric = m;
                break;
            }
        }

        assertNotNull("Unable to find disabled metric for " + r.getName(),
                      disabledMetric);

        disabledMetric.setEnabled(true);

        List<Metric> syncMetrics = new ArrayList<Metric>();
        syncMetrics.add(disabledMetric);
        StatusResponse syncResponse = metricApi.syncMetrics(syncMetrics);
        hqAssertSuccess(syncResponse);

        MetricResponse metric = metricApi.getMetric(disabledMetric.getId());
        hqAssertSuccess(metric);
        assertTrue("Metric " + disabledMetric.getName() + " not enabled.",
                   metric.getMetric().isEnabled());

        // Reset
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            // Cannot re-enable so quickly without getting ObjectNotFoundException
            // for ScheduleRevNum
        }

        syncResponse = metricApi.syncMetrics(orignalMetrics.getMetric());
        hqAssertSuccess(syncResponse);
    }

    public void testSyncInvalidMetric() throws Exception {
        HQApi api = getApi();
        MetricApi metricApi = api.getMetricApi();

        Metric m = new Metric();
        m.setId(Integer.MAX_VALUE);

        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(m);

        // Reset metrics back to the original state
        StatusResponse syncResponse = metricApi.syncMetrics(metrics);
        hqAssertFailureObjectNotFound(syncResponse);
    }

    public void testSyncInvalidInterval() throws Exception {

        HQApi api = getApi();
        MetricApi metricApi = api.getMetricApi();
        Resource r = getLocalPlatformResource(false, false);

        MetricsResponse metrics = metricApi.getMetrics(r, true);
        hqAssertSuccess(metrics);

        assertTrue("No metrics found for " + r.getName(),
                   metrics.getMetric().size() > 0);

        Metric enabledMetric = null;

        for (Metric m : metrics.getMetric()) {
            if (m.isEnabled()) {
                enabledMetric = m;
                break;
            }
        }

        assertNotNull("Unable to find default on metric for " + r.getName(),
                      enabledMetric);

        final long  BAD_INTERVALS[] = { -1, 0, 1, 1000, 59999 };
        for (long BAD_INTERVAL : BAD_INTERVALS) {
            enabledMetric.setInterval(BAD_INTERVAL);

            List<Metric> syncMetrics = new ArrayList<Metric>();
            syncMetrics.add(enabledMetric);

            StatusResponse syncResponse = metricApi.syncMetrics(syncMetrics);
            hqAssertFailureInvalidParameters(syncResponse);            
        }
    }
}
