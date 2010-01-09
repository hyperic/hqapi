/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.MetricDataResponse;
import org.hyperic.hq.hqapi1.types.MetricResponse;
import org.hyperic.hq.hqapi1.types.MetricTemplate;
import org.hyperic.hq.hqapi1.types.MetricTemplateResponse;
import org.hyperic.hq.hqapi1.types.MetricTemplatesRequest;
import org.hyperic.hq.hqapi1.types.MetricTemplatesResponse;
import org.hyperic.hq.hqapi1.types.MetricsDataResponse;
import org.hyperic.hq.hqapi1.types.MetricsRequest;
import org.hyperic.hq.hqapi1.types.MetricsResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.MetricsRescheduleRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * The Hyperic HQ Metric API.
 * <br><br>
 * This class provides access to the {@link org.hyperic.hq.hqapi1.types.Metric}s,
 * {@link org.hyperic.hq.hqapi1.types.MetricTemplate}s, and
 * {@link org.hyperic.hq.hqapi1.types.MetricData} within the HQ system.
 * Each of the methods in this class return
 * {@link org.hyperic.hq.hqapi1.types.Response} objects that wrap the result
 * of the method with a
 * {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 */
public class MetricApi extends BaseApi {

    MetricApi(HQConnection conn) {
        super(conn);
    }
    
    /**
     * List all {@link org.hyperic.hq.hqapi1.types.Metric}s associated with a
     * {@link org.hyperic.hq.hqapi1.types.Resource}
     *
     * @param resource The associated {@link org.hyperic.hq.hqapi1.types.Resource} which the metrics belong.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metrics were successfully retrieved.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MetricsResponse getMetrics(Resource resource)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("resourceId", new String[] { Integer.toString(resource.getId()) });
        return doGet("metric/getMetrics.hqu", params,
                     MetricsResponse.class);
    }

    /**
     * List all enabled {@link org.hyperic.hq.hqapi1.types.Metric}s associated with a
     * {@link org.hyperic.hq.hqapi1.types.Resource}
     *
     * @param resource The associated {@link org.hyperic.hq.hqapi1.types.Resource}
     * which the metrics belong.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metrics were successfully retrieved.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MetricsResponse getEnabledMetrics(Resource resource)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("resourceId", new String[] { Integer.toString(resource.getId()) });
        params.put("enabled", new String[] { Boolean.toString(true) });
        return doGet("metric/getMetrics.hqu", params,
                     MetricsResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.Metric} associated
     * with the metricId
     *
     * @param id The id used to retrieve the associated
     *  {@link org.hyperic.hq.hqapi1.types.Metric}.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * {@link org.hyperic.hq.hqapi1.types.Metric} was retrieved
     * successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MetricResponse getMetric(int id)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(id) });
        return doGet("metric/getMetric.hqu", params,
                     MetricResponse.class);
    }

    /**
     * Sync a List of {@link Metric}s.
     *
     * @param metrics The List of Metrics to sync.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * Metrics were synced successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse syncMetrics(List<Metric> metrics)
        throws IOException
    {
        MetricsRequest syncRequest = new MetricsRequest();
        syncRequest.getMetric().addAll(metrics);
        return doPost("metric/syncMetrics.hqu", syncRequest,
                      StatusResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MetricTemplate} associated
     * with the metric id.
     *
     * @param id The id used to retrieve the associated
     *  {@link org.hyperic.hq.hqapi1.types.MetricTemplate}.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * {@link org.hyperic.hq.hqapi1.types.MetricTemplate} was retrieved
     * successfully
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MetricTemplateResponse getMetricTemplate(int id)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(id) });
        return doGet("metric/getMetricTemplate.hqu", params,
                     MetricTemplateResponse.class);
    }

    /**
     * List all {@link org.hyperic.hq.hqapi1.types.MetricTemplate}s associated
     * with the given {@link org.hyperic.hq.hqapi1.types.ResourcePrototype}.
     *
     * @param prototype The associated {@link org.hyperic.hq.hqapi1.types.ResourcePrototype}
     * to query.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric templates were successfully retrieved.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MetricTemplatesResponse getMetricTemplates(ResourcePrototype prototype)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("prototype", new String[] { prototype.getName() });
        return doGet("metric/getTemplates.hqu", params,
                     MetricTemplatesResponse.class);
    }

    /**
     * Sync a List of {@link MetricTemplate}s.
     *
     * @param templates The List of MetricTemplates to update.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * MetricTemplates were synced successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse syncMetricTemplates(List<MetricTemplate> templates)
        throws IOException
    {
        MetricTemplatesRequest syncRequest = new MetricTemplatesRequest();
        syncRequest.getMetricTemplate().addAll(templates);
        return doPost("metric/syncTemplates.hqu", syncRequest,
                      StatusResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MetricData} for the
     * given {@link org.hyperic.hq.hqapi1.types.Metric}
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
    public MetricDataResponse getMetricData(int metricId, long start, long end)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("metricId", new String[] { Integer.toString(metricId) });
        params.put("start", new String[] { Long.toString(start) });
        params.put("end", new String[] { Long.toString(end) });

        return doGet("metric/getData.hqu", params, MetricDataResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MetricData} for the given
     * {@link org.hyperic.hq.hqapi1.types.Group} and {@link org.hyperic.hq.hqapi1.types.MetricTemplate}.
     *
     * @param groupId The id of the compatible {@link org.hyperic.hq.hqapi1.types.Group} to query.
     * @param templateId The id of the {@link org.hyperic.hq.hqapi1.types.MetricTemplate}
     * to query for data.  The template must belong to the
     * {@link org.hyperic.hq.hqapi1.types.ResourcePrototype} for the passed in
     * compatible group.
     * @param start The start time to query, in epoch-millis.
     * @param end The end time to query, in epoch-millis.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the data was succesfully queried.  The returned data can be retrieved
     * via {@link org.hyperic.hq.hqapi1.types.MetricsDataResponse#getMetricData()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MetricsDataResponse getMetricData(int groupId, int templateId,
                                             long start, long end)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("groupId", new String[] { Integer.toString(groupId) });
        params.put("templateId", new String[] { Integer.toString(templateId) });
        params.put("start", new String[] { Long.toString(start) });
        params.put("end", new String[] { Long.toString(end) });

        return doGet("metric/getGroupData.hqu", params,
                     MetricsDataResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MetricData} for the given
     * list of {@link Resource}s and {@link org.hyperic.hq.hqapi1.types.MetricTemplate}.
     *
     * @param resourceIds The list of {@link org.hyperic.hq.hqapi1.types.Resource}s to
     * query.  It is required that all Resources in this list are of the same
     * {@link org.hyperic.hq.hqapi1.types.ResourcePrototype}.
     * @param templateId The id of the {@link org.hyperic.hq.hqapi1.types.MetricTemplate}
     * to query for data.  The template must belong to the
     * {@link org.hyperic.hq.hqapi1.types.ResourcePrototype} for the passed in
     * resources.
     * @param start The start time to query, in epoch-millis.
     * @param end The end time to query, in epoch-millis.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the data was succesfully queried.  The returned data can be retrieved
     * via {@link org.hyperic.hq.hqapi1.types.MetricsDataResponse#getMetricData()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MetricsDataResponse getMetricData(int[] resourceIds, int templateId,
                                             long start, long end)
        throws IOException {

        Map<String,String[]> params = new HashMap<String,String[]>();
        String[] ids = new String[resourceIds.length];
        for (int i = 0; i < resourceIds.length; i++) {
            ids[i] = Integer.toString(resourceIds[i]);
        }
        params.put("ids", ids);
        params.put("templateId", new String[] { Integer.toString(templateId) });
        params.put("start", new String[] { Long.toString(start) });
        params.put("end", new String[] { Long.toString(end) });

        return doGet("metric/getResourceData.hqu", params,
                     MetricsDataResponse.class);
    }

    // Helper function to unroll a resource and it's children into a single list.
    private List<Resource> getFlattenResources(List<Resource> resources) {
        List<Resource> result = new ArrayList<Resource>();

        for (Resource r : resources) {
            result.add(r);
            if (r.getResource().size() > 0) {
                result.addAll(getFlattenResources(r.getResource()));
            }
        }
        return result;
    }

    /**
     * Reschedule metrics for the given list of {@link org.hyperic.hq.hqapi1.types.Resource}s.
     * If the passed in List has child resources, those Resources will also be
     * rescheduled.
     *
     * @param resources A List of {@link org.hyperic.hq.hqapi1.types.Resource}'s
     * to reschedule.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metrics were successfully retrieved.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse reschedule(List<Resource> resources)
        throws IOException {

        List<Resource> flattened = getFlattenResources(resources);

        MetricsRescheduleRequest request = new MetricsRescheduleRequest();
        request.getResource().addAll(flattened);

        return doPost("metric/reschedule.hqu", request,
                      StatusResponse.class);
    }
}
