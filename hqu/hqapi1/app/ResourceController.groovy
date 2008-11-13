import org.hyperic.hq.hqu.rendit.BaseController

import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.hqapi1.ErrorCode;

class ResourceController extends ApiController {

    private Closure getResourceXML(r) {
        { doc ->
            Resource(id : r.id,
                     name : r.name,
                     description : r.description)
        }
    }

    private Closure getPrototypeXML(p) {
        { doc -> 
            ResourcePrototype(id   : p.id,
                              name : p.name)
        }
    }

    def listResourcePrototypes(params) {
        def prototypes = resourceHelper.findAllAppdefPrototypes()
        
        renderXml() {
            out << ListResourcePrototypesResponse() {
                out << getSuccessXML()
                for (p in prototypes.sort {a, b -> a.name <=> b.name}) {
                    out << getPrototypeXML(p)
                }
            }
        }
    }

    def getResourcePrototype(params) {
        def name = params.getOne("name")

        def prototype = resourceHelper.find(prototype: name)

        renderXml() {
            out << GetResourcePrototypeResponse() {
                if (!prototype) {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                } else {
                    out << getSuccessXML()
                    out << getPrototypeXML(prototype)
                }
            }
        }
    }

    // TODO: Implement
    def syncPlatform(params) {
        renderXml() {
            out << SyncPlatformResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)
            }
        }
    }

    // TODO: Implement
    def syncServer(params) {
        renderXml() {
            out << CreateResourceResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)
            }
        }
    }

    // TODO: Implement
    def syncService(params) {
        renderXml() {
            out << CreateResourceResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)
            }
        }
    }

    def get(params) {
        def id = params.getOne("id")?.toInteger()

        def resource
        def failureXml
        if (!id) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            resource = resourceHelper.findById(id)

            if (!resource) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            } else {
                //XXX: Need to add get() to ResourceHelper. Catch lazy initialization
                //     errors.
                try {
                    resource.name
                } catch (Throwable t) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                }
            }
        }


        renderXml() {
            out << GetResourceResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    out << getResourceXML(resource)
                }
            }
        }
    }
}
