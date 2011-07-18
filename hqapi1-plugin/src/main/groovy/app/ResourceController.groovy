import org.hyperic.hq.hqapi1.ErrorCode
import org.hyperic.hq.appdef.shared.AppdefEntityID
import org.hyperic.hq.appdef.shared.AppdefUtil
import org.hyperic.hq.authz.shared.AuthzConstants
import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.authz.shared.ResourceEdgeCreateException

import org.hyperic.hq.appdef.shared.ServiceManager
import org.hyperic.hq.appdef.shared.ServerManager
import org.hyperic.hq.appdef.shared.PlatformManager
import org.hyperic.hq.authz.shared.ResourceManager

import org.hyperic.hq.authz.server.session.Resource
import org.hyperic.hq.context.Bootstrap
import org.hyperic.hq.common.VetoException

class ResourceController extends ApiController {

    private static final String PROP_FQDN        = "fqdn"
    private static final String PROP_INSTALLPATH = "installPath"
    private static final String PROP_AIIDENIFIER = "autoIdentifier"
    private static final String PROP_AGENT_ID    = "agentId"

    private static platMan = Bootstrap.getBean(PlatformManager.class)
    private static svrMan = Bootstrap.getBean(ServerManager.class)
    private static svcMan = Bootstrap.getBean(ServiceManager.class)
    private static resMan = Bootstrap.getBean(ResourceManager.class)
    
    private toPlatform(Resource r) {
        platMan.findPlatformById(r.instanceId)
    }

    private toServer(Resource r) {
        svrMan.findServerById(r.instanceId)
    }

    private toService(Resource r) {
        svcMan.findServiceById(r.instanceId)
    }

    private Closure getResourceXML(user, r, boolean verbose, boolean children) {
        { doc ->
            def appdefRes = null
            def isPlatform = r.isPlatform()
            if (isPlatform) {
                appdefRes = toPlatform(r)
            }
            def isServer = r.isServer()
            if (isServer) {
                appdefRes = toServer(r)
            }
            def isService = r.isService()
            if (isService) {
                appdefRes = toService(r)
            }

            Resource(id : r.id,
                     name : r.name,
                     description : appdefRes.description,
                     location : appdefRes.location,
                     instanceId : r.entityId.id,
                     typeId : r.entityId.type) {
                if (verbose) {
                    try {
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
                    } catch (Throwable t) {
                        // Invalid confi?. Bad DB entry?
                        log.error("Exception thrown while retrieving config for Resource ID " + r.id + " probably needs to be deleted manually")
                    }
                }
                if (children && !isService) {
                    r.getViewableChildren(user).each { child ->
                        out << getResourceXML(user, child, verbose, children)
                    }
                }
                try { 
                    ResourcePrototype(instanceId: r.prototype.instanceId,
                                      resourceTypeId: r.prototype.resourceType.id - 600,
                                      id : r.prototype.id,
                                      name : r.prototype.name)

                    if (isPlatform) {
                        def p = appdefRes
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
                    } else if (isServer) {
                        def s = appdefRes
                        ResourceInfo(key: PROP_INSTALLPATH, value: s.installPath)
                        ResourceInfo(key: PROP_AIIDENIFIER, value: s.autoinventoryIdentifier)
                    } else if (isService) {
                        def s = appdefRes
                        ResourceInfo(key: PROP_AIIDENIFIER, value: s.autoinventoryIdentifier)
                    }
                } catch (Throwable t) {
                    // Invalid confi?. Bad DB entry?
                    log.error("Exception thrown while retrieving info for Resource ID " + r.id + " probably needs to be deleted manually")
                }

            }
        }
    }

    private Closure getPrototypeXML(p) {
        { doc -> 
            ResourcePrototype(instanceId: p.instanceId,
                              resourceTypeId: p.resourceType.id - 600,
                              id   : p.id,
                              name : p.name)
        }
    }

    private Closure getResourceEdgeXML(edges) {
    	{ doc ->
    		for (e in edges.sort {a, b -> a.to.name <=> b.to.name}) {
    			ResourceEdge(relation : e.relation.name,
    					 	 distance : e.distance) {
    				ResourceFrom() {
    					Resource(id : e.from.id,
    						 	 name : e.from.name)
    				}
    				ResourceTo() {
                        Resource(id : e.to.id,
                        		 name : e.to.name)
                    }    			
    			}
    		}                    
    	}
    }

