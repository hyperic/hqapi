import org.hyperic.hq.hqu.rendit.BaseController

import org.hyperic.hq.authz.shared.PermissionException;

class UserController extends ApiController {
    private Closure getUserXML(u) {
        { doc -> 
            User(id          : u.id,
                 name        : u.name,
                 firstName   : u.firstName,
                 lastName    : u.lastName,
                 department  : (u.department ? u.department : ''),
                 email       : u.emailAddress,
                 smsAddress  : (u.SMSAddress ? u.SMSAddress : ''),
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
        def name = params.getOne("name")
        def u    = userHelper.findUser(name)

        renderXml() {
            GetUserResponse() {
                if (!u) {
                    out << getFailureXML("ObjectNotFound")
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
        
        if (!name || !password || !first || !last || !email) {
            failureXml = getFailureXML("InvalidParameters")
        } else {
            try {
                def existing = userHelper.findUser(name)
                if (existing) {
                    failureXml = getFailureXML("ObjectExists")
                } else {
                    userHelper.createUser(name, password, active,
                                          dsn, dept, email, first,
                                          last, phone, sms, htmlEmail)
                }
            } catch (Exception e) {
                log.error("UnexpectedError: " + e.getMessage(), e);
                failureXml = getFailureXML("UnexpectedError")
            }
        }
        
        renderXml() {
            CreateUserResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def delete(params) {
        def name = params.getOne('name')

        def existing = userHelper.findUser(name)
        def failureXml
        
        if (!existing) {
            failureXml = getFailureXML("ObjectNotFound")
        } else {
            try {
                userHelper.removeUser(existing.id)
            } catch (Exception e) {
                log.error("UnexpectedError: " + e.getMessage(), e)
                failureXml = getFailureXML("UnexpectedError")
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
                        out << getFailureXML('InvalidParameters')
                    }
                }
                return
            }
            
            def xmlIn = xmlUser[0]

            def name = xmlIn.'@name'
            def existing = userHelper.findUser(name)
            if (!existing) {
                failureXml = getFailureXML("ObjectNotFound")
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
            log.error("Permission denied [${user.name}]", e)
            failureXml = getFailureXML("PermissionDenied")
        } catch (Exception e) {
            log.error("UnexpectedError: " + e.getMessage(), e)
            failureXml = getFailureXML("UnexpectedError")
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
        xmlOut
    }
}
