import org.hyperic.hq.hqu.rendit.BaseController

import ErrorHandler

// XXX: Need to create a base class more suitable.  It should include the
//      bits from ErrorHandler.groovy
class UserController extends BaseController
{
    protected void init() {
        setXMLMethods(['list', 'get', 'create'])
    }

    private printUser(xmlOut, u) {
        xmlOut.User() {
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
            ErrorHandler.printSuccessStatus(xmlOut)
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
                ErrorHandler.printFailureStatus(xmlOut, "ObjectNotFound")
            }
        } else {
            xmlOut.GetUserResponse() {
                ErrorHandler.printSuccessStatus(xmlOut)
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
        def htmlEmail = params.getOne("HtmlEmail", "false")
        def active = params.getOne("Active", "true")
        def dept = params.getOne("Department")
        def phone = params.getOne("Phone")
        def sms = params.getOne("SMSAddress")

        // We require the user to authenticate via built in JDBC
        def dsn = "CAM"

        if (name == null || password == null || first == null ||
            last == null || email == null) {
            xmlOut.CreateUserResponse() {
                ErrorHandler.printFailureStatus(xmlOut, "InvalidParameters")
            }
        } else {
            // XXX: Do it
            xmlOut.CreateUserResponse() {
                ErrorHandler.printSuccessStatus(xmlOut)
            }
        }

        xmlOut
    }
}
