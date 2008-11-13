import org.hyperic.hq.hqapi1.ErrorCode;

class ResourceController extends ApiController {

    private Closure getResourceXML(r) {
        { doc ->
            Resource(id : r.id,
                     name : r.name,
                     description : r.description) {
                r.getConfig().each { k, v ->
                    if (v.type.equals("configResponse")) {
                        ResourceConfig(key: k, value: v.value)
                    }
                }                
            }
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
                //XXX: ResourceHelper needs some work here..
                try {
                    resource.name // Check the object really exists
                    resource.entityId // Check the object is an appdef object
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

    def find(params) {
        def agentId = params.getOne("agentId")?.toInteger()

        def resources = []
        def failureXml
        
        if (!agentId) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            def agent = agentHelper.getAgent(agentId)
            if (!agent) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            } else {
                def platforms = agent.platforms
                resources = platforms*.resource
            }
        }

        renderXml() {
            out << FindResourcesResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (resource in resources) {
                        out << getResourceXML(resource)
                    }
                }
            }
        }
    }
}
