import org.hyperic.hq.hqapi1.ErrorCode;
import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.grouping.shared.GroupNotCompatibleException
import org.hyperic.util.pager.PageControl

class ControlController extends ApiController {

    private Closure getHistoryXML(h) {
        { doc ->
            ControlHistory(scheduled:     h.scheduled,
                           dateScheduled: h.dateScheduled,
                           endTime:       h.endTime,
                           startTime:     h.startTime,
                           status:        h.status,
                           message:       h.message,
                           description:   h.description,
                           args:          h.args,
                           action:        h.action)
        }
    }

    private Closure getActionXML(action) {
        { doc ->
            Action(action)
        }
    }

    def actions(params) {
        def failureXml = null
        def resourceId = params.getOne('resourceId')?.toInteger()

        def resource = getResource(resourceId)

        def actions = []
        if (!resource) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                       "Resource id " + resourceId + " not found")
        } else {
            try {
            	actions = resource.getControlActions(user)
            } catch (PermissionException e) {
                failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
            }
        }

        renderXml() {
            out << ControlActionResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    // TODO: If resource has no actions should there be an error?
                    out << getSuccessXML()
                    for (action in actions.sort {a, b -> a <=> b}) {
                        out << getActionXML(action)
                    }
                }
            }
        }
    }

    def history(params) {
        def failureXml = null
        def resourceId = params.getOne('resourceId')?.toInteger()

        def resource = getResource(resourceId)
        def history = []
        if (!resource) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                       "Resource id " + resourceId + " not found")
        } else {
            try {
            	history = resource.getControlHistory(user)
            } catch (PermissionException e) {
                failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
            }
        }

        renderXml() {
            out << ControlHistoryResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (h in history) {
                        out << getHistoryXML(h)
                    }
                }
            }
        }
    }

    def execute(params) {
        def failureXml = null
        def resourceId = params.getOne('resourceId')?.toInteger()
        def action = params.getOne('action')
        def arguments = params.get('arguments')?.join(",")
        if (arguments == null) {
            arguments = ""
        }
        def resource = getResource(resourceId)
        if (!resource) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                       "Resource id " + resourceId + " not found")
        } else if (!action) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "No action given")
        } else {
            try {
            	def actions = resource.getControlActions(user)
            	if (!actions.contains(action)) {
                	failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                           "Resource type " + resource.prototype.name +
                                           " does not support action " + action +
                                           ". Possible values: " + actions)
            	} else {
                    resource.runAction(user, action, arguments)
                }
            } catch (org.hyperic.hq.product.PluginException e) {
                failureXml = getFailureXML(ErrorCode.NOT_SUPPORTED,
                                           "Resource id " + resourceId +
                                           " does not support action " + action)
            } catch (PermissionException e) {
                failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
            } catch (GroupNotCompatibleException e) {
                failureXml = getFailureXML(ErrorCode.NOT_SUPPORTED,
                                           "Control actions not supported for mixed groups")
            } catch (Exception e) {
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
                                           "Unexpected error: " + e.getMessage())
            }
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

