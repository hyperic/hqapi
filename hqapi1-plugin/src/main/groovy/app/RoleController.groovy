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
                for (u in r.subjects.sort {a, b -> a.name <=> b.name}) {
                    // TODO: Use getUserXML() in ApiController
                    User(id         : u.id,
                        name        : u.name,
                        firstName   : u.firstName,
                        lastName    : u.lastName,
                        department  : (u.department ? u.department : ''),
                        emailAddress: u.emailAddress,
                        SMSAddress  : (u.SMSAddress ? u.SMSAddress : ''),
                        phoneNumber : (u.phoneNumber ? u.phoneNumber : ''),
                        active      : u.active,
                        htmlEmail   : u.htmlEmail)
                }
            }
        }
    }

    def list(params) {
        def user = params.getOne("user")

        def roles
        if (user && user.length() != 0) {
            def subject = getUser(null, user)
            if (!subject) {
                renderXml() {
                    RolesResponse() {
                        out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                             "User with name " + user +
                                             " not found")
                    }
                }
                return
            }
            roles = subject.roles
        } else {
            roles = roleHelper.allRoles
        }

        renderXml() {
            out << RolesResponse() {
                out << getSuccessXML()
                for (role in roles.sort {a, b -> a.name <=> b.name}) {
                    if (!role.system) {
                        out << getRoleXML(role)
                    }
                }
            }
        }
    }

    def get(params) {
        def id   = params.getOne("id")?.toInteger()
        def name = params.getOne("name")

        def r = getRole(id, name)
        renderXml() {
            RoleResponse() {
                if (!r) {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                         "Role with id=" + id + " name='" +
                                         name + "' not found")
                } else if (r.system && r.id != 0 && r.id != 2) {  // Allow listing of Super User Role and Guest Role
                    out << getFailureXML(ErrorCode.NOT_SUPPORTED,
                                         "Cannot get system role " + r.name)
                } else {
                    out << getSuccessXML()
                    out << getRoleXML(r)
                }
            }
        }
    }

    def create(params) {
        def failureXml = null
        def createdRole
        try {
            def createRequest = new XmlParser().parseText(getPostData())
            def xmlRole = createRequest['Role']

            if (!xmlRole || xmlRole.size() != 1) {
                renderXml() {
                    RoleResponse() {
                        out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                    }
                }
                return
            }

            def xmlIn = xmlRole[0]
            def existing = getRole(null, xmlIn.'@name')
            if (existing) {
                failureXml = getFailureXML(ErrorCode.OBJECT_EXISTS,
                                           "Role with name='" + xmlIn.'@name' +
                                           "' already exists")
            } else {
                def operations = []
                def ops = xmlIn['Operation']
                ops.each{o ->
                    operations << o.text()
                }

                def users = []
                def subjects = xmlIn['User']
                subjects.each{subj ->
                    def u = getUser(subj.'@id'?.toInteger(), subj.'@name')
                    if (u) {
                        users << u
                    } else {
                        failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                                   "User with id=" + subj.'@id' +
                                                   ", name=" + subj.'@name' +
                                                   " not found")
                    }
                }

                if (!failureXml) {
                    createdRole = roleHelper.createRole(xmlIn.'@name',
                                                        xmlIn.'@description',
                                                        operations as String[],
                                                        users*.id as Integer[],
                                                        [] as Integer[])
                    // TODO: Setting subjects via createRole broken?
                    createdRole.setSubjects(user, users)
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
            RoleResponse() {
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
        def failureXml = null
        try {
            def updateRequest = new XmlParser().parseText(getPostData())
            def xmlRole = updateRequest['Role']

            if (!xmlRole || xmlRole.size() != 1) {
                renderXml() {
                    StatusResponse() {
                        out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                    }
                }
                return
            }

            def xmlIn = xmlRole[0]
            def existing = getRole(xmlIn.'@id'?.toInteger(), xmlIn.'@name')
            if (!existing) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            } else if (existing.system) {
                failureXml = getFailureXML(ErrorCode.NOT_SUPPORTED,
                                           "Cannot update system role " +
                                           existing.name)
            } else {
                def opMap = roleHelper.operationMap
                def operations = []
                def ops = xmlIn['Operation']
                ops.each{o ->
                    operations << opMap[o.text()]
                }

                def users = []
                def subjects = xmlIn['User']
                subjects.each{subj ->
                    def u = getUser(subj.'@id'?.toInteger(), subj.'@name')
                    if (u) {
                        users << u
                    } else {
                        failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                                   "User with id=" + subj.'@id' +
                                                   ", name=" + subj.'@name' +
                                                   " not found")
                    }
                }

                if (!failureXml) {
                    existing.update(user,
                                    xmlIn.'@name',
                                    xmlIn.'@description')
                    existing.setOperations(user, operations)
                    existing.setSubjects(user, users)
                }
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
            StatusResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def sync(params) {
        def SUPER_USER_ROLE = "Super User Role"
        def GUEST_ROLE = "Guest Role"
        def failureXml = null
        
        try {
            def syncRequest = new XmlParser().parseText(getPostData())
            for (xmlRole in syncRequest['Role']) {
                def existing = getRole(xmlRole.'@id'?.toInteger(),
                                       xmlRole.'@name')
                // Break early if a system role is being synced.
                if (existing?.system && existing?.id != 0 && existing?.id != 1) {
                    failureXml = getFailureXML(ErrorCode.NOT_SUPPORTED,
                                               "Cannot sync system role " +
                                               existing.name)
                    break
                }

                if (existing) {
                    def systemUserPresent = false
                    def opMap = roleHelper.operationMap
                    def operations = []
                    def ops = xmlRole['Operation']
                    ops.each{o ->
                        operations << opMap[o.text()]
                    }

                    def users = []
                    def subjects = xmlRole['User']
                    subjects.each{subj ->
                        def u = getUser(subj.'@id'?.toInteger(), subj.'@name')
                        if (u) {
                            if (u.id == 1 || u.id == 2) {
                                systemUserPresent = true
                            }
                            users << u
                        } else {
                            failureXml=  getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                                       "User with id=" + subj.'@id' +
                                                       ", name=" + subj.'@name' +
                                                       " not found")
                        }
                    }
                 
                    if (!failureXml) {
                        if (existing.id == 0 || existing.id == 2) {
                            if (systemUserPresent) {
                                existing.setSubjects(user, users)
                            } else {
                                failureXml = getFailureXML(ErrorCode.NOT_SUPPORTED, 
                                                        "Required user missing from system role")
                            }
                        } else {
                            existing.update(user,
                                        xmlRole.'@name',
                                        xmlRole.'@description')
                            existing.setOperations(user, operations)
                            existing.setSubjects(user, users)
                        }
                    }
                } else {
                    def operations = []
                    def ops = xmlRole['Operation']
                    ops.each{o ->
                        operations << o.text()
                    }

                    def users = []
                    def subjects = xmlRole['User']
                    subjects.each{subj ->
                        def u = getUser(subj.'@id'?.toInteger(), subj.'@name')
                        if (u) {
                            users << u
                        } else {
                            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                                       "User with id=" + subj.'@id' +
                                                       ", name=" + subj.'@name' +
                                                       " not found")
                        }
                    }

                    if (!failureXml) {
                        def createdRole = roleHelper.createRole(xmlRole.'@name',
                                                                xmlRole.'@description',
                                                                operations as String[],
                                                                [] as Integer[],
                                                                [] as Integer[])

                        // TODO: Setting subjects via createRole broken?
                        createdRole.setSubjects(user, users)
                    }
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
        def id = params.getOne('id')?.toInteger()
        def name = params.getOne("name")
        
        def failureXml
        def existing = getRole(id, name)
        if (!existing) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                       "Unable to find role with id=" + id +
                                       " name='" + name + "'")
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
            StatusResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }
}