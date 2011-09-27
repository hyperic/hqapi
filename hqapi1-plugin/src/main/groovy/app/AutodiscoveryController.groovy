
import org.hyperic.hq.hqapi1.ErrorCode;

class AutodiscoveryController extends ApiController {

    private Closure getAIPlatformXML(p) {
        { doc ->
            AIPlatform(id    : p.id,
                       name  : p.name,
                       fqdn  : p.fqdn) {
                       p.getAIServerValues().each { s ->
                           AIServer(id                  : s.id,
                                    name                : s.name) 
                       }
            }
        }
    }

    def getQueue(params) {
        def failureXml
        def list
        try {
            list = autodiscoveryHelper.getQueue()
        } catch (Exception e) {
            log.error("UnexpectedError: " + e.getMessage(), e);
            failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
        }

        renderXml() {
            out << QueueResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (platform in list.sort {a, b -> a.name <=> b.name}) {
                        out << getAIPlatformXML(platform)
                    }
                }
            }
        }
    }

    def approve(params) {
        def id = params.getOne('id')?.toInteger()
        def serverId = params.getOne('serverId')?.toInteger()

        def failureXml
        if (id) {
            try {
                def plat = autodiscoveryHelper.findById(id)
                if (!plat) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Platform " + id + " not found")
                } else {
                    autodiscoveryHelper.approve(plat)
                }
            } catch (Exception e) {
                log.error("UnexpectedError: " + e.getMessage(), e);
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
            }
        } else if (serverId) {
        	try {
        		def server = autodiscoveryHelper.findServerById(serverId)
        		if (!server) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                               "Server " + serverId + " not found")
        		} else {
        			autodiscoveryHelper.approve(server)
        		}
        	} catch (Exception e) {
                log.error("UnexpectedError: " + e.getMessage(), e);
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
        	}
        } else {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        }

        renderXml() {
            out << StatusResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }
}