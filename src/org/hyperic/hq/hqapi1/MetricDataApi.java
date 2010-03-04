package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.DataPoint;
import org.hyperic.hq.hqapi1.types.MetricDataResponse;
import org.hyperic.hq.hqapi1.types.DataPointsRequest;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.LastMetricsDataResponse;
import org.hyperic.hq.hqapi1.types.LastMetricDataResponse;
import org.hyperic.hq.hqapi1.types.MetricsDataResponse;
import org.hyperic.hq.hqapi1.types.Metric;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * The Hyperic HQ MetricData API.
 * <br><br>
 * This class provides access to the @{link MetricData} within the HQ system.  Each of the
 * methods in this class return {@link org.hyperic.hq.hqapi1.types.Response}
 * objects that wrap the result of the method with a
 * {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 */
public class MetricDataApi extends BaseApi {

    MetricDataApi(HQConnection conn) {
        super(conn);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MetricData} for the
     * given {@link org.hyperic.hq.hqapi1.types.Metric}.
     *
     * @param metric The {@link org.hyperic.hq.hqapi1.types.Metric} to query.
     * @param start The start time to query, in epoch-millis.
     * @param end The end time to query, in epoch-millis.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the data was succesfully queried.  The returned data can be retrieved
     * via {@link org.hyperic.hq.hqapi1.types.MetricDataResponse#getMetricData()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */    
    public MetricDataResponse getData(Metric metric, long start, long end)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(metric.getId()) });
        params.put("start", new String[] { Long.toString(start)});
        params.put("end", new String[] { Long.toString(end)});
        return doGet("metricData/get.hqu", params, 
                     new XmlResponseHandler<MetricDataResponse>(MetricDataResponse.class));
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.LastMetricData} for the
     * given {@link org.hyperic.hq.hqapi1.types.Metric}. This object
     * represents the last metric collection for the given Metric.
     *
     * @param metric The {@link org.hyperic.hq.hqapi1.types.Metric} to query.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the data was succesfully queried.  The returned data can be retrieved
     * via {@link org.hyperic.hq.hqapi1.types.LastMetricDataResponse#getLastMetricData()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public LastMetricDataResponse getData(Metric metric)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(metric.getId()) });
        return doGet("metricData/getLast.hqu", params, 
                     new XmlResponseHandler<LastMetricDataResponse>(LastMetricDataResponse.class));
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MetricData} for the
     * given List of {@link org.hyperic.hq.hqapi1.types.Metric}s.
     *
     * @param metrics The List of {@link org.hyperic.hq.hqapi1.types.Metric}s to query.
     * @param start The start time to query, in epoch-millis.
     * @param end The end time to query, in epoch-millis.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the data was succesfully queried.  The returned data can be retrieved
     * via {@link org.hyperic.hq.hqapi1.types.MetricsDataResponse#getMetricData()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MetricsDataResponse getData(List<Metric> metrics, long start, long end)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        String[] ids = new String[metrics.size()];
        for (int i = 0; i < metrics.size(); i++) {
            ids[i] = Integer.toString(metrics.get(i).getId());

        }
        params.put("id", ids);
        params.put("start", new String[] { Long.toString(start)});
        params.put("end", new String[] { Long.toString(end)});
        return doGet("metricData/getMulti.hqu", params, 
                     new XmlResponseHandler<MetricsDataResponse>(MetricsDataResponse.class));
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.LastMetricData} for the
     * given List of {@link org.hyperic.hq.hqapi1.types.Metric}s. This object
     * represents the last metric collection for the given Metric.
     *
     * @param metrics The List of {@link org.hyperic.hq.hqapi1.types.Metric}s to query.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the data was succesfully queried.  The returned data can be retrieved
     * via {@link org.hyperic.hq.hqapi1.types.LastMetricsDataResponse#getLastMetricData()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public LastMetricsDataResponse getData(List<Metric> metrics)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        String[] ids = new String[metrics.size()];
        for (int i = 0; i < metrics.size(); i++) {
            ids[i] = Integer.toString(metrics.get(i).getId());

        }
        params.put("id", ids);
        return doGet("metricData/getMultiLast.hqu", params, 
                     new XmlResponseHandler<LastMetricsDataResponse>(LastMetricsDataResponse.class));
    }

    /**
     * Insert {@link org.hyperic.hq.hqapi1.types.DataPoint}s for the specified
     * Metric.
     *
     * @param metric The Metric to insert data for.
     * @param data A List of {@link org.hyperic.hq.hqapi1.types.DataPoint}s to insert.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * data was sucessfully inserted.
     * 
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse addData(Metric metric, List<DataPoint> data)
        throws IOException
    {
        DataPointsRequest request = new DataPointsRequest();
        request.setMetricId(metric.getId());
        request.getDataPoint().addAll(data);

        return doPost("metricData/put.hqu", request, 
                      new XmlResponseHandler<StatusResponse>(StatusResponse.class));
    }
}
