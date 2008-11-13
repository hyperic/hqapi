package org.hyperic.hq.hqapi1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.types.DisableMetricResponse;
import org.hyperic.hq.hqapi1.types.EnableMetricResponse;
import org.hyperic.hq.hqapi1.types.GetMetricResponse;
import org.hyperic.hq.hqapi1.types.GetMetricTemplateResponse;
import org.hyperic.hq.hqapi1.types.ListMetricResponse;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultIndicatorResponse;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultIntervalResponse;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultOnResponse;
import org.hyperic.hq.hqapi1.types.SetMetricIntervalResponse;

/**
 * 
 * NOTES:
 * 1) should we error out for scenarios where a user wants to enable a metric
 * which is already enabled or ignore?
 * (difference btwn metrics and metrictemplates?)
 *
 */
public class MetricApi {

    private static final String HQU_URI = "/hqu/hqapi1/metric";

    private final HQConnection _conn;

    MetricApi(HQConnection conn) {
        _conn = conn;
    }
    
    /**
     * List {@link org.hyperic.hq.hqapi1.types.Metric}s associated with a
     * resource
     *
     * @param resourceId The associated resourceId which the metrics belong.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metrics were successfully retrieved.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes may be returned:
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @throws IOException If a network error occurs while making the request.
     */
    public ListMetricResponse listMetrics(Integer resourceId)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceId", resourceId.toString());
        return _conn.doGet(HQU_URI+"/listMetrics.hqu", params,
            ListMetricResponse.class);
    }
    
    /**
     * Disable a {@link org.hyperic.hq.hqapi1.types.Metric}.
     *
     * @param metricId The metric to disable.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric was successfully disabled.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes may be returned:
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @throws IOException If a network error occurs while making the request.
     */
    public DisableMetricResponse disableMetric(Integer metricId)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", metricId.toString());
        return _conn.doGet(HQU_URI+"/disableMetric.hqu", params,
            DisableMetricResponse.class);
    }

    /**
     * Enable a {@link org.hyperic.hq.hqapi1.types.Metric}.
     *
     * @param metricId The metric to enable.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric was successfully enabled.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes may be returned:
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @throws IOException If a network error occurs while making the request.
     */
    public EnableMetricResponse enableMetric(Integer metricId, Long interval)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", metricId.toString());
        params.put("interval", interval.toString());
        return _conn.doGet(HQU_URI+"/enableMetric.hqu", params,
            EnableMetricResponse.class);
    }

    /**
     * Set a {@link org.hyperic.hq.hqapi1.types.Metric} collection interval.
     *
     * @param metricId The metric to change the collection interval.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric's collection interval was successfully updated.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes may be returned:
     * @throws IOException 
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @throws IOException If a network error occurs while making the request.
     */
    public SetMetricIntervalResponse setInterval(Integer metricId,
                                                 Integer interval)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", metricId.toString());
        params.put("interval", interval.toString());
        return _conn.doGet(HQU_URI+"/setInterval.hqu", params,
            SetMetricIntervalResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.Metric} associated
     * with the metricId
     *
     * @param metricId The metricId used to retrieve the associated
     *  {@link org.hyperic.hq.hqapi1.types.Metric}.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * {@link org.hyperic.hq.hqapi1.types.Metric} was retrieved
     * successfully
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes may be returned:
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @throws IOException If a network error occurs while making the request.
     */
    public GetMetricResponse getMetric(Integer metricId)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", metricId.toString());
        return _conn.doGet(HQU_URI+"/getMetric.hqu", params,
            GetMetricResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MetricTemplate} associated
     * with the metricId
     *
     * @param metricId The metricId used to retrieve the associated
     *  {@link org.hyperic.hq.hqapi1.types.MetricTemplate}.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * {@link org.hyperic.hq.hqapi1.types.MetricTemplate} was retrieved
     * successfully
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes may be returned:
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @throws IOException If a network error occurs while making the request.
     */
    public GetMetricTemplateResponse getMetricTemplate(Integer metricId)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", metricId.toString());
        return _conn.doGet(HQU_URI+"/getMetricTemplate.hqu", params,
            GetMetricTemplateResponse.class);
    }
    
    /**
     * Sets the defaultOn behavior for all
     *  {@link org.hyperic.hq.hqapi1.types.Metric}s associated with this
     *  {@link org.hyperic.hq.hqapi1.types.MetricTemplate}
     *
     * @param templateId Sets the associated metric template's defaultOn
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric's collection interval was successfully updated.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes may be returned:
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @throws IOException If a network error occurs while making the request.
     */
    public SetMetricDefaultOnResponse setDefaultOn(Integer templateId,
                                                   Boolean on)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("templateId", templateId.toString());
        params.put("on", on.toString());
        return _conn.doGet(HQU_URI+"/setDefaultOn.hqu", params,
            SetMetricDefaultOnResponse.class);
    }
    
    /**
     * Sets the defaultIndicator for all
     *  {@link org.hyperic.hq.hqapi1.types.Metric}s associated with this
     *  {@link org.hyperic.hq.hqapi1.types.MetricTemplate}
     *
     * @param templateId Sets the associated metric template's default
     *  indicator
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric's collection interval was successfully updated.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes may be returned:
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @throws IOException If a network error occurs while making the request.
     */
    public SetMetricDefaultIndicatorResponse setDefaultIndicator(Integer templateId,
                                                                 Boolean on)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("templateId", templateId.toString());
        params.put("on", on.toString());
        return _conn.doGet(HQU_URI+"/setDefaultIndicator.hqu", params,
            SetMetricDefaultIndicatorResponse.class);
    }
    
    /**
     * Set a {@link org.hyperic.hq.hqapi1.types.Metric} collection interval.
     *
     * @param templateId Sets the associated metric template's default interval
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric template's default collection interval was successfully updated.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes may be returned:
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#LOGIN_FAILURE
     * @see org.hyperic.hq.hqapi1.ErrorCode#OBJECT_NOT_FOUND
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @throws IOException If a network error occurs while making the request.
     */
    public SetMetricDefaultIntervalResponse setDefaultInterval(Integer templateId,
                                                               Integer interval)
        throws IOException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("templateId", templateId.toString());
        params.put("interval", interval.toString());
        return _conn.doGet(HQU_URI+"/setDefaultInterval.hqu", params,
            SetMetricDefaultIntervalResponse.class);
    }
}
