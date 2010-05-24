package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.DataPoint;
import org.hyperic.hq.hqapi1.types.DataPointsRequest;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Metric;

import java.io.IOException;
import java.util.List;

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
                      StatusResponse.class);
    }
}
