package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.EventsResponse;
import org.hyperic.hq.hqapi1.types.Resource;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * The Hyperic HQ Event API.
 * <br><br>
 * This class provides access to the events within the HQ system.  Each of
 * the methods in this class return response objects that wrap the result of the
 * method with a {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 */
public class EventApi extends BaseApi {

    EventApi(HQConnection conn) {
        super(conn);
    }

    public enum EventStatus {
        /**
         * All Events.
         */
        ANY("ANY"),
        /**
         * Events with a status of Error.
         */
        ERROR("ERR"),
        /**
         * Events with a status of Warn.
         */
        WARN("WRN"),
        /**
         * Events with a status of Info.
         */
        INFO("INF"),
        /**
         * Events with a status of Debug.
         */
        DEBUG("DBG");

        private final String _status;

        EventStatus(String status) {
            _status = status;
        }

        public String getStatus() {
            return _status;
        }
    }

    public enum EventType {

        /**
         * All Events.
         */
        ANY(null),
        /**
         * Events for alerts firing.
         */
        ALERT("org.hyperic.hq.events.AlertFiredEvent"),
        /**
         * Events for baselines changing.
         */
        BASELINE("org.hyperic.hq.measurement.ext.BaselineChangeEvent"),
        /**
         * Events for custom properties changing.
         */
        CPROP("org.hyperic.hq.appdef.shared.CPropChangeEvent"),
        /**
         * Events for when a resource is cloned.
         */
        CLONE("org.hyperic.hq.events.CloningEvent"),
        /**
         * Events for when control actions are run.
         */
        CONTROL("org.hyperic.hq.control.ControlEvent"),
        /**
         * Events for when an escalation changes state.
         */
        ESCALATION("org.hyperic.hq.escalation.EscalationEvent"),
        /**
         * Events for when a maintenance window occurs.
         */
        MAINTENANCE("org.hyperic.hq.events.MaintenanceEvent"),
        /**
         * Events for log track messages.
         */
        LOG("org.hyperic.hq.measurement.shared.ResourceLogEvent");

        private final String _type;

        EventType(String type) {
            _type = type;
        }

        public String getType() {
            return _type;
        }
    }

    /**
     * Find {@link org.hyperic.hq.hqapi1.types.Event}s for a {@link Resource}.
     *
     * @param r The Resource to search for events
     * @param begin The beginning of the time window in epoch-millis.
     * @param end The end of the time window in epoch-millis.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of Events are returned via
     * {@link org.hyperic.hq.hqapi1.types.EventsResponse#getEvent()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public EventsResponse findEvents(Resource r, long begin, long end)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("begin", new String[] { Long.toString(begin)});
        params.put("end", new String[] { Long.toString(end)});
        params.put("resourceId", new String[] { Integer.toString(r.getId())});

        return doGet("event/findByResource.hqu", params, EventsResponse.class);
    }

    /**
     * Find {@link org.hyperic.hq.hqapi1.types.Event}s in HQ.
     *
     * @param begin The beginning of the time window in epoch-millis.
     * @param end The end of the time window in epoch-millis.
     * @param type The type of event to search for, or null for all types.
     * @param status The maximum status to include in the search results.
     * @param count The maximum number of Events to return.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of Events are returned via
     * {@link org.hyperic.hq.hqapi1.types.EventsResponse#getEvent()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public EventsResponse findEvents(long begin, long end,
                                     EventType type, EventStatus status,
                                     int count)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("begin", new String[] { Long.toString(begin)});
        params.put("end", new String[] { Long.toString(end)});
        params.put("type", new String[] { type != null ? type.getType() : null });
        params.put("status", new String[] { status != null ? status.getStatus() : null });
        params.put("count", new String[] { Integer.toString(count)});

        return doGet("event/find.hqu", params, EventsResponse.class);   
    }
}
