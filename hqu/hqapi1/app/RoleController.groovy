import org.hyperic.hq.hqu.rendit.BaseController

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
}