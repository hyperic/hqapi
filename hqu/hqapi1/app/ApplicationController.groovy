import org.hyperic.hq.hqapi1.ErrorCode;

class ApplicationController extends ApiController {

    def list(params) {
        def failureXml = null

        renderXml() {
            ApplicationsResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def create(params) {
        def failureXml = null

        renderXml() {
            ApplicationResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def update(params) {
        def failureXml = null

        renderXml() {
            ApplicationResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def delete(params) {
        def failureXml = null

        renderXml() {
            StatusResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }
}