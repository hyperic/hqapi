
import org.hyperic.hq.appdef.shared.ServiceManager;
import org.hyperic.hq.common.shared.ServerConfigManager;
import org.hyperic.hq.context.Bootstrap;
import org.hyperic.hq.hqapi1.ErrorCode

class ServerconfigController extends ApiController {


    private _serverMan = Bootstrap.getBean(ServerConfigManager.class)

    
    private _hiddenProps = ['CAM_SCHEMA_VERSION', 'CAM_SERVER_VERSION', 
                            'CAM_JAAS_PROVIDER', 'CAM_LDAP_NAMING_FACTORY_INITIAL',
                            'CAM_HELP_USER', 'CAM_HELP_PASSWORD',
                            'CAM_MULTICAST_ADDRESS', 'CAM_MULTICAST_PORT',
                            'CAM_SYSLOG_ACTIONS_ENABLED', 'CAM_GUIDE_ENABLED',
                            'CAM_RT_COLLECT_IP_ADDRS', 'AGENT_BUNDLE_REPOSITORY_DIR',
                            'CAM_DATA_PURGE_1H', 'CAM_DATA_PURGE_6H', 
                            'CAM_DATA_PURGE_1D', 'RT_DATA_PURGE',
                            'BATCH_AGGREGATE_BATCHSIZE', 'BATCH_AGGREGATE_QUEUE',
                            'BATCH_AGGREGATE_WORKERS', 'DATA_STORE_ALL',
                            'REPORT_STATS_SIZE', 'HQ-GUID']

        
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
                        // Filter out non-UI server configs
                        if (!_hiddenProps.contains(key)) {
                        	ServerConfig(key: key, value: props.getProperty(key))
                        }
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

            Properties props = _serverMan.config
            def update = false
            def postData = new XmlParser().parseText(getPostData())
            
            // replace current server config props with new values
            for (xmlConfig in postData['ServerConfig']) {
                def key = xmlConfig.'@key'
                if (props.containsKey(key)) {
                	if (_hiddenProps.contains(key)) {
                		failureXml = getFailureXML(ErrorCode.OPERATION_DENIED,
                							 "Cannot update configuration parameter " + key)
                		break
                	} else {
                		props.put(key, xmlConfig.'@value')
                		update = true
                	}
                } else {
                	failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                	                           "Unknown configuration parameter " + key)
                	break
                }
            }

            try {
                if (!failureXml) {
                	if (update) {
                		_serverMan.setConfig(user, props)
                	} else {
                		failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                							       "No configuration parameters updated")
                	}
                }
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