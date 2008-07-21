import org.hyperic.hq.hqu.rendit.BaseController

import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.hqapi1.ErrorCode;

class UserController extends ApiController {

    private Closure getUserXML(u) {
        { doc -> 
            User(id          : u.id,
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

    def list(params) {
        renderXml() {
            out << GetUsersResponse() {
                out << getSuccessXML()
                for (u in userHelper.allUsers.sort {a, b -> a.name <=> b.name}) {
                    out << getUserXML(u)
                }
            }
        }
    }

    def get(params) {
        def id   = params.getOne("id")?.toInteger()
        def name = params.getOne("name")

        def u
        if (id) {
            u = userHelper.getUser(id)
        } else {
            u = userHelper.findUser(name)
        }
        
        renderXml() {
            GetUserResponse() {
                if (!u) {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                } else {
                    out << getSuccessXML()
                    out << getUserXML(u)
                }
            }
        }
    }

    def create(params) {
        // Required attributes
        def name     = params.getOne("name")
        def password = params.getOne("password")
        def first    = params.getOne("firstName")
        def last     = params.getOne("lastName")
        def email    = params.getOne("emailAddress")

        // Optional attributes
        def htmlEmail = params.getOne("htmlEmail", "false").toBoolean()
        def active    = params.getOne("active", "false").toBoolean()
        def dept      = params.getOne("department")
        def phone     = params.getOne("phoneNumber")
        def sms       = params.getOne("SMSAddress")

        // We require the user to authenticate via built in JDBC
        def dsn = "CAM"

        def failureXml
        def newUser
        if (!name || !password || !first || !last || !email) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            try {
                def existing = userHelper.findUser(name)
                if (existing) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_EXISTS)
                } else {
                    newUser = userHelper.createUser(name, password, active,
                                                    dsn, dept, email, first,
                                                    last, phone, sms, htmlEmail)
                }
            } catch (Exception e) {
                log.error("UnexpectedError: " + e.getMessage(), e);
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
            }
        }
        
        renderXml() {
            CreateUserResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    out << getUserXML(newUser)
                }
            }
        }
    }

    def delete(params) {
        def id = params.getOne('id')?.toInteger()

        def existing = userHelper.getUser(id)
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
            DeleteUserResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def update(params) {
        def failureXml
        try {
            def updateRequest = new XmlParser().parseText(getUpload('postdata'))
            def xmlUser = updateRequest['User']
            
            if (!xmlUser || xmlUser.size() != 1) {
                renderXml() {
                    UpdateUserResponse() {
                        out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                    }
                }
                return
            }
            
            def xmlIn = xmlUser[0]

            def name = xmlIn.'@name'
            def existing = userHelper.findUser(name)
            if (!existing) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            } else {
                userHelper.updateUser(existing,
                                      xmlIn.'@active'?.toBoolean(),
                                      "CAM", // Dsn
                                      xmlIn.'@department',
                                      xmlIn.'@emailAddress',
                                      xmlIn.'@firstName',
                                      xmlIn.'@lastName',
                                      xmlIn.'@phoneNumber',
                                      xmlIn.'@SMSAddress',
                                      xmlIn.'@htmlEmail'?.toBoolean())
            }
        } catch (PermissionException e) {
            log.debug("Permission denied [${user.name}]", e)
            failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
        } catch (Exception e) {
            log.error("UnexpectedError: " + e.getMessage(), e)
            failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
        }

        renderXml() {
            UpdateUserResponse() {
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
            for (xmlUser in syncRequest['User']) {
                def name = xmlUser.'@name'
                def existing = userHelper.findUser(name)
                if (existing) {
                    userHelper.updateUser(existing,
                                          xmlUser.'@active'?.toBoolean(),
                                          "CAM", // Dsn
                                          xmlUser.'@department',
                                          xmlUser.'@emailAddress',
                                          xmlUser.'@firstName',
                                          xmlUser.'@lastName',
                                          xmlUser.'@phoneNumber',
                                          xmlUser.'@SMSAddress',
                                          xmlUser.'@htmlEmail'?.toBoolean())
                } else {
                    if (!xmlUser.'@name' || !xmlUser.'@firstName' ||
                        !xmlUser.'@lastName' || !xmlUser.'@emailAddress') {
                        failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
                    } else {
                        // XXX: This needs to handle the password hash                        
                        userHelper.createUser(xmlUser.'@name',
                                              xmlUser.'@active'?.toBoolean(),
                                              "CAM", // Dsn
                                              xmlUser.'@department',
                                              xmlUser.'@emailAddress',
                                              xmlUser.'@firstName',
                                              xmlUser.'@lastName',
                                              xmlUser.'@phoneNumber',
                                              xmlUser.'@SMSAddress',
                                              xmlUser.'@htmlEmail'?.toBoolean())
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
            SyncUsersResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def changePassword(params) {
        def id = params.getOne('id')?.toInteger()
        def password = params.getOne('password')

        def failureXml

        if (!password || password.length() == 0) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {

            def existing = userHelper.getUser(id)
            if (!existing) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            } else {
                try {
                    existing.changePassword(user, password)
                } catch (Exception e) {
                    log.error("UnexpectedError: " + e.getMessage(), e)
                    failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
                }
            }
        }

        renderXml() {
            ChangePasswordResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }
}
