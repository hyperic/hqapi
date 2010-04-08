package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.DataPoint;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.MetricResponse;
import org.hyperic.hq.hqapi1.types.MetricDataResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class MetricDataAddData_test extends MetricDataTestBase {

    public MetricDataAddData_test(String name) {
        super(name);
    }

    public void testAddDataSinglePoint() throws Exception {

        ResourceApi resourceApi = getApi().getResourceApi();
        MetricApi metricApi = getApi().getMetricApi();
        MetricDataApi dataApi = getApi().getMetricDataApi();
        Agent a = getRunningAgent();

        // Find HTTP resource type
        ResourcePrototypeResponse protoResponse =
                resourceApi.getResourcePrototype("HTTP");
        hqAssertSuccess(protoResponse);
        ResourcePrototype pt = protoResponse.getResourcePrototype();

        // Find local platform
        ResourcesResponse resourcesResponse =
                resourceApi.getResources(a, false, false);
        hqAssertSuccess(resourcesResponse);
        assertTrue("Did not find a single platform for " + a.getAddress() + ":" +
                   a.getPort(), resourcesResponse.getResource().size() == 1);
        Resource platform = resourcesResponse.getResource().get(0);

        // Configure service
        Map<String,String> params = new HashMap<String,String>();
        params.put("hostname", "www.hyperic.com");
        params.put("port", "80");
        params.put("sotimeout", "10");
        params.put("path", "/");
        params.put("method", "GET");

        Random r = new Random();
        String name = "My HTTP Check " + r.nextInt();

        ResourceResponse resp = resourceApi.createService(pt, platform,
                                                          name, params);
        hqAssertSuccess(resp);
        Resource createdResource = resp.getResource();
        assertEquals(createdResource.getName(), name);

        pauseTest();

        MetricsResponse metricsResponse =
                metricApi.getMetrics(createdResource, true);
        hqAssertSuccess(metricsResponse);
        assertTrue("No metrics found for " + createdResource.getName(),
                   metricsResponse.getMetric().size() > 0);

        Metric m = null;
        for (Metric metric : metricsResponse.getMetric()) {
            if (!metric.getMetricTemplate().getName().equals("Availability")) {
                m = metric;
                break;
            }
        }

        assertNotNull("Unable to find suitible metric for " +
                      createdResource.getName(), m);

        // Insert slightly into the past to avoid collisions with
        // current data.
        long ts = System.currentTimeMillis() - (60 * 1000);
        List<DataPoint> dps = new ArrayList<DataPoint>();
        DataPoint dp = new DataPoint();
        dp.setTimestamp(ts);
        dp.setValue(10000.0);
        dps.add(dp);

        StatusResponse insertResponse = dataApi.addData(m, dps);
        hqAssertSuccess(insertResponse);

        // BatchInserter only inserts once every 10 seconds.
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            // Ignore
        }

        // Validate we can pull the metrics back out
        MetricDataResponse metricDataResponse = dataApi.getData(m, ts - 60000,
                                                                ts + 60000);
        hqAssertSuccess(metricDataResponse);
        assertTrue("Invalid number of data points found, expected 1 found " +
                   metricDataResponse.getMetricData().getDataPoint().size(),
                   metricDataResponse.getMetricData().getDataPoint().size() == 1);

        DataPoint insertedPoint = metricDataResponse.getMetricData().getDataPoint().get(0);

        assertEquals("Timestamps don't match", insertedPoint.getTimestamp(),
                     dp.getTimestamp());
        assertEquals("Values don't match", dp.getValue(), dp.getValue());

        // Clean up
        StatusResponse deleteResponse = resourceApi.deleteResource(createdResource.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testAddDataInvalidMetric() throws Exception {

        MetricDataApi dataApi = getApi().getMetricDataApi();

        List<DataPoint> dps = new ArrayList<DataPoint>();
        Metric m = new Metric();
        m.setId(Integer.MAX_VALUE);

        StatusResponse response = dataApi.addData(m, dps);
        hqAssertFailureObjectNotFound(response);
    }
}
