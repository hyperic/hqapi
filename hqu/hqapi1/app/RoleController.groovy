import org.hyperic.hq.hqu.rendit.BaseController

import org.hyperic.hq.authz.shared.PermissionException
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
            r = roleHelper.getRoleById(id)
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

    def create(params) {
        def failureXml
        def createdRole
        try {
            def createRequest = new XmlParser().parseText(getUpload('postdata'))
            def xmlRole = createRequest['Role']

            if (!xmlRole || xmlRole.size() != 1) {
                renderXml() {
                    CreateRoleResponse() {
                        out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                    }
                }
                return
            }

            def xmlIn = xmlRole[0]
            def name = xmlIn.'@name'
            def existing = roleHelper.findRoleByName(name)
            if (existing) {
                failureXml = getFailureXML(ErrorCode.OBJECT_EXISTS)
            } else {
                createdRole = roleHelper.createRole(xmlIn.'@name',
                                                    xmlIn.'@description',
                                                    [] as String[],
                                                    [] as Integer[],
                                                    [] as Integer[])
            }
        } catch (PermissionException e) {
            log.debug("Permission denied [${user.name}]", e)
            failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
        } catch (Exception e) {
            log.error("UnexpectedError: " + e.getMessage(), e)
            failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
        }

        renderXml() {
            CreateRoleResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    out << getRoleXML(createdRole)
                }
            }
        }
    }
}