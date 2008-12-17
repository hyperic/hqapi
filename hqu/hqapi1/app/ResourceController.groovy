import org.hyperic.hq.hqapi1.ErrorCode;

class ResourceController extends ApiController {

    private Closure getPrototypeXML(p) {
        { doc -> 
            ResourcePrototype(id   : p.id,
                              name : p.name)
        }
    }

    def getResourcePrototypes(params) {
        def existing = params.getOne('existing')?.toBoolean()

        def prototypes
        if (existing) {
            prototypes = resourceHelper.findAppdefPrototypes()
        } else {
            prototypes = resourceHelper.findAllAppdefPrototypes()
        }
        
        renderXml() {
            out << ResourcePrototypesResponse() {
                out << getSuccessXML()
                for (p in prototypes.sort {a, b -> a.name <=> b.name}) {
                    out << getPrototypeXML(p)
                }
            }
        }
    }

    def getResourcePrototype(params) {
        def name = params.getOne("name")

        def prototype
        if (name) {
            prototype = resourceHelper.find(prototype: name)
        }

        renderXml() {
            out << ResourcePrototypeResponse() {
                if (!name) {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                         "Resource prototype not given")
                } else if (!prototype) {
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
            out << ResourceResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)
            }
        }
    }

    // TODO: Implement
    def createServer(params) {
        renderXml() {
            out << ResourceResponse() {
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
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                }
            }
            return
        }

        def parent = getResource(xmlParent[0].'@id'.toInteger())
        def prototype = resourceHelper.find(prototype: xmlServicePrototype[0].'@name')

        if (!parent || !prototype) {
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                }
            }
        }

        def serviceXml = xmlService[0]
        def cfgXml = serviceXml['ResourceConfig']
        def cfg = [:]
        cfgXml.each { c ->
            cfg.put(c.'@key', c.'@value')
        }

        def service
        try {
            service = prototype.createInstance(parent, serviceXml.'@name',
                                               user, cfg)
        } catch (Exception e) {
            // XXX: duplicate service not handled properly, but assume that's
            //      the case.
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_EXISTS);
                }
            }
            log.debug("Error creating service", e)
            return
        }
        
        renderXml() {
            ResourceResponse() {
                out << getSuccessXML()                
                out << getResourceXML(service)
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
            out << ResourceResponse() {
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
                def resource = getResource(childrenOfId)
                if (!resource) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                } else {
                    resources = resource.getViewableChildren(user)
                }
            } else {
                // Shouldn't happen
                failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
            }
        }

        renderXml() {
            out << ResourcesResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (resource in resources.sort {a, b -> a.name <=> b.name}) {
                        out << getResourceXML(resource)
                    }
                }
            }
        }
    }

    def delete(params) {
        def id = params.getOne("id")?.toInteger()
        def resource = getResource(id)

        if (!resource) {
            renderXml() {
                StatusResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                }
            }
            return
        }

        try {
            resource.remove(user)
        } catch (Exception e) {
            renderXml() {
                log.error("Error removing resource", e)
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
}
