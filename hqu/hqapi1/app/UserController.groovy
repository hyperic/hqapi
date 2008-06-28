import org.hyperic.hq.hqu.rendit.BaseController

class UserController extends ApiController
{
    protected void init() {
        setXMLMethods(['list', 'get', 'create', 'delete', 'sync'])
    }

    private printUser(xmlOut, u) {
        xmlOut.User() {
            Id(u.id)
            Name(u.name)
            FirstName(u.firstName)
            LastName(u.lastName)
            Department(u.department)
            EmailAddress(u.emailAddress)
            SMSAddress(u.SMSAddress)
            Active(u.active)
            HtmlEmail(u.htmlEmail)
        }
    }

    def list(xmlOut, params) {
        def users = userHelper.allUsers

        xmlOut.GetUsersResponse() {
            printSuccessStatus(xmlOut)
            for (u in users.sort {a, b -> a.name <=> b.name}) {
                printUser(xmlOut, u)
            }
        }

        xmlOut
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
        def name = params.getOne("Name")
        def password = params.getOne("Password")
        def first = params.getOne("FirstName")
        def last = params.getOne("LastName")
        def email = params.getOne("EmailAddress")

        // Optional attributes
        def htmlEmail = params.getOne("HtmlEmail", "false").toBoolean()
        def active = params.getOne("Active", "false").toBoolean()
        def dept = params.getOne("Department")
        def phone = params.getOne("Phone")
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
                xmlOut.CreateUserResponse() {
                    printFailureStatus(xmlOut, "UnexpectedError")
                }
            }
        }

        xmlOut
    }

    def delete(xmlOut, params) {
        def name = params.getOne('Name')

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

    def sync(xmlOut, params) {

        try {
            def xmlIn = new XmlParser().parseText(getUpload('postdata'))

            def name = xmlIn['Name'].text()
            def existing = userHelper.findUser(name)
            if (!existing) {
                xmlOut.SyncUserResponse() {
                    printFailureStatus(xmlOut, "ObjectNotFound")
                }
            } else {
                userHelper.updateUser(user,
                                      xmlIn['Active'].text()?.toBoolean(),
                                      "CAM", // Dsn
                                      xmlIn['Department'].text(),
                                      xmlIn['EmailAddress'].text(),
                                      xmlIn['FirstName'].text(),
                                      xmlIn['LastName'].text(),
                                      xmlIn['Phone'].text(),
                                      xmlIn['SMSAddress'].text(),
                                      xmlIn['HtmlEmail'].text()?.toBoolean())
                xmlOut.SyncUserResponse() {
                    printSuccessStatus(xmlOut)
                }
            }
        } catch (Exception e) {
            log.error("UnexpectedError: " + e.getMessage(), e)
            xmlOut.SyncUserResponse() {
                printFailureStatus(xmlOut, "UnexpectedError")
            }
        }

        xmlOut
    }
}
