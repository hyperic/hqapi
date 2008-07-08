package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.DisableMetricResponse;
import org.hyperic.hq.hqapi1.types.EnableMetricResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.SetIntervalResponse;

public class MetricApi {

    private static final String HQU_URI = "/hqu/hqapi1/metric";

    private final HQConnection _conn;

    MetricApi(HQConnection conn) {
        _conn = conn;
    }
    
    /**
     * Disable a {@link org.hyperic.hq.hqapi1.types.Metric}.
     *
     * @param metric The metric to disable.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric was successfully disabled.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>InvalidParameters - All the required parameters in the Metric object were not supplied.
     *   <li>ObjectNotFound - The given Metric does not exist.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public DisableMetricResponse disableMetric(Metric metric) {
        return null;
    }

    /**
     * Enable a {@link org.hyperic.hq.hqapi1.types.Metric}.
     *
     * @param metric The metric to enable.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric was successfully enabled.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>InvalidParameters - All the required parameters in the Metric object were not supplied.
     *   <li>ObjectNotFound - The specified Metric does not exist.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public EnableMetricResponse enableMetric(Metric metric) {
        return null;
    }

    /**
     * Set a {@link org.hyperic.hq.hqapi1.types.Metric} collection interval.
     *
     * @param metric The metric to change the collection interval.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric's collection interval was successfully updated.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>InvalidParameters - All the required parameters in the Metric object were not supplied.
     *   <li>ObjectNotFound - The given Metric does not exist.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public SetIntervalResponse setInterval(Metric metric, int interval) {
        return null;
    }

}
