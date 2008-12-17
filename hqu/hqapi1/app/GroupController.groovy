import org.hyperic.hq.hqapi1.ErrorCode

class GroupController extends ApiController {

    private Closure getGroupXML(g) {
        { doc ->
            Group(id          : g.id,
                  name        : g.name,
                  description : g.description,
                  location    : g.location) {
                if (g.resourcePrototype) {
                    ResourcePrototype(id   : g.resourcePrototype.id,
                                      name : g.resourcePrototype.name)
                }
                // TODO: Roles, Criteria
            }
        }
    }

    private getGroup(Integer id, String name) {
        if (id) {
            return resourceHelper.findGroup(id)
        } else {
            return resourceHelper.findGroupByName(name)
        }
    }

    def get(params) {
        def id = params.getOne('id')?.toInteger()
        def name = params.getOne('name')

        def group
        def failureXml = null
        if (!id && !name) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            group = getGroup(id, name)
            if (!group) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
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

    def create(params) {
        renderXml() {
            GroupResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)
            }
        }
    }

    def delete(params) {
        renderXml() {
            StatusResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)
            }
        }
    }

    def removeResource(params) {
        renderXml() {
            StatusResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)                
            }
        }
    }

    def list(params) {
        def compatible = params.getOne('compatible')?.toBoolean()

        def groups = resourceHelper.findViewableGroups()

        if (compatible != null) {
            if (compatible)
                groups = groups.grep { it.resourcePrototype != null }
            else
                groups = groups.grep { it.resourcePrototype == null }
        }

        renderXml() {
            out << GroupsResponse() {
                out << getSuccessXML()
                for (g in  groups.sort {a, b -> a.name <=> b.name}) {
                    out << getGroupXML(g)
                }
            }
        }
    }

    def listResources(params) {
        def id = params.getOne("groupId")?.toInteger()

        if (!id) {
            renderXml() {
                ResourcesResponse() {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                }
            }
            return
        }

        def group = getGroup(id, null)
        if (!group) {
            renderXml() {
                ResourcesResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND);
                }
            }
            return
        }

        def resources = group.resources
        renderXml() {
            ResourcesResponse() {
                out << getSuccessXML()   
                for (r in resources.sort {a, b -> a.name <=> b.name}) {
                    out << getResourceXML(r)
                }
            }
        }
    }

    def addResource(params) {
        renderXml() {
            StatusResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)                                                                
            }
        }
    }
}