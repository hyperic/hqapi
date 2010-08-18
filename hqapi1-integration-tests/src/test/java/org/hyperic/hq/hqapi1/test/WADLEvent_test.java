package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLEvent_test extends WADLTestBase {

    public void testFind() throws Exception {
        HttpLocalhost8080HquHqapi1.EventFindHqu find = new HttpLocalhost8080HquHqapi1.EventFindHqu();

        EventsResponse response = find.getAsEventsResponse(0l, System.currentTimeMillis(),
                                                           100);
        hqAssertSuccess(response);
    }

    public void testFindByResource() throws Exception {
        HttpLocalhost8080HquHqapi1.EventFindByResourceHqu find = new HttpLocalhost8080HquHqapi1.EventFindByResourceHqu();

        EventsResponse response = find.getAsEventsResponse(Integer.MAX_VALUE,
                                                           0l,
                                                           System.currentTimeMillis());
        hqAssertFailure(response); // Resource not found
    }
}
