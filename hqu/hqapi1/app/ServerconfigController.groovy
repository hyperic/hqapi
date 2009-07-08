import org.hyperic.hq.common.server.session.ServerConfigManagerEJBImpl as SMan
import org.hyperic.hq.hqapi1.ErrorCode

class ServerconfigController extends ApiController {

    private _serverMan = SMan.one
        
    def getConfig(params) {

        def props = _serverMan.config

        renderXml() {
            ServerConfigsResponse() {
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

        def failureXml = null

        if (!user.isSuperUser()) {
            failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED,
                                       "User " + user.name + " is not superuser")
        } else {

            Properties props = new Properties()
            def postData = new XmlParser().parseText(getPostData())
            for (xmlConfig in postData['ServerConfig']) {
                props.put(xmlConfig.'@key', xmlConfig.'@value')
            }

            try {
                _serverMan.setConfig(user, props)
            } catch (Exception e) {
                failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                           e.getMessage())
            }
        }

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