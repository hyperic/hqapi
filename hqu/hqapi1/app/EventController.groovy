
import org.hyperic.hq.hqapi1.ErrorCode
import org.hyperic.hq.events.EventLogStatus
import org.hyperic.hq.events.server.session.EventLogManagerEJBImpl as EMan
import org.hyperic.hq.events.server.session.EventLogSortField
import org.hyperic.hibernate.PageInfo

class EventController extends ApiController {

    private eMan = EMan.one

    private Closure getEventXML(e) {
        { doc ->
            Event(detail     : e.detail,
                  type       : e.type,
                  ctime      : e.timestamp,
                  user       : e.subject,
                  status     : e.status,
                  resourceId : e.resource?.id)
        }
    }

    def findByResource(params) {
        def resourceId = params.getOne("resourceId")?.toInteger()
        def begin = params.getOne("begin")?.toLong()
        def end = params.getOne("end")?.toLong()
        def resource = getResource(resourceId)
        def failureXml = null

        def events = []
        if (!resource) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                       "Unable to find resource with id=" +
                                       resourceId)
        } else if (begin == null || end == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Both begin and end parameters are required")
        } else if (begin > end) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Begin must be < end time")
        } else {
            events = resource.getLogs(user, begin, end)
        }

        renderXml() {
            EventsResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (e in events) {
                        out << getEventXML(e)
                    }
                }
            }
        }
    }

    def find(params) {
        def begin = params.getOne("begin")?.toLong()
        def end = params.getOne("end")?.toLong()
        def type = params.getOne("type")
        def status = params.getOne("status")
        def count = params.getOne("count")?.toInteger()

        def failureXml = null

        def events = []
        if (begin == null || end == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Both begin and end parameters are required")
        } else if (begin > end) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Begin must be < end time")
        } else if (count == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Count not specified")
        } else {
            def eventEnum;
            if (status == null || status == "ANY") {
                eventEnum = EventLogStatus.findByCode(10)
            } else if (status == "ERR") {
                eventEnum = EventLogStatus.findByCode(3)
            } else if (status == "WRN") {
                eventEnum = EventLogStatus.findByCode(4)
            } else if (status == "INF") {
                eventEnum = EventLogStatus.findByCode(6)
            } else if (status == "DBG") {
                eventEnum = EventLogStatus.findByCode(7)
            } else {
                failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                           "Unknown status " + status)
            }

            if (failureXml == null) {
                def pInfo = PageInfo.create(0, count, EventLogSortField.DATE,
                                            false)  
                events = eMan.findLogs(user, begin, end, pInfo,
                                       eventEnum, type, null)
            }
        }


        renderXml() {
            EventsResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (e in events) {
                        out << getEventXML(e.eventLog)
                    }
                }
            }
        }
    }
}