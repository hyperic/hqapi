import org.hyperic.hq.hqu.rendit.BaseController

class ApiController extends BaseController {
    
    // List of all possible error conditions
    final CODES =
    [LoginFailure: "The given username and password could not be validated",
     ObjectNotFound: "The requested object could not be found",
     ObjectExists: "The given object already exists",
     InvalidParameters: "The given parameters are incorrect",
     UnexpectedError: "An unexpected error occured",
     PermissionDenied: "Permission denied"]

    void printSuccessStatus(xmlOut) {
        xmlOut.Status("Success")
    }

    void printFailureStatus(xmlOut, code) {
        def reason = CODES.get(code)
        if (reason == null) {
            throw new IllegalArgumentException("Invalid ErrorCode: " + code)
        }

        xmlOut.Status("Failure")
        xmlOut.Error() {
            ErrorCode(code)
            ReasonText(reason)
        }
    }

    def index(params) {
        render(locals:[plugin: getPlugin()])
    }
}