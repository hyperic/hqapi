package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

import java.util.ArrayList;
import java.util.List;

public class WADLMetricData_test extends WADLTestBase {

    public void testGet() throws Exception {
        Endpoint.MetricDataGetHqu get = new Endpoint.MetricDataGetHqu();

        MetricDataResponse response =
                get.getAsMetricDataResponse(Integer.MAX_VALUE,
                                            0l, System.currentTimeMillis());
        hqAssertFailure(response);
    }

    public void testGetLast() throws Exception {
        Endpoint.MetricDataGetLastHqu last = new Endpoint.MetricDataGetLastHqu();

        LastMetricDataResponse response =
                last.getAsLastMetricDataResponse(Integer.MAX_VALUE);
        hqAssertFailure(response);
    }

    public void testGetMulti() throws Exception {
        Endpoint.MetricDataGetMultiHqu multi = new Endpoint.MetricDataGetMultiHqu();

        List<Integer> ids = new ArrayList<Integer>();
        ids.add(0);
        ids.add(2);
        MetricsDataResponse response =
                multi.getAsMetricsDataResponse(ids, 0l,
                                               System.currentTimeMillis());
        hqAssertFailure(response);

    }

    public void testGetMultiLast() throws Exception {
        Endpoint.MetricDataGetMultiLastHqu multiLast =
                new Endpoint.MetricDataGetMultiLastHqu();

        List<Integer> ids = new ArrayList<Integer>();
        ids.add(0);
        ids.add(2);

        LastMetricsDataResponse response =
                multiLast.getAsLastMetricsDataResponse(ids);
        hqAssertFailure(response);
    }

    public void testPut() throws Exception {
        Endpoint.MetricDataPutHqu put = new Endpoint.MetricDataPutHqu();

        List<DataPoint> dps = new ArrayList<DataPoint>();
        DataPoint dp = new DataPoint();
        dp.setTimestamp(System.currentTimeMillis());
        dp.setValue(0);
        dps.add(dp);

        DataPointsRequest request = new DataPointsRequest();
        request.setMetricId(Integer.MAX_VALUE);
        request.getDataPoint().addAll(dps);

        StatusResponse response = put.postAsStatusResponse(request);
        hqAssertFailure(response);
    }
}

