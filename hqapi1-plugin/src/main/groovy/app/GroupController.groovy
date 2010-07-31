import org.hyperic.hq.grouping.CritterList;
import org.hyperic.hq.grouping.CritterType;
import org.hyperic.hq.grouping.CritterRegistry;
import org.hyperic.hq.grouping.prop.CritterPropType 
import org.hyperic.hq.grouping.prop.EnumCritterProp 
import org.hyperic.hq.grouping.prop.EnumCritterPropDescription 
import org.hyperic.hq.grouping.prop.GroupCritterProp 
import org.hyperic.hq.grouping.prop.ProtoCritterProp 
import org.hyperic.hq.grouping.prop.ResourceCritterProp 
import org.hyperic.hq.grouping.prop.StringCritterProp 
import org.hyperic.hq.hqapi1.ErrorCode
import org.hyperic.hq.authz.shared.PermissionException;
import org.hyperic.hq.authz.shared.ResourceGroupManager 
import org.hyperic.hq.common.VetoException
import org.hyperic.hq.context.Bootstrap 
import org.hyperic.util.HypericEnum 

class GroupController extends ApiController {

    private Closure getGroupXML(g) {
        { doc ->
            Group(id          : g.id,
            	  resourceId  : g.resource.id,
                  name        : g.name,
                  description : g.description,
                  location    : g.location) {
                if (g.resourcePrototype) {
                    ResourcePrototype(id   : g.resourcePrototype.id,
                                      name : g.resourcePrototype.name)
                }
                for (r in g.resources) {
                    Resource(id : r.id,
                             name : r.name)
                }
                for (r in g.roles) {
                    Role(id : r.id,
                         name : r.name)
                }
            }
        }
    }

