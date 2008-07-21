import org.hyperic.hq.hqu.rendit.BaseController

import groovy.xml.StreamingMarkupBuilder
import org.hyperic.hq.hqapi1.ErrorCode

class ApiController extends BaseController {

    /**
     * Get the ResponseStatus Success XML.
     */
    protected Closure getSuccessXML() {
        { doc -> 
            Status("Success")
        }
    }

    /**
     * Get the ResponseStatus Failure XML.
     */
    protected Closure getFailureXML(ErrorCode code) {
        { doc ->
            Status("Failure")
            Error() {
                ErrorCode(code.getErrorCode())
                ReasonText(code.getReasonText())
            }
        }
    }

    /**
     * Get the XML for a User
     */
    protected Closure getUserXML(u) {
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

    def index(params) {
        render(locals:[plugin: getPlugin()])
    }
}
