package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MetricDataApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.MetricDataResponse;
import org.hyperic.hq.hqapi1.types.MetricDataSummary;
import org.hyperic.hq.hqapi1.types.MetricsDataSummaryResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;

public class MetricDataGetSummary_test extends MetricDataTestBase {

    public MetricDataGetSummary_test(String name) {
        super(name);
    }

    public void testResourceSummary() throws Exception {        
        ResourceApi resourceApi = getApi().getResourceApi();
        MetricDataApi metricDataApi = getApi().getMetricDataApi();
        
        // Find CPU resources
        ResourcePrototypeResponse cpuPrototypeResponse =
                resourceApi.getResourcePrototype("CPU");
        hqAssertSuccess(cpuPrototypeResponse);

        ResourcePrototype cpuPrototype = cpuPrototypeResponse.getResourcePrototype();
        ResourcesResponse resourceResponse =
                resourceApi.getResources(cpuPrototype,
                                         false, false);
        hqAssertSuccess(resourceResponse);
        assertFalse(resourceResponse.getResource().isEmpty());

        Resource cpu = resourceResponse.getResource().get(0);
        
        // Keep the metric data range small to validate HHQ-3803
        long end = System.currentTimeMillis();
        long start = end - (30 * 60 * 1000);
        
        MetricsDataSummaryResponse summaryResponse = 
            metricDataApi.getSummary(cpu, start, end);
        hqAssertSuccess(summaryResponse);
        
        for (MetricDataSummary s : summaryResponse.getMetricDataSummary()) {
            validateMetricDataSummary(s);
        }        
    }

    public void testGroupSummary() throws Exception {        
        ResourceApi resourceApi = getApi().getResourceApi();
        MetricDataApi metricDataApi = getApi().getMetricDataApi();
        
        // Find CPU resources
        ResourcePrototypeResponse cpuPrototypeResponse =
                resourceApi.getResourcePrototype("CPU");
        hqAssertSuccess(cpuPrototypeResponse);

        ResourcePrototype cpuPrototype = cpuPrototypeResponse.getResourcePrototype();
        ResourcesResponse resourceResponse =
                resourceApi.getResources(cpuPrototype,
                                         false, false);
        hqAssertSuccess(resourceResponse);
        assertFalse(resourceResponse.getResource().isEmpty());
        
        // Create CPU group
        Group group = createGroup(resourceResponse.getResource());
        
        long end = System.currentTimeMillis();
        long start = end - (30 * 60 * 1000);
        
        MetricsDataSummaryResponse summaryResponse = 
            metricDataApi.getSummary(group, start, end);
        hqAssertSuccess(summaryResponse);
        
        for (MetricDataSummary s : summaryResponse.getMetricDataSummary()) {
            validateMetricDataSummary(s);
        }
        
        cleanupGroup(group);
    }
    
    //TODO public void testGetInvalidResourceId() throws Exception {}

    //TODO public void testGetInvalidRange() throws Exception {}
    
    private void validateMetricDataSummary(MetricDataSummary s) {
        
        assertTrue(s.getMetricName() + ": Average value (" + s.getAvgMetric() 
                        + ") must be greater than or equal to the minimum value ("
                        + s.getMinMetric() + ")",
                   s.getAvgMetric().compareTo(s.getMinMetric()) >=0);

        assertTrue(s.getMetricName() + ": Average value (" + s.getAvgMetric() 
                        + ") must be less than or equal to the maximum value ("
                        + s.getMaxMetric() + ")",
                        s.getAvgMetric().compareTo(s.getMaxMetric()) <=0);
        
        if ("percentage".equals(s.getUnits())) {
            assertTrue("The minimum value (" + s.getMinMetric()
                            + ") for percentage metrics must be less than or equal to 1",
                       s.getMinMetric() <= 1);

            assertTrue("The average value (" + s.getAvgMetric()
                            + ") for percentage metrics must be less than or equal to 1",
                       s.getAvgMetric() <= 1);

            assertTrue("The maximum value (" + s.getMaxMetric()
                            + ") for percentage metrics must be less than or equal to 1",
                       s.getMaxMetric() <= 1);

            assertTrue("The last or sum value (" + s.getLastMetric()
                            + ") for percentage metrics must be less than or equal to 1",
                       s.getLastMetric() <= 1);
        }
        
    }
}
