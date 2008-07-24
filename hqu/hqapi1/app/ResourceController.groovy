import org.hyperic.hq.hqu.rendit.BaseController

import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.hqapi1.ErrorCode;

class ResourceController extends ApiController {
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
}
