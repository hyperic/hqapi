import org.hyperic.hq.hqapi1.ErrorCode

class GroupController extends ApiController {

    private Closure getGroupXML(g) {
        { doc ->
            Group(id          : g.id,
                  name        : g.name,
                  description : g.description,
                  location    : g.location) {
                // TODO: Roles, Criteria, Prototype
            }
        }
    }

    def create(params) {
        renderXml() {
            CreateGroupResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)
            }
        }
    }

    def delete(params) {
        renderXml() {
            DeleteGroupResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)
            }
        }
    }

    def removeResource(params) {
        renderXml() {
            RemoveResourceFromGroupResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)                
            }
        }
    }

    def list(params) {
        def groups = resourceHelper.findViewableGroups()

         renderXml() {
            out << GetGroupsResponse() {
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
                FindResourcesResponse() {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                }
            }
            return
        }

        def group = resourceHelper.findGroup(id)
        if (!group) {
            renderXml() {
                FindResourcesResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND);
                }
            }
            return
        }

        def resources = group.resources
        renderXml() {
            FindResourcesResponse() {
                out << getSuccessXML()   
                for (r in resources.sort {a, b -> a.name <=> b.name}) {
                    out << getResourceXML(r)
                }
            }
        }
    }

    def addResource(params) {
        renderXml() {
            AddResourceToGroupResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)                                                                
            }
        }
    }
}