    def get(params) {
        def id = params.getOne('id')?.toInteger()
        def name = params.getOne('name')

        def group
        def failureXml = null
        if (id == null && name == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            group = getGroup(id, name)
            if (!group) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                           "Group with id=" + id + " name='" +
                                           name + "' not found")
            }
        }

        renderXml() {
            GroupResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    out << getGroupXML(group)
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

        def group = getGroup(id, null)
        def failureXml = null
        if (!group) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                       "Group with id " + id + " not found")
        } else {

            if (group.system) {
                failureXml = getFailureXML(ErrorCode.NOT_SUPPORTED,
                                           "Cannot delete system group " +
                                           group.name)
            } else {
                try {
                    group.remove(user)
                } catch (PermissionException e) {
                    failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
                } catch (Exception e) {
                    failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
                }
            }
        }

        renderXml() {
            out << StatusResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }
	
	private setCriteria(group,xmlIn) {
		def isAny = xmlIn[0].'@any'?.toBoolean();
 		def failureXml = null
 	 	def critters = parseCritters(xmlIn)
		CritterList clist = new CritterList(critters, isAny)
 	 	Bootstrap.getBean(ResourceGroupManager.class).setCriteria(user, group, clist)
	}
	
	 private CritterType findCritterType(String name) {
 		CritterRegistry.getRegistry().critterTypes.find { t ->
 	 	 	t.class.name == name
 		}
 	 }
	
	private List parseCritters(xmlIn) {
		xmlIn.'Criteria'.collect { critterDef ->
		CritterType critterType = findCritterType(critterDef.'@class')

		if (critterType == null) {
			throw new Exception("Unable to find critter class [${critterDef.'@class'}]")
		}

		def props = [:]
		for (propDef in critterDef.children()) {
			String propId   = propDef.'@name'
			String propType = propDef.'@type'
			//TODO maybe not so specific on prop type?
			if (propType == 'string') {
				props[propId] = new StringCritterProp(propId, propDef.'@value')
			} else if (propType == 'resource') {
				def rsrcId   = propDef.'@value'.toInteger()
				def resource = resourceHelper.findResource(rsrcId)
				props[propId] = new ResourceCritterProp(propId, resource)
			} else if (propType == 'group') {
				def group = resourceHelper.findGroupByName(propDef.'@value')
				props[propId] = new GroupCritterProp(propId, group)
			} else if (propType == 'proto') { 
				def proto  = resourceHelper.findResourcePrototype(propDef.'@value')
				props[propId] = new ProtoCritterProp(propId, proto)
			} else if (propType == 'enum') {
				def desc = critterType.propDescriptions.find { it.id == propId }
				if (!desc) {
					throw new Exception("Unknown prop [${propId}] for " + 
                                "critter [${critterDef.class}]")
				}
				if (desc.type != CritterPropType.ENUM) {
					throw new Exception("Property [${propId}] of critter ["+ 
                                "[${critterDef.class}] should be " + 
                                "of type <enum>")
				}
        
				EnumCritterPropDescription eDesc = desc
				def propDesc = propDef.'@value'
				HypericEnum match = eDesc.possibleValues.find { it.description == propDesc }
        		if (match == null) {
            		throw new Exception("[${propDesc}] is not a valid " + 
                                "value for prop [${propId}] for " +
                                "critter [${critterDef.@class}]")
        		}
        		props[propId] = new EnumCritterProp(propId, match)
    		} else {
    			throw new Exception("Unknown prop type [${propDef.@type}] for " + 
                            "critter [${critterDef.@class}]")
    		}
		}
		critterType.newInstance(props)
		}
    }


    def sync(params) {
        def syncRequest = new XmlParser().parseText(getPostData())

        def groups = []

        for (xmlGroup in syncRequest['Group']) {
            // Check for existance
            def existing = getGroup(xmlGroup.'@id'?.toInteger(),
                                    xmlGroup.'@name')

            def failureXml = null
            def roles = [] as Set
            def resources = []
            def prototype = null

            if (!xmlGroup.'@name') {
                failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                           "Group name required.")
            }

            if (existing?.isSystem()) {
                failureXml = getFailureXML(ErrorCode.NOT_SUPPORTED,
                                           "Cannot update system group " +
                                           "with id=" + existing.id)
            }

            // Look up prototype
            def xmlPrototype = xmlGroup.'ResourcePrototype'
            if (!xmlPrototype) {
                log.debug("No prototype found for " + xmlGroup.'@name')
            } else {
                prototype = resourceHelper.find(prototype:xmlPrototype.'@name')
                if (!prototype) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Unable to find prototype with " +
                                               "name " + xmlPrototype.'@name')
                } else {
                    if (existing) {
                        if (existing.resourcePrototype) {
                            // If already compatible - ensure the same type.
                            if (!existing.resourcePrototype.name.equals(prototype.name)) {
                                failureXml = getFailureXML(ErrorCode.NOT_SUPPORTED,
                                                           "Cannot change group type from " +
                                                           existing.resourcePrototype.name +
                                                           " to " + prototype.name)
                            }
                        }
                    }
                }
            }

            // Look up roles
            for (xmlRole in xmlGroup['Role']) {
                log.debug("Found role "+ xmlRole.'@name')

                def role = getRole(xmlRole.'@id'?.toInteger(),
                                   xmlRole.'@name')
                if (!role) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Unable to find role with id " +
                                               xmlRole.'@id' + " and name " +
                                               xmlRole.'@name')
                } else {
                    roles.add(role)
                }
            }

            // Look up resources
            for (xmlResource in xmlGroup['Resource']) {
                log.debug("Found resource " + xmlResource.'@name')

                def resource = getResource(xmlResource.'@id'?.toInteger());

                if (!resource) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Unable to find resource with id " +
                                               xmlResource.'@id')
                } else {
                    if (prototype) {
                        if (!resource.prototype.name.equals(prototype.name)) {
                            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                                       "Resource " + resource.name +
                                                       " is not of type " +
                                                       prototype.name)
                        }
                    }

                    // Avoid duplicate resources which causes constraint violations
                    // TODO: Backend should handle this case.
                    if (!resources.contains(resource)) {
                        resources.add(resource)
                    }
                }
            }

            if (!failureXml) {
                if (existing) {
                    // TODO: This needs to be moved out to the Manager.
                    try {
                    	existing.setResources(user, resources)
                    	existing.setRoles(roles)
                    	existing.updateGroup(user,
                                         	 xmlGroup.'@name',
                                         	 xmlGroup.'@description',
                                         	 xmlGroup.'@location')
						def criteriaList = xmlGroup.'CriteriaList'
						if(criteriaList) {
							try {
								setCriteria(existing,criteriaList)
							} catch(Exception e) {
								failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,e.getMessage())
								log.error("Unable to set group criteria", e)	
							} 
						}
						if(!failureXml) {
							groups << existing
						}
                    } catch (VetoException ve) {
                    	failureXml = getFailureXML(ErrorCode.OPERATION_DENIED,
                    							   ve.getMessage())
                    } 
                } else {
                    // TODO: private groups
					def criteriaList = xmlGroup.'CriteriaList'
					
					if(criteriaList) {
						def clist
						def isAny = criteriaList[0].'@any'?.toBoolean();
						try {
							def critters = parseCritters(criteriaList)
							clist = new CritterList(critters, isAny)
						}catch(Exception e) {
							failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,e.getMessage())
							log.error("Unable to set group criteria", e)	
						}
						if(!failureXml) {
							def group = resourceHelper.createGroup(xmlGroup.'@name',
                                                           xmlGroup.'@description',
                                                           xmlGroup.'@location',
                                                           prototype, roles,
                                                           resources,
                                                           false, clist)
                           groups << group
						}
					} else {
							def group = resourceHelper.createGroup(xmlGroup.'@name',
                                                           xmlGroup.'@description',
                                                           xmlGroup.'@location',
                                                           prototype, roles,
                                                           resources,
                                                           false)
                           groups << group
					}
					
                }
            }
			
			

            // If any group is unable to be synced exit with an error.
            if (failureXml) {
                renderXml() {
                    out << GroupsResponse() {
                        out << failureXml
                    }
                }
                return
            }
        }

        renderXml() {
            out << GroupsResponse() {
                out << getSuccessXML()
                for (g in  groups) {
                    out << getGroupXML(g)
                }
            }
        }
    }

    def list(params) {
        def compatible = params.getOne('compatible')?.toBoolean()
        def containing = params.getOne('containing')?.toBoolean()
        def roleId = params.getOne('roleId')?.toInteger()

		def groups = null
		def failureXml = null
		
        if (containing != null) {
        	def resourceId = params.getOne('resourceId')?.toInteger()
        	def resource = getResource(resourceId)
        	
            if (resource) {
        		if (containing) {
        			groups = resource.getGroupsContaining(user)
        		} else {
        			groups = resource.getGroupsNotContaining(user)
        		}            
            } else {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                           "Resource id=" + resourceId +
                                           " not found")
            }
        } else if (roleId != null) {
        	def role = getRole(roleId, null)
        	if (!role) {
            	failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                       	   "Role id=" + roleId + 
                                       	   " not found")
            } else {
       			groups = role.getGroups(user)        	
            }
        } else {
        	groups = resourceHelper.findViewableGroups()

        	if (compatible != null) {
            	if (compatible) {
                	groups = groups.grep { it.resourcePrototype != null }
            	} else {
                	groups = groups.grep { it.resourcePrototype == null }
            	}
            }        
        }

        renderXml() {
            out << GroupsResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                	out << getSuccessXML()
                	for (g in  groups.sort {a, b -> a.name <=> b.name}) {
                    	out << getGroupXML(g)
                	}
                }
            }
        }
    }
} 
