import org.hyperic.hq.hqapi1.ErrorCode
import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.authz.shared.PermissionManagerFactory
import org.hyperic.hq.authz.shared.ResourceManager
import org.hyperic.hq.context.Bootstrap
import org.hyperic.hq.events.shared.MaintenanceEventManager

class MaintenanceController extends ApiController {

    private static resMan = Bootstrap.getBean(ResourceManager.class)

    private static MaintenanceEventManager maintMan =
        PermissionManagerFactory.getInstance().getMaintenanceEventManager();

    private Closure getMaintenanceEventXML(m) {

        { doc ->
            MaintenanceEvent(state:     m.state,
            				 resourceId: resMan.findResource(m.appdefEntityID).id,
                             groupId:   m.groupId,
                             startTime: m.startTime,
                             endTime:   m.endTime,
                             modifiedBy: m.modifiedBy) {
                State(m.state)
            }
        }
    }

    def schedule(params) {
        def groupId = params.getOne("groupId")?.toInteger()
        def resourceId = params.getOne("resourceId")?.toInteger()
        def start = params.getOne("start")?.toLong()
        def end = params.getOne("end")?.toLong()

        def failureXml = null

        if (groupId == null && resourceId == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Resource or group id not given")
        }

        if (start == null || end == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Maintenance window not specified")
        }

        if (end < start) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "End time < start time")
        }

        if (start < System.currentTimeMillis()) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Start time cannot be in the past")
        }

        def result
        if (!failureXml) {
            try {
				def rez = null
				def errorMsg = null

            	if (groupId) {
            		rez = resourceHelper.findGroup(groupId)
            	} else {
            		rez = resourceHelper.findById(resourceId)
            	}
            	
            	if (rez) {
                    result = rez.scheduleMaintenance(user, start, end)           	
            	} else {            		
            		if (groupId) {
            			errorMsg = "Group with id " + groupId + " not found"
            		} else {
            			errorMsg = "Resource with id " + resourceId + " not found"
            		}
            		
                	failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND, errorMsg)
            	}
            } catch (UnsupportedOperationException e) {
                failureXml = getFailureXML(ErrorCode.NOT_SUPPORTED)
            } catch (PermissionException e) {
                failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
            } catch (Exception e) {
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
                                           e.getMessage())
            }
        }

        renderXml() {
            MaintenanceResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    out << getMaintenanceEventXML(result)
                }
            }
        }
    }

    def unschedule(params) {
        def groupId = params.getOne("groupId")?.toInteger()
        def resourceId = params.getOne("resourceId")?.toInteger()

        def failureXml = null

        if (groupId == null && resourceId == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Resource or group id not given")
        }

        if (!failureXml) {
            try {
				def rez = null
				def errorMsg = null

            	if (groupId) {
            		rez = resourceHelper.findGroup(groupId)
            	} else {
            		rez = resourceHelper.findById(resourceId)
            	}
            	
            	if (rez) {
                    rez.unscheduleMaintenance(user)            	
            	} else {            		
            		if (groupId) {
            			errorMsg = "Group with id " + groupId + " not found"
            		} else {
            			errorMsg = "Resource with id " + resourceId + " not found"
            		}
            		
                	failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND, errorMsg)
            	}
            } catch (UnsupportedOperationException e) {
                failureXml = getFailureXML(ErrorCode.NOT_SUPPORTED)
            } catch (PermissionException e) {
                failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
            } catch (Exception e) {
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
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
    
    def getAll(params) {
    	def state = params.getOne("state")
    	def failureXml = null
    	
    	def maintSchedules
    	try {
    		maintSchedules = maintMan.getMaintenanceEvents(user, state)
        } catch (UnsupportedOperationException e) {
            failureXml = getFailureXML(ErrorCode.NOT_SUPPORTED)
        } catch (PermissionException e) {
            failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
        } catch (Exception e) {
            failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
                                       e.getMessage())
        }
    	
        renderXml() {
            out << MaintenancesResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (m in maintSchedules) {
                        out << getMaintenanceEventXML(m)
                    }
                }
            }
        }
    }

    def get(params) {
        def groupId = params.getOne("groupId")?.toInteger()
        def resourceId = params.getOne("resourceId")?.toInteger()

        def failureXml = null

        if (groupId == null && resourceId == null) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Resource or group id not given")
        }

        def result
        if (!failureXml) {
            try {
				def rez = null
				def errorMsg = null

            	if (groupId) {
            		rez = resourceHelper.findGroup(groupId)
            	} else {
            		rez = resourceHelper.findById(resourceId)
            	}
            	
            	if (rez) {
                    result = rez.getMaintenanceEvent(user)            	
            	} else {            		
            		if (groupId) {
            			errorMsg = "Group with id " + groupId + " not found"
            		} else {
            			errorMsg = "Resource with id " + resourceId + " not found"
            		}
            		
                	failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND, errorMsg)
            	}
            } catch (UnsupportedOperationException e) {
                failureXml = getFailureXML(ErrorCode.NOT_SUPPORTED)
            } catch (PermissionException e) {
                failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
            } catch (Exception e) {
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
                                           e.getMessage())
            }
        }

        renderXml() {
            MaintenanceResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    if (result) {
                        out << getMaintenanceEventXML(result)
                    }
                }
            }
        }
    }
}