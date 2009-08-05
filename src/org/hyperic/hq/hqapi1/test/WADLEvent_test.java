package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLEvent_test extends WADLTestBase {

    public void testFind() throws Exception {
        Endpoint.EventFindHqu find = new Endpoint.EventFindHqu();

        EventsResponse response = find.getAsEventsResponse(0l, System.currentTimeMillis(),
                                                           100);
        hqAssertSuccess(response);
    }

    public void testFindByResource() throws Exception {
        Endpoint.EventFindByResourceHqu find = new Endpoint.EventFindByResourceHqu();

        EventsResponse response = find.getAsEventsResponse(Integer.MAX_VALUE,
                                                           0l,
                                                           System.currentTimeMillis());
        hqAssertFailure(response); // Resource not found
    }
}
