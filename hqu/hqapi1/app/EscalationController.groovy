import org.hyperic.hq.hqu.rendit.BaseController

import org.hyperic.hq.authz.shared.PermissionException;
import org.hyperic.hq.hqapi1.ErrorCode;

class EscalationController extends ApiController {
    private Closure getEscalationXML(e) {
        { doc -> 
            Escalation(id :           e.id,
                       name :         e.name,
                       description :  e.description,
                       pauseAllowed : e.pauseAllowed,
                       maxPauseTime : e.maxPauseTime,
                       notifyAll :    e.notifyAll,
                       repeat :       e.repeat)
        }
    }

    def get(params) {
        def id = params.getOne("id")?.toInteger()
        def name = params.getOne("name")
        
        def esc = escalationHelper.getEscalation(id, name)

        renderXml() {
            out << GetEscalationResponse() {
                if (!esc) {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                }
                else {
                    out << getSuccessXML()
                    out << getEscalationXML(esc)
                }
            }
        }
    }

    def list(params) {
        renderXml() {
            out << ListEscalationsResponse() {
                out << getSuccessXML()
                for (e in escalationHelper.allEscalations.sort {a, b -> a.name <=> b.name}) {
                    out << getEscalationXML(e)
                }
            }
        }
    }

    def delete(params) {
        def id = params.getOne("id").toInteger()
        def esc = escalationHelper.deleteEscalation(id)
        renderXml() {
            out << DeleteEscalationResponse() {
                out << getSuccessXML()
            }
        }
    }
    
    def create(params) {
        def name         = params.getOne("name")
        def desc         = params.getOne("description")
        def pauseAllowed = params.getOne("pauseAllowed").toBoolean()
        def maxWaitTime  = params.getOne("maxWaitTime").toLong()
        def notifyAll    = params.getOne("notifyAll").toBoolean()
        def repeat       = params.getOne("repeat").toBoolean()
        
        renderXml() {
            out << CreateEscalationResponse() {
                out << getSuccessXML()
                out << getEscalationXML(escalationHelper
                                            .createEscalation(name, desc,
                                                              pauseAllowed,
                                                              maxWaitTime,
                                                              notifyAll,
                                                              repeat))
            }
        }
    }
}
