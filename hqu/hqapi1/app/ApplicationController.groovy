import org.hyperic.hq.hqapi1.ErrorCode
//import org.hyperic.hq.hqapi1.types.Application
import org.hyperic.hq.appdef.server.session.ApplicationManagerEJBImpl
import org.hyperic.hq.appdef.shared.ApplicationManagerLocal

class ApplicationController extends ApiController {

    ApplicationManagerLocal applicationManager = null;

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
//            a.getAppServiceValues().each { asv ->
//                print "Resource: " + asv
//                if (!asv.isCluster) {
//                    Resource(id : asv.service.id,
//                        name : asv.service.name,
//                        description : asv.service.description)
//                }
//            }
        }
    }

    protected void init() {
        applicationManager = ApplicationManagerEJBImpl.getOne()
        // TODO: remove next debugging line
        print "Number of Applications: " + applicationManager.getApplicationCount()
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