
import org.hyperic.hq.appdef.shared.ServiceManager;
import org.hyperic.hq.common.shared.ServerConfigManager;
import org.hyperic.hq.context.Bootstrap;
import org.hyperic.hq.hqapi1.ErrorCode

class ServerconfigController extends ApiController {

    private _serverMan = Bootstrap.getBean(ServerConfigManager.class)
        
    def getConfig(params) {

        def props = _serverMan.config

        renderXml() {
            ServerConfigResponse() {
                if (!user.isSuperUser()) {
                    out << getFailureXML(ErrorCode.PERMISSION_DENIED,
                                         "User " + user.name + " is not superuser")
                } else {
                    out << getSuccessXML()
                    for (key in props.keySet().sort {a, b -> a <=> b}) {
                        ServerConfig(key: key, value: props.getProperty(key))
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