import org.hyperic.hq.hqu.rendit.BaseController

import groovy.xml.StreamingMarkupBuilder
import org.hyperic.hq.hqapi1.ErrorCode

class ApiController extends BaseController {

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
    protected Closure getFailureXML(ErrorCode code) {
        { doc ->
            Status("Failure")
            Error() {
                ErrorCode(code.getErrorCode())
                ReasonText(code.getReasonText())
            }
        }
    }

    def index(params) {
        render(locals:[plugin: getPlugin()])
    }
}
