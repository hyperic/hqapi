import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqapi1.ErrorCode

class RoleController extends ApiController {

    private Closure getRoleXML(r) {
        { doc ->
            Role(id          : r.id,
                 name        : r.name,
                 description : r.description) {
                for (o in r.operations.sort {a, b -> a.name <=> b.name}) {
                    Operation(o.name)
                }
            }
        }
    }

    def list(params) {
        renderXml() {
            out << GetRolesResponse() {
                out << getSuccessXML()
                for (role in roleHelper.allRoles.sort {a, b -> a.name <=> b.name}) {
                    out << getRoleXML(role)
                }
            }
        }
    }

    def get(params) {
        def id   = params.getOne("id")?.toInteger()
        def name = params.getOne("name")

        def r
        if (id) {
            r = roleHelper.findRoleById(id)
        } else {
            r = roleHelper.findRoleByName(name)
        }

        renderXml() {
            GetRoleResponse() {
                if (!r) {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                } else {
                    out << getSuccessXML()
                    out << getRoleXML(r)
                }
            }
        }
    }
}