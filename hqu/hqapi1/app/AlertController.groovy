
import org.hyperic.hq.hqapi1.ErrorCode

import org.hyperic.hq.events.AlertSeverity
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.events.server.session.AlertSortField
import org.hyperic.hq.events.server.session.AlertManagerEJBImpl as AlertMan
import org.hyperic.hq.escalation.server.session.EscalationManagerEJBImpl as EscMan
import org.hyperic.hq.events.server.session.ClassicEscalationAlertType

public class AlertController extends ApiController {

    private aMan = AlertMan.one
    private escMan = EscMan.one

    private getEscalationState(alert) {
        // TODO: Move to AlertCategory
        if (alert.stateId) {
            return escMan.findEscalationState(alert.alertDefinition)
        }
        return null
    }

    private Closure getAlertXML(a) {
        { doc ->
            Alert(id                : a.id,
                  name              : a.alertDefinition.name,
                  alertDefinitionId : a.alertDefinition.id,
                  resourceId        : a.alertDefinition.resource.id,
                  ctime             : a.ctime,
                  fixed             : a.fixed,
                  reason            : aMan.getLongReason(a).trim()) {
                def e = getEscalationState(a)
                if (e) {
                    EscalationState(ackedBy: e.acknowledgedBy?.name,
                                    escalationId: e.escalation.id,
                                    nextActionTime: e.nextActionTime)
                }
                for (l in a.actionLog) {
                    if (l.subject) {
                        // Ignore 'internal' logs.
                        AlertActionLog(timestamp: l.timeStamp,
                                       detail: l.detail,
                                       user: l.subject?.name)
                    }
                }
            }
        }
    }

    def find(params) {
        Long    begin    = params.getOne("begin")?.toLong()
        Long    end      = params.getOne("end")?.toLong()
        Integer count    = params.getOne("count")?.toInteger()
        Integer sev      = params.getOne("severity")?.toInteger()
        Boolean inEsc    = params.getOne("inEscalation", "false").toBoolean()
        Boolean notFixed = params.getOne("notFixed", "false").toBoolean()

        def failureXml = null
        def alerts = []
        if (begin == null || end == null || count == null || sev == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "All required attributes not given")
        } else if (begin >= end) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Begin must be < end")
        } else if (count <= 0) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Count must ben > 0")
        } else {
            try {
                Integer groupId = null
                AlertSeverity severity = AlertSeverity.findByCode(sev)
                long timerange = end - begin
                PageInfo pInfo = PageInfo.create(0, count, AlertSortField.DATE,
                                                 false);

                alerts = alertHelper.findAlerts(severity, timerange, end,
                                                inEsc, notFixed, groupId, pInfo)
            } catch (IllegalStateException e) {
                failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                           "Invalid severity " + sev)

            } catch (Throwable t) {
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
                                           t.getMessage())
            }
        }

        renderXml() {
            AlertsResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (a in alerts) {
                        out << getAlertXML(a)
                    }
                }
            }
        }
    }

    def findByResource(params) {
        Integer rid = params.getOne("resourceId")?.toInteger()
        Long    begin    = params.getOne("begin")?.toLong()
        Long    end      = params.getOne("end")?.toLong()
        Integer count    = params.getOne("count")?.toInteger()
        Integer sev      = params.getOne("severity")?.toInteger()
        Boolean inEsc    = params.getOne("inEscalation", "false").toBoolean()
        Boolean notFixed = params.getOne("notFixed", "false").toBoolean()

        def failureXml = null
        def alerts = []
        if (rid == null || begin == null || end == null || sev == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "All required attributes not given")
        } else if (begin >= end) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Begin must be < end")
        } else if (count <= 0) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Count must ben > 0")
        } else {
            try {
                AlertSeverity severity = AlertSeverity.findByCode(sev)
                def resource = getResource(rid)
                if (!resource) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Unable to find resource with " +
                                               "id=" + rid)
                } else {
                    alerts = resource.getAlerts(user, begin, end,
                                                count, severity)
                    //TODO: Move these to ResourceCategory or AlertManager
                    if (inEsc) {
                        alerts = alerts.findAll { it.stateId != null && it.stateId > 0 }
                    }

                    if (notFixed) {
                        alerts = alerts.findAll { !it.fixed }
                    }
                }
            } catch (IllegalStateException e) {
                failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                           "Invalid severity " + sev)

            } catch (Throwable t) {
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
                                           t.getMessage())
            }
        }

        renderXml() {
            AlertsResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (a in alerts) {
                        out << getAlertXML(a)
                    }
                }
            }
        }
    }

    private getAlertById(id) {
        // TODO: Add get method in AlertHelper
        try {
            return aMan.findAlertById(id)
        } catch (Throwable t) {
            return null
        }
    }

    def ack(params) {
        def id = params.getOne("id")?.toInteger()
        def reason = params.getOne("reason")
        def pause = params.getOne("pause", "0").toLong()

        def failureXml = null

        if (id == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Required parameter id not given")
        } else {
            def alert = getAlertById(id)
            if (!alert) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                           "Unable to find alert with id = " + id)  
            } else {
                try {
                    // TODO: Add to EscalationHelper
                    escMan.acknowledgeAlert(user, ClassicEscalationAlertType.CLASSIC,
                                            id, reason, pause)
                } catch (Throwable t) {
                    failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
                                               t.getMessage())
                }
            }
        }

        renderXml() {
            StatusResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def fix(params) {
        def id = params.getOne("id")?.toInteger()

        def failureXml = null
        if (id == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Required parameter id not given")

        } else {
            def alert = getAlertById(id)
            if (!alert) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                           "Unable to find alert with id = " + id)
            } else {
                try {
                    // TODO: Add to AlertCategory
                    aMan.setAlertFixed(alert)
                } catch (Exception e) {
                    failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
                                               e.getMessage())
                }
            }
        }

        renderXml() {
            StatusResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def delete(params) {
        def id = params.getOne("id")?.toInteger()
        def failureXml = null
        if (id == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Required parameter id not given")
        } else {
            def alert = getAlertById(id)
            if (!alert) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                           "Unable to find alert with id = " + id)
            } else {
                try {
                    def ids = [alert.id]
                    // TODO: Add to AlertCategory
                    aMan.deleteAlerts(ids as Integer[])
                } catch (Exception e) {
                    failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
                                               e.getMessage())
                }
            }
        }

        renderXml() {
            StatusResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }
}