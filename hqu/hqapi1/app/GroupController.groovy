import org.hyperic.hq.hqapi1.ErrorCode

class GroupController extends ApiController {

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
        renderXml() {
            GetGroupsResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)                                
            }
        }
    }

    def listResources(params) {
        renderXml() {
            FindResourcesResponse() {
                out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)                                                
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