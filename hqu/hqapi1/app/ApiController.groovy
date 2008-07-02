import org.hyperic.hq.hqu.rendit.BaseController

import groovy.xml.StreamingMarkupBuilder

class ApiController extends BaseController {
    
    // List of all possible error conditions
    final CODES = [
     LoginFailure: "The given username and password could not be validated",
     ObjectNotFound: "The requested object could not be found",
     ObjectExists: "The given object already exists",
     InvalidParameters: "The given parameters are incorrect",
     UnexpectedError: "An unexpected error occured",
     PermissionDenied: "Permission denied"
    ]

    protected Closure getSuccessXML() {
        { doc -> 
            Status("Success")
        }
    }

    /**
     * Add a failure code to a chunk of XML.
     * 
     * @param code  Must be a code from CODES
     */
    protected Closure getFailureXML(String code) {
        def reason = CODES.get(code)
        if (reason == null) {
            throw new IllegalArgumentException("Invalid ErrorCode: [${code}]")
        }

        { doc ->
            Status("Failure")
            Error() {
                ErrorCode(code)
                ReasonText(reason)
            }
        }
    }

    def index(params) {
        render(locals:[plugin: getPlugin()])
    }
}
