import org.hyperic.hq.hqapi1.ErrorCode
//import org.hyperic.hq.hqapi1.types.Application
import org.hyperic.hq.appdef.server.session.ApplicationManagerEJBImpl
import org.hyperic.hq.appdef.shared.ApplicationManagerLocal

class ApplicationController extends ApiController {

    ApplicationManagerLocal applicationManager = ApplicationManagerEJBImpl.getOne()

    private Closure getApplicationXML(a) {
        { doc ->
            Application(id    : a.id,
                  name        : a.name,
                  location    : a.location,
                  description : a.description,
                  engContact  : a.engContact,
                  opsContact  : a.opsContact,
                  bizContact  : a.businessContact)
              // TODO: include ApplicationServices and ApplicationGroups
           a.getAppServiceValues().each { asv ->
               print "Resource: " + asv
//                if (!asv.isCluster) {
//                    Resource(id : asv.service.id,
//                        name : asv.service.name,
//                        description : asv.service.description)
//                }
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
                    for (app in applicationManager.getAllApplications(user, null)) {
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
        def id = params.getOne('id')?.toInteger()

        if (id == null) {
            renderXml() {
                out << StatusResponse() {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                }
            }
            return
        }

        def app = getApplication(id)
        def failureXml = null

        if (!app) {
            renderXml() {
                out << StatusResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                }
            }
            return
        }

        try {
            removeApplication(id)
        } catch (Exception e) {
            renderXml() {
                log.error("Error removing application", e)
                StatusResponse() {
                    out << getFailureXML(ErrorCode.UNEXPECTED_ERROR)
                }
            }
            return
        }

        renderXml() {
            StatusResponse() {
                out << getSuccessXML()
            }
        }
    }

    private getApplication(id) {
        try {
            return applicationManager.findApplicationById(user, id)
        }
        catch (Exception e) {
            return null
        }
    }

    private removeApplication(id) {
        applicationManager.removeApplication(user, id)
    }
}
