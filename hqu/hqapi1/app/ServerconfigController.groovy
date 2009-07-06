import org.hyperic.hq.common.server.session.ServerConfigManagerEJBImpl as SMan
import org.hyperic.hq.hqapi1.ErrorCode

class ServerconfigController extends ApiController {

    private _serverMan = SMan.one
        
    def getConfig(params) {

        def props = _serverMan.config

        renderXml() {
            ServerConfigResponse() {
                if (!user.isSuperUser()) {
                    out << getFailureXML(ErrorCode.PERMISSION_DENIED,
                                         "User " + user.name + " is not superuser")
                } else {
                    out << getSuccessXML()
                    props.each { k, v ->
                        ServerConfig(key: k, value: v)
                    }
                }
            }
        }
    }

    def setConfig(params) {

        // TODO: Implement
        
        renderXml() {
            StatusResponse() {
                out << getSuccessXML()
            }
        }
    }
}