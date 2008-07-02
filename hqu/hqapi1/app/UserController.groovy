import org.hyperic.hq.hqu.rendit.BaseController

import org.hyperic.hq.authz.shared.PermissionException;

class UserController extends ApiController
{
    protected void init() {
        setXMLMethods(['get', 'create', 'delete', 'update'])
    }

    private printUser(xmlOut, u) {
        xmlOut.User(id:u.id,
                    name:u.name,
                    firstName: u.firstName,
                    lastName: u.lastName,
                    department: (u.department ? u.department : ''),
                    email: u.emailAddress,
                    smsAddress: (u.SMSAddress ? u.SMSAddress : ''),
                    phoneNumber: (u.phoneNumber ? u.phoneNumber : ''),
                    active: u.active,
                    htmlEmail: u.htmlEmail)
    }

    def list(params) {
        def users = userHelper.allUsers

        renderXml() { xmlOut ->
            GetUsersResponse() {    
                printSuccessStatus(xmlOut)
                for (u in users.sort {a, b -> a.name <=> b.name}) {
                    printUser(xmlOut, u)
                }
            }
        }
    }

    def get(xmlOut, params) {
        def name = params.getOne("name")
        def u = userHelper.findUser(name)

        if (u == null) {
            xmlOut.GetUserResponse() {
                printFailureStatus(xmlOut, "ObjectNotFound")
            }
        } else {
            xmlOut.GetUserResponse() {
                printSuccessStatus(xmlOut)
                printUser(xmlOut, u)
            }
        }
        
        xmlOut
    }

    def create(xmlOut, params) {
        // Required attributes
        def name = params.getOne("name")
        def password = params.getOne("password")
        def first = params.getOne("firstName")
        def last = params.getOne("lastName")
        def email = params.getOne("emailAddress")

        // Optional attributes
        def htmlEmail = params.getOne("htmlEmail", "false").toBoolean()
        def active = params.getOne("active", "false").toBoolean()
        def dept = params.getOne("department")
        def phone = params.getOne("phoneNumber")
        def sms = params.getOne("SMSAddress")

        // We require the user to authenticate via built in JDBC
        def dsn = "CAM"

        if (name == null || password == null || first == null ||
            last == null || email == null) {
            xmlOut.CreateUserResponse() {
                printFailureStatus(xmlOut, "InvalidParameters")
            }
        } else {
            try {
                def existing = userHelper.findUser(name)
                if (existing) {
                    xmlOut.CreateUserResponse() {
                        printFailureStatus(xmlOut, "ObjectExists")
                    }
                } else {
                    userHelper.createUser(name, password, active, dsn, dept,
                                          email, first, last, phone, sms,
                                          htmlEmail)
                    xmlOut.CreateUserResponse() {
                        printSuccessStatus(xmlOut)
                    }
                }
            } catch (Exception e) {
                log.error("UnexpectedError: " + e.getMessage(), e);
                xmlOut.CreateUserResponse() {
                    printFailureStatus(xmlOut, "UnexpectedError")
                }
            }
        }

        xmlOut
    }

    def delete(xmlOut, params) {
        def name = params.getOne('name')

        def existing = userHelper.findUser(name)
        if (!existing) {
            xmlOut.DeleteUserResponse() {
                printFailureStatus(xmlOut, "ObjectNotFound")
            }
        } else {
            try {
                userHelper.removeUser(existing.id)
                xmlOut.DeleteUserResponse() {
                    printSuccessStatus(xmlOut)
                }
            } catch (Exception e) {
                log.error("UnexpectedError: " + e.getMessage(), e)
                xmlOut.DeleteUserResponse() {
                    printFailureStatus(xmlOut, "UnexpectedError")
                }
            }
        }

        xmlOut
    }

    def update(xmlOut, params) {

        try {
            def updateRequest = new XmlParser().parseText(getUpload('postdata'))
            def xmlUser = updateRequest['User']
            
            if (xmlUser == null || xmlUser.size() != 1) {
                xmlOut.UpdateUserResponse() {
                    printFailureStatus(xmlOut, 'InvalidParameters')
                }
                return xmlOut
            }
            
            def xmlIn = xmlUser[0]

            def name = xmlIn.'@name'
            log.info "xmlIn = ${xmlIn}"
            log.info "xmlIn.active = ${xmlIn.'@active'}"
            def existing = userHelper.findUser(name)
            if (!existing) {
                xmlOut.UpdateUserResponse() {
                    printFailureStatus(xmlOut, "ObjectNotFound")
                }
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
                xmlOut.UpdateUserResponse() {
                    printSuccessStatus(xmlOut)
                }
            }
        } catch (PermissionException e) {
            xmlOut.UpdateUserResponse() {
                printFailureStatus(xmlOut, "PermissionDenied")
            }
        } catch (Exception e) {
            log.error("UnexpectedError: " + e.getMessage(), e)
            xmlOut.UpdateUserResponse() {
                printFailureStatus(xmlOut, "UnexpectedError")
            }
        }

        xmlOut
    }
}
