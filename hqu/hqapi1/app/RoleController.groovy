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

    /**
     * Get a Role by id or name
     * @return The role by the given id.  If the passed in id is null then
     * the Role by the given name is returned.  If no role could be found
     * for either the id or name, null is returned.
     */
    private getRole(Integer id, String name) {
        if (id) {
            return roleHelper.getRoleById(id)
        } else {
            return roleHelper.findRoleByName(name)
        }
    }

    def get(params) {
        def id   = params.getOne("id")?.toInteger()
        def name = params.getOne("name")

        def r = getRole(id, name)
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
            def existing = getRole(null, xmlIn.'@name')
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
            def existing = getRole(xmlIn.'@id'?.toInteger(), xmlIn.'@name')
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

    def sync(params) {
        def failureXml
        try {
            def syncRequest = new XmlParser().parseText(getUpload('postdata'))
            for (xmlRole in syncRequest['Role']) {
                def existing = getRole(xmlRole.'@id'?.toInteger(),
                                       xmlRole.'@name')
                if (existing) {
                    def opMap = roleHelper.operationMap
                    def operations = []
                    def ops = xmlRole['Operation']
                    ops.each{o ->
                        operations << opMap[o.text()]
                    }

                    existing.update(user,
                                    xmlRole.'@name',
                                    xmlRole.'@description')
                    existing.setOperations(user, operations)
                } else {
                    def operations = []
                    def ops = xmlRole['Operation']
                    ops.each{o ->
                        operations << o.text()
                    }

                    roleHelper.createRole(xmlRole.'@name',
                                          xmlRole.'@description',
                                          operations as String[],
                                          [] as Integer[],
                                          [] as Integer[])
                }
            }
        } catch (PermissionException e) {
            log.debug("Permission denied [${user.name}]", e)
            failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
        } catch (Exception e) {
            log.error("UnexpectedError: " + e.getMessage(), e)
            failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
        }

        renderXml() {
            SyncRolesResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def setUsers(params) {
        def failureXml
        try {
            def setRequest = new XmlParser().parseText(getUpload('postdata'))
            def xmlRole = setRequest['Role']

            if (!xmlRole || xmlRole.size() != 1) {
                renderXml() {
                    UpdateRoleResponse() {
                        out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                    }
                }
                return
            }

            def xmlIn = xmlRole[0]
            def role = getRole(xmlIn.'@id'?.toInteger(),
                               xmlIn.'@name')
            if (!role) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            } else {
                def users = []
                for (xmlUser in setRequest['User']) {
                    def u = getUser(xmlUser.'@id'?.toInteger(),
                                    xmlUser.'@name')
                    if (u) {
                        users << u
                    }
                }

                role.setSubjects(user, users)
            }
        } catch (PermissionException e) {
            log.debug("Permission denied [${user.name}]", e)
            failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
        } catch (Exception e) {
            log.error("UnexpectedError: " + e.getMessage(), e)
            failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
        }

        renderXml() {
            SetUsersResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def getUsers(params) {
        def id   = params.getOne("id")?.toInteger()
        def name = params.getOne("name")

        def r = getRole(id, name)
        renderXml() {
            GetUsersResponse() {
                if (!r) {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                } else {
                    out << getSuccessXML()
                    r.subjects.each{u ->
                        out << getUserXML(u)
                    }
                }
            }
        }
    }

    def delete(params) {
        def id = params.getOne('id')?.toInteger()
        def name = params.getOne("name")
        
        def failureXml
        def existing = getRole(id, name)
        if (!existing) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
        } else {
            try {
                existing.remove(user)
            } catch (PermissionException e) {
                log.debug("Permission denied [${user.name}]", e)
                failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
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