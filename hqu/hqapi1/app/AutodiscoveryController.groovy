
import org.hyperic.hq.hqapi1.ErrorCode;

class AutodiscoveryController extends ApiController {

    def getQueue(params) {

        renderXml() {
            out << GetQueueResponse() {
                out << getSuccessXML()
            }
        }
    }

    def approve(params) {
        def fqdn = params.getOne('fqdn')

        def failureXml
        if (!fqdn || fqdn.length() == 0) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        }

        renderXml() {
            out << ApproveResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }
}