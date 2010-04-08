package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EventApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.EventsResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.Event;
import org.hyperic.hq.hqapi1.types.ResourceResponse;

public class EventFindByResource_test extends HQApiTestBase {

    public EventFindByResource_test(String name) {
        super(name);
    }

    public void testFindValid() throws Exception {
        HQApi api = getApi();
        ResourceApi rApi = api.getResourceApi();
        EventApi eventApi = api.getEventApi();

        EventsResponse response = eventApi.findEvents(0, System.currentTimeMillis(),
                                                      null, null, 100);
        hqAssertSuccess(response);

        if (response.getEvent().size() > 0) {
            Event e = response.getEvent().get(0);

            ResourceResponse resource = rApi.getResource(e.getResourceId(), false, false);
            hqAssertSuccess(resource);

            EventsResponse eventsByResource =
                    eventApi.findEvents(resource.getResource(), 0,
                                        System.currentTimeMillis());
            hqAssertSuccess(eventsByResource);
            assertTrue("No events found!", response.getEvent().size() > 0);

        } else {
            System.out.println("WARN: No events found");
        }
    }

    public void testFindInvalidResource() throws Exception {

        EventApi api = getApi().getEventApi();

        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);
        EventsResponse response = api.findEvents(r, 0,
                                                 System.currentTimeMillis());
        hqAssertFailureObjectNotFound(response);
    }
}
