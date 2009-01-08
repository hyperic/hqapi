import org.hyperic.hq.hqapi1.ErrorCode;

class ResourceController extends ApiController {

    private Closure getResourceXML(user, r, boolean verbose, boolean children) {
        { doc ->
            Resource(id : r.id,
                     name : r.name,
                     description : r.description) {
                if (verbose) {
                    def config = r.getConfig()
                    config.each { k, v ->
                        if (v.type.equals("configResponse")) {
                            ResourceConfig(key: k, value: v.value)
                        }
                    }
                    config.each { k, v ->
                        if (v.type.equals("cprop")) {
                            ResourceProperty(key: k, value: v.value)
                        }
                    }
                }
                if (children) {
                    r.getViewableChildren(user).each { child ->
                        out << getResourceXML(user, child, verbose, children)
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

    def createPlatform(params) {
        def createRequest = new XmlParser().parseText(getUpload('postdata'))
        def xmlResource = createRequest['Resource']
        def xmlPrototype = createRequest['Prototype']
        def xmlIps = createRequest['Ip']
        def xmlAgent = createRequest['Agent']
        def fqdn = createRequest['Fqdn']?.text();

        if (!xmlResource || xmlResource.size() != 1 ||
            !xmlPrototype || xmlPrototype.size() != 1 ||
            !xmlIps || xmlIps.size() < 1 ||
            !xmlAgent || xmlAgent.size() != 1 ||
            !fqdn)
        {
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                }
            }
            return
        }

        def parent = resourceHelper.findRootResource()
        def agent = agentHelper.getAgent(xmlAgent[0].'@id'?.toInteger())
        def prototype = resourceHelper.find(prototype: xmlPrototype[0].'@name')

        if (!parent || !prototype) {
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                }
            }
        }

        def resourceXml = xmlResource[0]
        def cfgXml = resourceXml['ResourceConfig']
        def cfg = [:]
        cfgXml.each { c ->
            cfg.put(c.'@key', c.'@value')
        }
        cfg.put('fqdn', fqdn)

        def ips = []
        xmlIps.each { ip ->
            ips << [address: ip.'@address', netmask: ip.'@netmask', mac: ip.'@mac']
        }

        def resource
        try {
            resource = prototype.createInstance(parent, resourceXml.'@name',
                                                user, cfg, agent, ips)
        } catch (Exception e) {
            // TODO: Fix this
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_EXISTS);
                }
            }
            log.info("Error creating resource", e)
            return
        }

        renderXml() {
            ResourceResponse() {
                out << getSuccessXML()
                // Only return this resource w/ it's config
                out << getResourceXML(user, resource, true, false)
            }
        }
    }

    def createResource(params) {
        def createRequest = new XmlParser().parseText(getUpload('postdata'))
        def xmlParent = createRequest['Parent']
        def xmlResource = createRequest['Resource']
        def xmlPrototype = createRequest['Prototype']

        if (!xmlParent || xmlParent.size() != 1 ||
            !xmlResource || xmlResource.size() != 1 ||
            !xmlPrototype || xmlPrototype.size() != 1) {
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                }
            }
            return
        }

        def parent = getResource(xmlParent[0].'@id'.toInteger())
        def prototype = resourceHelper.find(prototype: xmlPrototype[0].'@name')

        if (!parent || !prototype) {
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                }
            }
        }

        def resourceXml = xmlResource[0]
        def cfgXml = resourceXml['ResourceConfig']
        def cfg = [:]
        cfgXml.each { c ->
            cfg.put(c.'@key', c.'@value')
        }

        def resource
        try {
            resource = prototype.createInstance(parent, resourceXml.'@name',
                                                user, cfg)
        } catch (Exception e) {
            // TODO: Fix this
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_EXISTS);
                }
            }
            log.debug("Error creating resource", e)
            return
        }
        
        renderXml() {
            ResourceResponse() {
                out << getSuccessXML()
                // Only return this resource w/ it's config
                out << getResourceXML(user, resource, true, false)
            }
        }
    }

    def get(params) {
        def id = params.getOne("id")?.toInteger()
        def platformName = params.getOne("platformName")
        def children = params.getOne("children")?.toBoolean()
        def verbose = params.getOne("verbose")?.toBoolean()

        def resource = null
        def failureXml
        if (!id && !platformName) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            if (id) {
                resource = getResource(id)
            } else if (platformName) {
                resource = resourceHelper.find('platform':platformName)
            }

            if (!resource) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            }
        }

        renderXml() {
            out << ResourceResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    out << getResourceXML(user, resource, verbose, children)
                }
            }
        }
    }

    def find(params) {
        def agentId = params.getOne("agentId")?.toInteger()
        def prototype = params.getOne("prototype")
        def children = params.getOne("children")?.toBoolean()
        def verbose = params.getOne("verbose")?.toBoolean()

        def resources = []
        def failureXml
        
        if (!agentId && !prototype ) {
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
                        out << getResourceXML(user, resource, verbose, children)
                    }
                }
            }
        }
    }

    // TODO: ResourceConfig does not properly handle unchanged configs.. 
    private configsEqual(existingConfig, newConfig) {
        def config = [:] + newConfig // Don't modify callers map
        existingConfig.each { k, v ->
            if (config.containsKey(k) && config[k] == v.value) {
                config.remove(k)
            }
        }
        return config.size() == 0;
    }

    private syncResource(xmlResource) {
        def id = xmlResource.'@id'?.toInteger()
        if (!id) {
            return getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
        } else {
            def resource = getResource(id)

            if (!resource) {
                return getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                     "Resource with id " + id + " not found")
            }
            
            def name        = xmlResource.'@name'
            def description = xmlResource.'@description'

            def config = [name: name,
                          description: description]
            
            xmlResource['ResourceConfig'].each {
                config[it.'@key'] = it.'@value'
            }

            xmlResource['ResourceProperty'].each {
                config[it.'@key'] = it.'@value'
            }

            if (!configsEqual(resource.getConfig(), config)) {
                resource.setConfig(config, user)
            }

            def xmlChildren = xmlResource['Resource']
            for (xmlChild in xmlChildren) {
                def res = syncResource(xmlChild)
                // Exit early on errors
                if (res != null) {
                    return res
                }
            }
        }

        return null
    }

    def sync(params) {

        def failureXml = null
        def syncRequest = new XmlParser().parseText(getUpload('postdata'))
        for (xmlResource in syncRequest['Resource']) {
            failureXml = syncResource(xmlResource)
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
