
import org.hyperic.hq.hqapi1.ErrorCode

import org.hyperic.hq.events.AlertSeverity
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.events.server.session.AlertSortField
import org.hyperic.hq.events.server.session.AlertManagerEJBImpl as AlertMan
import org.hyperic.hq.escalation.server.session.EscalationManagerEJBImpl as EscMan
import org.hyperic.hq.events.server.session.ClassicEscalationAlertType
import org.hyperic.hq.authz.shared.PermissionException

public class AlertController extends ApiController {

    private aMan = AlertMan.one
    private escMan = EscMan.one

    private static final int ROUNDING_VOODOO = 60000

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

    def get(params) {
        def id = params.getOne("id")?.toInteger()

        def failureXml = null

        if (id == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Alert id not given")
        }

        def alert
        if (!failureXml) {
            alert = getAlertById(id)
            
            if (!alert) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                           "Alert with id " + id +
                                           " not found")
            }
        }

        renderXml() {
            AlertResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    out << getAlertXML(alert)
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
                PageInfo pInfo = PageInfo.create(0, count, AlertSortField.DATE,
                                                 false);

                // TODO: Work around incorrect TimingVoodoo in AlertManagerEJBImpl
                long roundedEnd = end + (ROUNDING_VOODOO - (end % ROUNDING_VOODOO))
                long timerange = roundedEnd - begin

                alerts = alertHelper.findAlerts(severity, timerange, roundedEnd,
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
                    // TODO: See above, re-apply the actual end time
                    for (a in alerts.findAll { it.ctime <= end }) {
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
                    // TODO: Work around incorrect TimingVoodoo in AlertManagerEJBImpl
                    long roundedEnd = end + (ROUNDING_VOODOO - (end % ROUNDING_VOODOO))
                    alerts = resource.getAlerts(user, begin, roundedEnd,
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
                    // TODO: See above, re-apply the actual end time
                    for (a in alerts.findAll { it.ctime <= end }) {
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
        def ids = params.get("id")*.toInteger()
        def reason = params.getOne("reason")
        def pause = params.getOne("pause", "0").toLong()

        def failureXml = null
        def alerts = []

        if (ids == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Required parameter id not given")
        } else {
            for (id in ids) {
                def alert = getAlertById(id)
                if (!alert) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Unable to find alert with id = " + id)
                }
            }

            if (!failureXml) {
                try {
                    // TODO: Add to EscalationHelper
                    for (id in ids) {
                        def success = escMan.acknowledgeAlert(user, ClassicEscalationAlertType.CLASSIC,
                                                              id, reason, pause)
                        if (!success) {
                            // TODO: Should re-evaluate this in the future, should we return an error?
                            log.warn("Alert id " + id + " was not in an " +
                                     "acknowledgable state")
                        }
                        // Re-lookup alert so we have the correct escalation state
                        alerts << getAlertById(id)
                    }
                } catch (PermissionException e) {
                    failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
                } catch (Throwable t) {
                    failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
                                               t.getMessage())
                }
            }
        }

        renderXml() {
            AlertsResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (alert in alerts) {
                        out << getAlertXML(alert)
                    }
                }
            }
        }
    }

    private canManageAlerts(resource) {
        // TODO: Fix AlertManager to handle permissions
        def appdefResource
        // TODO: Platform/Server/Service have different modifyAlert operations
        def operation
        if (resource.isPlatform()) {
            operation = "modifyAlerts"
            appdefResource = resource.toPlatform()
        } else if (resource.isServer()) {
            operation = "modifyAlerts"
            appdefResource = resource.toServer()
        } else if (resource.isService()) {
            operation = "manageAlerts"
            appdefResource = resource.toService()
        } else {
            log.warn("Unhandled resource to canManageAlerts: " + resource.name)
            return false
        }

        try {
            appdefResource.checkPerms(operation:operation, user:user)
            return true
        } catch (Exception e) {
            return false
        }
    }

    def fix(params) {
        def ids = params.get("id")*.toInteger()
        def failureXml = null
        def alerts = []

        if (ids == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Required parameter id not given")

        } else {
            def alertsToFix = []
            for (id in ids) {
                def alert = getAlertById(id)
                if (!alert) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Unable to find alert with id = " + id)
                } else if (!canManageAlerts(alert.definition.resource)) {
                    failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
                } else {
                    alertsToFix << alert
                }
            }

            if (!failureXml) {
                try {
                    // TODO: Add to AlertCategory
                    for (alert in alertsToFix) {
                        aMan.setAlertFixed(alert)
                        alerts << getAlertById(alert.id)
                    }
                } catch (Exception e) {
                    failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
                                               e.getMessage())
                }
            }
        }

        renderXml() {
            AlertsResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (alert in alerts) {
                        out << getAlertXML(alert)
                    }
                }
            }
        }
    }

    def delete(params) {
        def ids = params.get("id")*.toInteger()
        def failureXml = null
        if (ids == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Required parameter id not given")
        } else {
            for (id in ids) {
                def alert = getAlertById(id)
                if (!alert) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Unable to find alert with id = " + id)
                } else if (!canManageAlerts(alert.definition.resource)) {
                    failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
                }
            }

            if (!failureXml) {
                try {
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