    private Closure getResourceEdgeGroupingXML(resourceRelation, parent, childrenEdges) {
    	{ doc ->
    		ResourceEdge(relation : resourceRelation,
    					 distance : 1) {
    			ResourceFrom() {
    				Resource(id : parent.id,
    						 name : parent.name)
    			}
    			ResourceTo() {
                    for (e in childrenEdges.sort {a, b -> a.to.name <=> b.to.name}) {
                        if (e.distance == 1) {
                        	Resource(id : e.to.id,
                        		 	 name : e.to.name)
                        }
                    }    			
    			}
    		}                     
    	}
    }

    private String getCause(Throwable t) {
        while (t.getCause()) {
            t = t.getCause()
        }
        return t.getMessage()
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
        def createRequest = new XmlParser().parseText(getPostData())
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
        def agent = getAgent(xmlAgent[0].'@id'?.toInteger(),
                             xmlAgent[0].'@address',
                             xmlAgent[0].'@port'?.toInteger())
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

        if (!agent) {
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                         "Agent with id=" + xmlAgent[0].'@id' +
                                         " address=" + xmlAgent[0].'@address' +
                                         " port=" + xmlAgent[0].'@port' +
                                         " not found")
                }
            }
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
        } catch (Throwable t) {
            String cause = getCause(t)
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                         "Error creating '" +
                                         resourceXml.'@name' + "': " + cause)
                }
            }
            log.warn("Error creating resource", t)
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
        def createRequest = new XmlParser().parseText(getPostData())
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
        } catch (Throwable t) {
            String cause = getCause(t)
            renderXml() {
                ResourceResponse() {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                         "Error creating '" +
                                         resourceXml.'@name' + "': " +
                                         cause);
                }
            }
            log.warn("Error creating resource", t)
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
        def fqdn = params.getOne("fqdn")
        def parentOf = params.getOne("parentOf")?.toInteger()
        def platformId = params.getOne("platformId")?.toInteger()
        def aeid = params.getOne("aeid")?.toString()
        boolean children = params.getOne("children", "false").toBoolean()
        boolean verbose = params.getOne("verbose", "false").toBoolean()

        def resource = null
        def failureXml
        if (!id && !platformName && !fqdn && !platformId && !parentOf && !aeid) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            try {
                if (id) {
                    resource = getResource(id)
                    if (!resource) {
                        failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                "Resource id=" + id +
                                        " not found")
                    }
                } else if (aeid) {
                    def appdefeid = new AppdefEntityID(aeid)
                    resource = resMan.findResource(appdefeid)
                    
                    if (!resource) {
                        failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                "Resource aeid=" + aeid +
                                        " not found")
                    }
                } else if (platformId) {
                    def wantedResource = getResource(platformId)
                    if (!wantedResource) {
                        failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                "Resource id=" + platformId +
                                        " not found")
                    } else {
                        resource = wantedResource.platform
                    }
                } else if (platformName) {
                    resource = resourceHelper.find('platform': platformName)
                    if (!resource) {
                        failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                "Platform '" + platformName +
                                        "' not found")
                    }
                } else if (fqdn) {
                	resource = resourceHelper.find('byFqdn':fqdn)
                    if (!resource) {
                        failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Platform fqdn='" + fqdn +
                                               "' not found")
                    }
                } else if (parentOf) {
                    def child = getResource(parentOf)
                    if (!child) {
                        failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Resource id ='" + parentOf +
                                               "' not found")
                    } else {
                        if (child.isPlatform()) {
                            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                                       "No parent for platform ='" + parentOf + "'")
                        } else if (child.isServer()) {
                            resource = child.toServer().platform.resource
                        } else if (child.isService()) {
                            resource = child.toService().server.resource
                        }
                    }                }
            } catch (PermissionException e) {
                failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
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
        def description = params.getOne("description")
        def children = params.getOne("children", "false").toBoolean()
        def verbose = params.getOne("verbose", "false").toBoolean()

        def resources = []
        def failureXml
        
        if (!agentId && !prototype && !description) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            if (agentId) {
                def agent = getAgent(agentId, null, null)
                if (!agent) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Agent id=" + agentId +
                                               " not found")
                } else {
                    def platforms = agent.platforms
                    for (platform in platforms) {
                        try {
                            resources.add(platform.checkPerms(operation: 'view', user:user))
                        } catch (PermissionException e) {
                            log.debug("Ignoring platform " + platform.name + " due to permissions.")
                        }
                    }
                }
            } else if (prototype) {
                def matching = resourceHelper.find('byPrototype': prototype)

                for (resource in matching) {
                    try {
                        resources.add(checkViewPermission(resource))
                    } catch (PermissionException e) {
                        log.debug("Ignoring resource " + resource.name + " due to permissions")
                    }
                }
            } else if (description) {
                // TODO: Move into HQ.
                def matching = []
                def session =  org.hyperic.hq.hibernate.SessionManager.currentSession()
                matching.addAll(session.createQuery(
                    "select p.resource from Platform p where p.description like '%${description}%'").list())
                matching.addAll(session.createQuery(
                    "select s.resource from Server s where s.description like '%${description}%'").list())
                matching.addAll(session.createQuery(
                    "select s.resource from Service s where s.description like '%${description}%'").list())

                for (resource in matching) {
                    try {
                        resources.add(checkViewPermission(resource))
                    } catch (PermissionException e) {
                        log.debug("Ignoring resource " + resource.name + " due to permissions")
                    }
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
                        out << getResourceXML(user, resource, verbose, children)
                    }
                }
            }
        }
    }

    def getParentResourcesByRelation(params) {
    	def name = params.getOne("name")
    	def prototype = params.getOne("prototype")
    	def resourceRelation = params.getOne("resourceRelation")
    	def hasChildren = params.getOne("hasChildren").toBoolean()
    	
    	def platforms = null
    	def failureXml
    	
    	if (!resourceRelation 
    			|| !resourceRelation.equals(AuthzConstants.ResourceEdgeNetworkRelation)) {
    		failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)    		
    	} else {
    		platforms = resourceHelper.findParentPlatformsByNetworkRelation(prototype, name, Boolean.valueOf(hasChildren))
    	}
    	
    	renderXml() {
    		ResourcesResponse() {
    			if (failureXml) {
                    out << failureXml
                } else {    			
    				out << getSuccessXML()
    				for (platform in platforms.sort {a, b -> a.name <=> b.name}) {
                		out << getResourceXML(user, platform.resource, Boolean.FALSE, Boolean.FALSE)
                	}
                }
    		}
    	}
    }
    
    def getResourcesByNoRelation(params) {
    	def name = params.getOne("name")
    	def prototype = params.getOne("prototype")
    	def resourceRelation = params.getOne("resourceRelation")
    	
    	def platforms = null
    	def failureXml
    	
    	if (!resourceRelation 
    			|| !resourceRelation.equals(AuthzConstants.ResourceEdgeNetworkRelation)) {
    		failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)    		
    	} else {
    		platforms = resourceHelper.findPlatformsByNoNetworkRelation(prototype, name)
    	}
    	
    	renderXml() {
    		ResourcesResponse() {
    			if (failureXml) {
                    out << failureXml
                } else {    			
    				out << getSuccessXML()
    				for (platform in platforms.sort {a, b -> a.name <=> b.name}) {
                		out << getResourceXML(user, platform.resource, Boolean.FALSE, Boolean.FALSE)
                	}
                }
    		}
    	}
    }
    
    def getResourceEdges(params) {
    	def resourceRelation = params.getOne("resourceRelation")
    	def id = params.getOne("id")?.toInteger()
    	def prototype = params.getOne("prototype")
    	def name = params.getOne("name")
    	
    	def parent = null
    	def edges = []
    	def failureXml
    	
    	if (!resourceRelation)  {
    		failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
    	} else {
    		if (id) {
    			parent = getResource(id)
    			if (!parent) {
    				failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
    										   "Resource id=" + id +
    										   " not found")
    			} else {
    				edges = resourceHelper.findResourceEdges(resourceRelation, parent)
    			}
    		} else {
    			edges = resourceHelper.findResourceEdges(resourceRelation, prototype, name)
    		}
    	}

        renderXml() {
            out << ResourceEdgesResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    if (edges.size() > 0) {
                    	if (parent) {
                    		out << getResourceEdgeGroupingXML(resourceRelation, 
                    						  	  		  	  parent, 
                    						  	  		  	  edges)
                    	} else {
                    		out << getResourceEdgeXML(edges)
                    	}
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
        def location = xmlResource.'@location'

        def config = [name: name,
                      description: description,
                      location: location]
        
        xmlResource['ResourceConfig'].each {
            // Do not set configs for empty keys
            if (it.'@value' && it.'@value'.length() > 0) {
                config[it.'@key'] = it.'@value'
            }
        }
        
        xmlResource['ResourceProperty'].each {
            config[it.'@key'] = it.'@value'
        }

        if (!name) {
            return getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                 "Resource name not given")
        }

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
        
        def resource = null
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
                def fqdn = xmlResource['ResourceInfo'].find { it.'@key' == PROP_FQDN }
                if (fqdn) {
                	resource = resourceHelper.find('byFqdn':fqdn.'@value')
                } else {
                	resource = resourceHelper.find('platform':name)
                }
            }
        }

        if (resource) {
            // Add special configurations from ResourceInfo
            if (prototype.isPlatformPrototype()) {
                def fqdn = xmlResource['ResourceInfo'].find { it.'@key' == PROP_FQDN }
                if (!fqdn) {
                    return getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                         "No FQDN given for " + name)
                } else {
                    config.put(PROP_FQDN, fqdn.'@value')
                }
                
                // Add agent info
        		def xmlAgent = xmlResource['Agent']        		
        		if (xmlAgent) {
        			def agentId = xmlAgent[0].'@id'?.toInteger()
                	def agent = getAgent(agentId, null, null)
                	if (!agent) {
                    	return getFailureXML(ErrorCode.OBJECT_NOT_FOUND ,
                                         "Unable to find agent id=" + agentId)
                	} else {
                		config.put(PROP_AGENT_ID, agentId)
                	}
                }
            } else if (prototype.isServerPrototype()) {
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
                    config.put(PROP_INSTALLPATH, installpath.'@value')
                }
            } else if (prototype.isServicePrototype()) {
                def aiid = xmlResource['ResourceInfo'].find {
                    it.'@key' == PROP_AIIDENIFIER
                }
                if (aiid) {
                    config.put(PROP_AIIDENIFIER, aiid.'@value')
                }
            }

            def existingConfig = resource.getConfig()
            // 2nd pass over configuration to unset any variables that
            // may already exist in the existing config, but are empty in
            // the configuration being set.
            xmlResource['ResourceConfig'].each {
                if ((it.'@value' && it.'@value'.length() > 0) ||
                    (it.'@value' != null && !it.'@value'.equals(existingConfig[it.'@key']?.value))) {
                    config[it.'@key'] = it.'@value'
                }
            }

            // Update
            if (!configsEqual(existingConfig, config)) {
                try {
                    resource.setConfig(config, user)
                } catch (Throwable t) {
                    String cause = getCause(t)
                    return getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                         "Error updating '" + name + "': " +
                                         cause)
                }
            }
        } else {
            // Create
            if (prototype.isPlatformPrototype()) {
                parent = resourceHelper.findRootResource()
                def xmlAgent = xmlResource['Agent']
                def agent = getAgent(xmlAgent[0].'@id'?.toInteger(),
                                     xmlAgent[0].'@address',
                                     xmlAgent[0].'@port'?.toInteger())
                if (!agent) {
                    return getFailureXML(ErrorCode.OBJECT_NOT_FOUND ,
                                         "Unable to find agent id=" + xmlAgent[0].'@id' +
                                         " address=" + xmlAgent[0].'@address' +
                                         " port=" + xmlAgent[0].'@port')
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
                } catch (Throwable t) {
                    String cause = getCause(t)
                    log.warn("Error creating resource", t)
                    return getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                         "Error creating '" + name + "':" +
                                         cause);
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
                        config.put(PROP_INSTALLPATH, installpath.'@value')
                    }

                    resource = prototype.createInstance(parent, name,
                                                        user, config)
                } catch (Throwable t) {
                    String cause = getCause(t)
                    log.warn("Error creating resource", t)
                    return getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                         "Error creating '" + name + "':" +
                                         cause);
                }
            } else if (prototype.isServicePrototype()) {

                try {
                    def aiid = xmlResource['ResourceInfo'].find {
                        it.'@key' == PROP_AIIDENIFIER
                    }
                    if (aiid) {
                        config.put(PROP_AIIDENIFIER, aiid.'@value')
                    }
                    resource = prototype.createInstance(parent, name,
                                                        user, config)  
                } catch (Throwable t) {
                    String cause = getCause(t)
                    log.warn("Error creating resource", t)
                    return getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                         "Error creating '" + name + "':" +
                                         cause);
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
        def syncRequest = new XmlParser().parseText(getPostData())

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

    def syncResourceEdges(params) {

        def failureXml = null
        def syncRequest = new XmlParser().parseText(getPostData())

        for (xmlResourceEdge in syncRequest['ResourceEdge']) {
            failureXml = updateResourceEdges("sync", xmlResourceEdge)
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
        
    def createResourceEdges(params) {

        def failureXml = null
        def syncRequest = new XmlParser().parseText(getPostData())

        for (xmlResourceEdge in syncRequest['ResourceEdge']) {
            failureXml = updateResourceEdges("add", xmlResourceEdge)
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
    
    def deleteResourceEdges(params) {

        def failureXml = null
        def syncRequest = new XmlParser().parseText(getPostData())

        for (xmlResourceEdge in syncRequest['ResourceEdge']) {
            failureXml = updateResourceEdges("remove", xmlResourceEdge)
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

    def deleteAllResourceEdges(params) {
    	def resourceRelation = params.getOne("resourceRelation")
        def id = params.getOne("id")?.toInteger()
        def resource = null
        def failureXml = null
        
    	if (!resourceRelation 
    			|| !resourceRelation.equals(AuthzConstants.ResourceEdgeNetworkRelation)) {
    		failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)    		
    	} else {
    		if (id) {
    			resource = getResource(id)
    			if (!resource) {
    				failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
    										   "Resource id=" + id +
    										   " not found")
    			} else {
    				try {
    					resourceHelper.removeResourceEdges(resourceRelation, resource)
    				} catch (Exception e) {
    					log.error("Error removing resource edges", e)
    					failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
    				}
    			}
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
        
    private updateResourceEdges(syncType, xmlResourceEdge) {
    	def relation = xmlResourceEdge.'@relation'                          
        def xmlResourceFrom = xmlResourceEdge['ResourceFrom']
        def xmlResourceTo = xmlResourceEdge['ResourceTo']
        def xmlResource = null
        def resourceFrom = null
        def resourceTo = null
        def parent = null
        def children = []

        xmlResource = xmlResourceFrom['Resource'].get(0)
        resourceFrom = getResource(xmlResource.'@id'?.toInteger())
        
        if (resourceFrom) {
        	parent = AppdefUtil.newAppdefEntityId(resourceFrom)
        } else {
           	return getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                           "Unable to find resource with id = " +
                                           xmlResource.'@id')
        }
        
		xmlResourceTo['Resource'].each {
            resourceTo = getResource(it.'@id'?.toInteger())
            if (resourceTo) {
            	children << AppdefUtil.newAppdefEntityId(resourceTo)
            } else {
            	return getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                           "Unable to find resource with id = " +
                                           it.'@id')
        	}
        }
                
        try {
    		if (syncType.equals("add")) {
    			resourceHelper.createResourceEdges(relation, 
    										   	   parent, 
    										   	   (AppdefEntityID[]) children.toArray(),
    										   	   false)
    		} else if (syncType.equals("remove")) {
    			resourceHelper.removeResourceEdges(relation, 
    										   	   parent, 
    										   	   (AppdefEntityID[]) children.toArray())    		
    		} else if (syncType.equals("sync")) {
    			resourceHelper.createResourceEdges(relation, 
    										   	   parent, 
    										   	   (AppdefEntityID[]) children.toArray(),
    										   	   true)
    		}
    	} catch (PermissionException p) {
            return getFailureXML(ErrorCode.PERMISSION_DENIED)
    	} catch (ResourceEdgeCreateException r)  {
    		return getFailureXML(ErrorCode.INVALID_PARAMETERS, r.getMessage())
    	} catch (IllegalArgumentException i)  {
    		return getFailureXML(ErrorCode.INVALID_PARAMETERS, i.getMessage())
    	} catch (Exception e) {
    		return getFailureXML(ErrorCode.UNEXPECTED_ERROR, e.getMessage())
    	}
    	return null
	}

    // Walk the Resource tree ensuring all resources exist.
    private ensureResourcesExist(xmlResource) {
        def resource = getResource(xmlResource.'@id'?.toInteger())
        if (!resource) {
            return getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                           "Unable to find resource with id = " +
                                           xmlResource.'@id')
        }

        for (xmlChild in xmlResource['Resource']) {
            def result = ensureResourcesExist(xmlChild)
            if (result != null) {
                return result
            }
        }

        return null
    }

    // Uses sync(), but does extra checks to ensure the resource exists.
    def update(params) {
        def updateRequest = new XmlParser().parseText(getPostData())
        def xmlResources = updateRequest['Resource']
        def failureXml = null

        if (!xmlResources) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Resource not given to update")
        } else if (xmlResources.size() != 1) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Single resource not passed to update")
        } else {
            def xmlResource = xmlResources[0]

            failureXml = ensureResourcesExist(xmlResource)

            if (!failureXml) {
                failureXml = syncResource(xmlResource, null)
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
            log.error("Error removing resource", e)
            renderXml() {
                StatusResponse() {
                    out << getFailureXML(ErrorCode.UNEXPECTED_ERROR, 
                    					 "Error removing resource: " + e.getMessage())
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
    
    def getPlatformResources(params) {
        boolean children = params.getOne("children", "false").toBoolean()
        boolean verbose = params.getOne("verbose", "false").toBoolean()
        def resources = resourceHelper.findAllPlatforms()
    
        renderXml() {
            out << ResourcesResponse() {
                out << getSuccessXML()
                for (r in resources.sort {a, b -> a.name <=> b.name}) {
                    out << getResourceXML(user, r, verbose, children)
                }
            }
        }

    }
}
