package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EventApi;
import org.hyperic.hq.hqapi1.types.EventsResponse;

public class EventFind_test extends HQApiTestBase {

    public EventFind_test(String name) {
        super(name);
    }

    public void testValidFind() throws Exception {

        EventApi api = getApi().getEventApi();

        EventsResponse response = api.findEvents(0, System.currentTimeMillis(),
                                                 null, null, 100);
        hqAssertSuccess(response);
    }

    public void testFindBadRange() throws Exception {

        EventApi api = getApi().getEventApi();

        EventsResponse response = api.findEvents(System.currentTimeMillis(), 0,
                                                 null, null, 100);
        hqAssertFailureInvalidParameters(response);
    }
}
