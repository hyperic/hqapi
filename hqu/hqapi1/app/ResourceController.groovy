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
                ResourcePrototype(id : r.prototype.id,
                                  name : r.prototype.name)
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
    def createPlatform(params) {
        renderXml() {
            out << CreateResourceResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)
            }
        }
    }

    // TODO: Implement
    def createServer(params) {
        renderXml() {
            out << CreateResourceResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)
            }
        }
    }

    def createService(params) {
        def createRequest = new XmlParser().parseText(getUpload('postdata'))
        def xmlParent = createRequest['Parent']
        def xmlService = createRequest['Service']
        def xmlServicePrototype = createRequest['ServicePrototype']

        if (!xmlParent || xmlParent.size() != 1 ||
            !xmlService || xmlService.size() != 1 ||
            !xmlServicePrototype || xmlServicePrototype.size() != 1) {
            renderXml() {
                CreateRoleResponse() {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                }
            }
            return
        }

        def parent = xmlParent[0]
        def service = xmlService[0]
        def servicePrototype = xmlServicePrototype[0]

        log.info("Found parent=" + parent.'@name' + " service=" +
                 service.'@name' + " type=" + servicePrototype.'@name')

        renderXml() {
            out << CreateResourceResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)
            }
        }
    }

    def get(params) {
        def id = params.getOne("id")?.toInteger()
        def platformId = params.getOne("platformId")?.toInteger()
        def platformName = params.getOne("platformName")
        def serverId = params.getOne("serverId")?.toInteger()
        def serviceId = params.getOne("serviceId")?.toInteger()

        def resource = null
        def failureXml
        if (!id && !platformId && !platformName && !serverId && !serviceId) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            if (id) {
                resource = resourceHelper.findById(id)
            } else if (platformId) {
                resource = resourceHelper.find('platform':platformId)
            } else if (platformName) {
                resource = resourceHelper.find('platform':platformName)
            } else if (serverId) {
                resource = resourceHelper.find('server':serverId)
            } else if (serviceId) {
                resource = resourceHelper.find('service':serviceId)
            }

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
        def prototype = params.getOne("prototype")
        def childrenOfId = params.getOne("childrenOfId")?.toInteger()

        def resources = []
        def failureXml
        
        if (!agentId && !prototype && !childrenOfId) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            if (agentId) {
                def agent = agentHelper.getAgent(agentId)
                if (!agent) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                } else {
                    def platforms = agent.platforms
                    resources = platforms*.resource
                }
            } else if (prototype) {
                resources = resourceHelper.find('byPrototype': prototype)
            } else if (childrenOfId) {
                def resource = resourceHelper.findById(childrenOfId)

                if (!resource) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                } else {
                    //XXX: ResourceHelper needs some work here..
                    try {
                        resource.name // Check the object really exists
                        resource.entityId // Check the object is an appdef object
                        resources = resource.getViewableChildren(user)
                    } catch (Throwable t) {
                        failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                    }
                }
            } else {
                // Shouldn't happen
                failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
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
