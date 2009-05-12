import org.hyperic.hq.hqapi1.ErrorCode;
import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.common.VetoException;

class ResourceController extends ApiController {

    private static final String PROP_FQDN        = "fqdn"
    private static final String PROP_INSTALLPATH = "installpath"
    private static final String PROP_AIIDENIFIER = "autoinventoryIdentifier"

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

                if (r.isPlatform()) {
                    def p = r.toPlatform()
                    def a = p.agent
                    Agent(id             : a.id,
                          address        : a.address,
                          port           : a.port,
                          version        : a.version,
                          unidirectional : a.unidirectional)
                    for (ip in p.ips) {
                        Ip(address : ip.address,
                           netmask : ip.netmask,
                           mac     : ip.macAddress)
                    }

                    ResourceInfo(key: PROP_FQDN, value: p.fqdn)
                } else if (r.isServer()) {
                    def s = r.toServer()
                    ResourceInfo(key: PROP_INSTALLPATH, value: s.installPath)
                    ResourceInfo(key: PROP_AIIDENIFIER, value: s.autoinventoryIdentifier)
                } else if (r.isService()) {
                    // Nothing yet.
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
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                         "Unable to find type " + name)
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

        if (!parent) {
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                         "Parent resource not found")
                }
            }
            return
        }

        if (!prototype) {
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                         "Resource type " +
                                         xmlPrototype[0].'@name' +
                                         " not found")
                }
            }
            return
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
            log.warn("Error creating resource", e)
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

        if (!parent) {
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                         "Parent resource " +
                                         xmlParent[0].'@id' +
                                         " not found")
                }
            }
            return
        }

        if (!prototype) {
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                         "Resource type " +
                                         xmlPrototype[0].'@name' +
                                         " not found")
                }
            }
            return
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
            log.warn("Error creating resource", e)
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
                if (!resource) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Resource id=" + id +
                                               " not found")
                }
            } else if (platformName) {
                resource = resourceHelper.find('platform':platformName)
                if (!resource) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Platform '" + platformName +
                                               "' not found")
                }
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
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Agent id=" + agentId +
                                               " not found")
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

    private syncResource(xmlResource, parent) {

        def id   = xmlResource.'@id'?.toInteger()
        def name = xmlResource.'@name'
        def description = xmlResource.'@description'

        def config = [name: name,
                      description: description]
        xmlResource['ResourceConfig'].each {
            // Do not set configs for empty keys
            if (it.'@value' && it.'@value'.length() > 0) {
                config[it.'@key'] = it.'@value'
            }
        }
        
        // TODO: Support cprops?
        //xmlResource['ResourceProperty'].each {
        //    config[it.'@key'] = it.'@value'
        //}

        if (!name) {
            return getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                 "Resource name not given")
        }

        def resource
        if (id) {
            resource = getResource(id)
        }

        if (!resource) {
            if (parent) {
                // If parent is defined, look through existing children
                def matches = parent.getViewableChildren(user).grep { it.name == name }
                log.info "Found " + matches.size() + " matches for " + name
                if (matches.size() == 1) {
                    resource = matches[0]
                } else if (matches.size() > 1) {
                    return getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                         "Found multiple matches for resource " + name)
                }
            } else {
                // Assume platform
                resource = resourceHelper.find('platform':name)
            }
        }
        
        if (resource) {
            // Update
            if (!configsEqual(resource.getConfig(), config)) {
                resource.setConfig(config, user)
            }
        } else {
            // Create
            def xmlPrototype = xmlResource['ResourcePrototype']
            if (!xmlPrototype) {
                return getFailureXML(ErrorCode.INVALID_PARAMETERS ,
                                     "Resource prototype not given for " + name)
            }

            def prototype = resourceHelper.find(prototype: xmlPrototype.'@name')

            if (!prototype) {
                return getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                     "No ResourcePrototype found for " +
                                     name)
            }

            if (prototype.isPlatformPrototype()) {
                parent = resourceHelper.findRootResource()
                def xmlAgent = xmlResource['Agent']
                def agent = agentHelper.getAgent(xmlAgent[0].'@id'?.toInteger())
                if (!agent) {
                    return getFailureXML(ErrorCode.OBJECT_NOT_FOUND ,
                                         "Unable to find agent id=" + xmlAgent[0].'@id')
                }

                def fqdn = xmlResource['ResourceInfo'].find { it.'@key' == PROP_FQDN }
                if (!fqdn) {
                    return getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                         "No FQDN given for " + name)
                } else {
                    config.put(PROP_FQDN, fqdn.'@value')
                }

                def xmlIps = xmlResource['Ip']
                def ips = []

                xmlIps.each { ip ->
                   ips << [address: ip.'@address', netmask: ip.'@netmask', mac: ip.'@mac']
                }

                try {
                    resource = prototype.createInstance(parent, name,
                                                        user, config, agent, ips)
                } catch (Exception e) {
                    log.warn("Error creating resource", e)
                    return getFailureXML(ErrorCode.OBJECT_EXISTS);
                }

            } else if (prototype.isServerPrototype()) {
                
                try {
                    def aiid = xmlResource['ResourceInfo'].find {
                        it.'@key' == PROP_AIIDENIFIER
                    }
                    if (aiid) {
                        config.put(PROP_AIIDENIFIER, aiid.'@value')
                    }

                    def installpath = xmlResource['ResourceInfo'].find {
                        it.'@key' == PROP_INSTALLPATH
                    }
                    if (installpath) {
                        config.put(PROP_INSTALLPATH, aiid.'@value')
                    }

                    resource = prototype.createInstance(parent, name,
                                                        user, config)
                } catch (Exception e) {
                    log.warn("Error creating resource", e)
                    return getFailureXML(ErrorCode.OBJECT_EXISTS);
                }
            } else if (prototype.isServicePrototype()) {

                try {
                    resource = prototype.createInstance(parent, name,
                                                        user, config)
                } catch (Exception e) {
                    log.warn("Error creating resource", e)
                    return getFailureXML(ErrorCode.OBJECT_EXISTS);
                }            
            } else {
                return getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                     "Invalid prototype=" + prototype.name)
            }
        }

        def xmlChildren = xmlResource['Resource']
        for (xmlChild in xmlChildren) {
            def res = syncResource(xmlChild, resource)
            if (res != null) {
                return res  // Exit early on errors.
            }
        }

        return null
    }

    def sync(params) {

        def failureXml = null
        def syncRequest = new XmlParser().parseText(getUpload('postdata'))

        for (xmlResource in syncRequest['Resource']) {
            failureXml = syncResource(xmlResource, null)
            if (failureXml != null) {
                break;
            }
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
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                         "Resource id=" + id + " not found")
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

    def move(params) {
        def targetId = params.getOne("targetId")?.toInteger()
        def destinationId = params.getOne("destinationId")?.toInteger()

        def failureXml = null
        def target = getResource(targetId)
        if (!target) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                       "Unable to find target resource with " +
                                       "id =" + targetId)
        }

        def destination = getResource(destinationId)
        if (!destination) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                       "Unable to find destination resource with " +
                                       "id =" + destinationId)
        }

        if (failureXml) {
            renderXml() {
                StatusResponse() {
                    out << failureXml
                }
            }
            return
        }

        try {
            target.moveTo(user, destination)
        } catch (VetoException e) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       e.getMessage())
        } catch (PermissionException e) {
            failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
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
}
