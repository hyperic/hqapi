import org.hyperic.hq.hqu.rendit.BaseController

import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.authz.shared.AuthzDuplicateNameException

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
                def operations = []
                def ops = xmlIn['Operation']
                ops.each{o ->
                    operations << o.text()
                }
                
                createdRole = roleHelper.createRole(xmlIn.'@name',
                                                    xmlIn.'@description',
                                                    operations as String[],
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

    def update(params) {
        def failureXml
        def updatedRole
        try {
            def updateRequest = new XmlParser().parseText(getUpload('postdata'))
            def xmlRole = updateRequest['Role']

            if (!xmlRole || xmlRole.size() != 1) {
                renderXml() {
                    UpdateRoleResponse() {
                        out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                    }
                }
                return
            }

            def xmlIn = xmlRole[0]
            def id = xmlIn.'@id'.toInteger()
            def existing = roleHelper.getRoleById(id)
            if (!existing) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            } else {
                def opMap = roleHelper.operationMap
                def operations = []
                def ops = xmlIn['Operation']
                ops.each{o ->
                    operations << opMap[o.text()]
                }

                existing.update(user,
                                xmlIn.'@name',
                                xmlIn.'@description')
                existing.setOperations(user, operations)
            }
        } catch (AuthzDuplicateNameException e) {
            log.debug("Duplicate object", e)
            failureXml = getFailureXML(ErrorCode.OBJECT_EXISTS)
        } catch (PermissionException e) {
            log.debug("Permission denied [${user.name}]", e)
            failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
        } catch (Exception e) {
            log.error("UnexpectedError: " + e.getMessage(), e)
            failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
        }

        renderXml() {
            UpdateRoleResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def delete(params) {
        def id = params.getOne('id')?.toInteger()

        def existing = roleHelper.getRoleById(id)
        def failureXml

        if (!existing) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
        } else {
            try {
                existing.remove(user)
            } catch (Exception e) {
                log.error("UnexpectedError: " + e.getMessage(), e)
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
            }
        }

        renderXml() {
            DeleteRoleResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }
}