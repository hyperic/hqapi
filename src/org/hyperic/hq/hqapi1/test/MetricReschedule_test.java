package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Resource;

import java.util.List;
import java.util.ArrayList;

public class MetricReschedule_test extends MetricTestBase {

    public MetricReschedule_test(String name) {
        super(name);
    }

    public void testRescheduleSingle() throws Exception {
        MetricApi api = getApi().getMetricApi();

        Resource platform = getLocalPlatformResource(false, false);
        List<Resource> resources = new ArrayList<Resource>();
        resources.add(platform);

        StatusResponse response = api.reschedule(resources);
        hqAssertSuccess(response);
    }

    public void testRescheduleMulti() throws Exception {
        MetricApi api = getApi().getMetricApi();

        // Recurse flag will cause all resources on this platform to be
        // rescheduled.
        Resource platform = getLocalPlatformResource(false, true);
        List<Resource> resources = new ArrayList<Resource>();
        resources.add(platform);

        StatusResponse response = api.reschedule(resources);
        hqAssertSuccess(response);
    }

    public void testRescheduleInvalidResource() throws Exception {
        MetricApi api = getApi().getMetricApi();

        List<Resource> resources = new ArrayList<Resource>();

        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);
        resources.add(r);

        StatusResponse response = api.reschedule(resources);
        hqAssertFailureObjectNotFound(response);
    }

    public void testRescheduleNoResources() throws Exception {

        MetricApi api = getApi().getMetricApi();

        List<Resource> resources = new ArrayList<Resource>();

        StatusResponse response = api.reschedule(resources);
        hqAssertFailureInvalidParameters(response);
    }
}
