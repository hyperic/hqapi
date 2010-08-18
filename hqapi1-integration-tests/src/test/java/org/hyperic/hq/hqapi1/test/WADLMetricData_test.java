package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

import java.util.ArrayList;
import java.util.List;

public class WADLMetricData_test extends WADLTestBase {

    public void testGet() throws Exception {
        HttpLocalhost8080HquHqapi1.MetricDataGetHqu get = new HttpLocalhost8080HquHqapi1.MetricDataGetHqu();

        MetricDataResponse response =
                get.getAsMetricDataResponse(Integer.MAX_VALUE,
                                            0l, System.currentTimeMillis());
        hqAssertFailure(response);
    }

    public void testGetLast() throws Exception {
        HttpLocalhost8080HquHqapi1.MetricDataGetLastHqu last = new HttpLocalhost8080HquHqapi1.MetricDataGetLastHqu();

        LastMetricDataResponse response =
                last.getAsLastMetricDataResponse(Integer.MAX_VALUE);
        hqAssertFailure(response);
    }

    public void testGetMulti() throws Exception {
        HttpLocalhost8080HquHqapi1.MetricDataGetMultiHqu multi = new HttpLocalhost8080HquHqapi1.MetricDataGetMultiHqu();

        List<Integer> ids = new ArrayList<Integer>();
        ids.add(0);
        ids.add(2);
        MetricsDataResponse response =
                multi.getAsMetricsDataResponse(ids, 0l,
                                               System.currentTimeMillis());
        hqAssertFailure(response);

    }

    public void testGetMultiLast() throws Exception {
        HttpLocalhost8080HquHqapi1.MetricDataGetMultiLastHqu multiLast =
                new HttpLocalhost8080HquHqapi1.MetricDataGetMultiLastHqu();

        List<Integer> ids = new ArrayList<Integer>();
        ids.add(0);
        ids.add(2);

        LastMetricsDataResponse response =
                multiLast.getAsLastMetricsDataResponse(ids);
        hqAssertFailure(response);
    }

    public void testPut() throws Exception {
        HttpLocalhost8080HquHqapi1.MetricDataPutHqu put = new HttpLocalhost8080HquHqapi1.MetricDataPutHqu();

        List<DataPoint> dps = new ArrayList<DataPoint>();
        DataPoint dp = new DataPoint();
        dp.setTimestamp(System.currentTimeMillis());
        dp.setValue(0);
        dps.add(dp);

        DataPointsRequest request = new DataPointsRequest();
        request.setMetricId(Integer.MAX_VALUE);
        request.getDataPoint().addAll(dps);

        StatusResponse response = put.postApplicationXmlAsStatusResponse(request);
        hqAssertFailure(response);
    }
}

