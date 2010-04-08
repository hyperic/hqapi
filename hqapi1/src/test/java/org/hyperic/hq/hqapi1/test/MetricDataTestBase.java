package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.MetricData;
import org.hyperic.hq.hqapi1.types.DataPoint;
import org.hyperic.hq.hqapi1.types.LastMetricData;

public abstract class MetricDataTestBase extends HQApiTestBase {

    public MetricDataTestBase(String name) {
        super(name);
    }

    protected void validateMetricData(MetricData data) {
        assertTrue("Resource name is empty", data.getResourceName().length() > 0);
        assertTrue("Invalid resource id", data.getResourceId() > 0);
        assertTrue("Metric name is empty", data.getMetricName().length() > 0);
        assertTrue("Invalid metric id", data.getMetricId() > 0);

        long lastTs = 0;
        // Data points should be in ascending order
        for (DataPoint dp : data.getDataPoint()) {
            assertTrue("Timestamp out of order " + lastTs + " > " + dp.getTimestamp(),
                       lastTs < dp.getTimestamp());
            lastTs = dp.getTimestamp();
        } 
    }


    protected void validateLastMetricData(LastMetricData data) {
        assertTrue("Resource name is empty", data.getResourceName().length() > 0);
        assertTrue("Invalid resource id", data.getResourceId() > 0);
        assertTrue("Metric name is empty", data.getMetricName().length() > 0);
        assertTrue("Invalid metric id", data.getMetricId() > 0);

        DataPoint dp = data.getDataPoint();
        assertNotNull(dp);
        assertTrue("Timestamp incorrect", dp.getTimestamp() > 0);
        assertTrue("Metric value incorrect", dp.getValue() >= 0);
    }
}
