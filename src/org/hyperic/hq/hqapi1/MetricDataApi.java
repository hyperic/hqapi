package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.DataPoint;
import org.hyperic.hq.hqapi1.types.MetricDataResponse;
import org.hyperic.hq.hqapi1.types.DataPointsRequest;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.LastMetricsDataResponse;
import org.hyperic.hq.hqapi1.types.LastMetricDataResponse;
import org.hyperic.hq.hqapi1.types.MetricsDataResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MetricDataApi extends BaseApi {

    MetricDataApi(HQConnection conn) {
        super(conn);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MetricData} for the
     * given {@link org.hyperic.hq.hqapi1.types.Metric} id.
     *
     * @param metricId The id of the {@link org.hyperic.hq.hqapi1.types.Metric} to query.
     * @param start The start time to query, in epoch-millis.
     * @param end The end time to query, in epoch-millis.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the data was succesfully queried.  The returned data can be retrieved
     * via {@link org.hyperic.hq.hqapi1.types.MetricDataResponse#getMetricData()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */    
    public MetricDataResponse getData(int metricId, long start, long end)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(metricId) });
        params.put("start", new String[] { Long.toString(start)});
        params.put("end", new String[] { Long.toString(end)});
        return doGet("metricData/get.hqu", params, MetricDataResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.LastMetricData} for the
     * given {@link org.hyperic.hq.hqapi1.types.Metric} id. This object
     * represents the last metric collection for the given metric id.
     *
     * @param metricId The id of the {@link org.hyperic.hq.hqapi1.types.Metric} to query.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the data was succesfully queried.  The returned data can be retrieved
     * via {@link org.hyperic.hq.hqapi1.types.LastMetricDataResponse#getLastMetricData()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public LastMetricDataResponse getData(int metricId)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(metricId) });
        return doGet("metricData/getLast.hqu", params, LastMetricDataResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MetricData} for the
     * given list of {@link org.hyperic.hq.hqapi1.types.Metric} ids.
     *
     * @param metricIds The ids of the {@link org.hyperic.hq.hqapi1.types.Metric}s to query.
     * @param start The start time to query, in epoch-millis.
     * @param end The end time to query, in epoch-millis.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the data was succesfully queried.  The returned data can be retrieved
     * via {@link org.hyperic.hq.hqapi1.types.MetricsDataResponse#getMetricData()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MetricsDataResponse getData(int[] metricIds, long start, long end)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        String[] ids = new String[metricIds.length];
        for (int i = 0; i < metricIds.length; i++) {
            ids[i] = Integer.toString(metricIds[i]);

        }
        params.put("id", ids);
        params.put("start", new String[] { Long.toString(start)});
        params.put("end", new String[] { Long.toString(end)});
        return doGet("metricData/getMulti.hqu", params, MetricsDataResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.LastMetricData} for the
     * given list of {@link org.hyperic.hq.hqapi1.types.Metric} ids. This object
     * represents the last metric collection for the given metric id.
     *
     * @param metricIds The ids of the {@link org.hyperic.hq.hqapi1.types.Metric}s to query.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the data was succesfully queried.  The returned data can be retrieved
     * via {@link org.hyperic.hq.hqapi1.types.LastMetricsDataResponse#getLastMetricData()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public LastMetricsDataResponse getData(int[] metricIds)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        String[] ids = new String[metricIds.length];
        for (int i = 0; i < metricIds.length; i++) {
            ids[i] = Integer.toString(metricIds[i]);

        }
        params.put("id", ids);
        return doGet("metricData/getMultiLast.hqu", params, LastMetricsDataResponse.class);
    }

    public StatusResponse addData(int metricId, List<DataPoint> data)
        throws IOException
    {
        DataPointsRequest request = new DataPointsRequest();
        request.setMetricId(metricId);
        request.getDataPoint().addAll(data);

        return doPost("metricData/put.hqu", request, StatusResponse.class);
    }
}
