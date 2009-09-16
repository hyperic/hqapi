import org.hyperic.hq.hqapi1.ErrorCode
import org.hyperic.hq.appdef.server.session.ApplicationManagerEJBImpl as AppMan
import org.hyperic.hq.bizapp.server.session.AppdefBossEJBImpl as ABoss
import org.hyperic.util.pager.PageControl
import org.hyperic.hq.auth.shared.SessionManager
import org.hyperic.hq.appdef.shared.AppdefGroupValue
import org.hyperic.hq.appdef.shared.ServiceValue

class ApplicationController extends ApiController {

    def appMan = AppMan.one
    def aBoss = ABoss.one

    private Closure getApplicationXML(a) {
        { doc ->
            Application(id          : a.id,
                        name        : a.name,
                        location    : a.location,
                        description : a.description,
                        engContact  : a.engContact,
                        opsContact  : a.opsContact,
                        bizContact  : a.businessContact) {
                def sessionId = SessionManager.instance.put(user)
                for (appService in aBoss.findServiceInventoryByApplication(sessionId, a.id, PageControl.PAGE_ALL)) {
                    if (appService instanceof ServiceValue) {
                        def resource = resourceHelper.find('service':appService.id)
                        Resource(id :          resource.id,
                                 name :        resource.name,
                                 description : resource.description)
                    } else {
                        Group(id :          appService.id,
                              name :        appService.name,
                              description : appService.description,
                              location :    appService.location)
                    }
                }
            }
        }
    }

    def list(params) {
        def failureXml = null

        renderXml() {
            out << ApplicationsResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (app in appMan.getAllApplications(user, PageControl.PAGE_ALL)) {
                        out << getApplicationXML(app)
                    }
                }
            }
        }
    }

    def create(params) {
        def failureXml = null

        renderXml() {
            ApplicationResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def update(params) {
        def failureXml = null

        renderXml() {
            ApplicationResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def delete(params) {
        def failureXml = null

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