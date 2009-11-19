import org.hyperic.hq.hqapi1.ErrorCode
import org.hyperic.hq.appdef.server.session.ApplicationManagerEJBImpl as AppMan
import org.hyperic.hq.bizapp.server.session.AppdefBossEJBImpl as ABoss
import org.hyperic.hq.authz.server.session.ResourceManagerEJBImpl as ResMan
import org.hyperic.util.pager.PageControl
import org.hyperic.hq.auth.shared.SessionManager
import org.hyperic.hq.appdef.shared.AppdefEntityID
import org.hyperic.hq.appdef.shared.ApplicationValue
import org.hyperic.hq.appdef.shared.ServiceValue
import org.hyperic.dao.DAOFactory
import org.hyperic.hq.appdef.server.session.AppServiceDAO
import org.hyperic.hq.appdef.shared.AppdefDuplicateNameException;

class ApplicationController extends ApiController {

    def appMan = AppMan.one
    def aBoss  = ABoss.one
    def resMan = ResMan.one

    def failureXml = null

    private Closure getApplicationXML(a) {
        { doc ->
            Application(id          : a.id,
                        name        : a.name,
                        location    : a.location,
                        description : a.description,
                        engContact  : a.engContact,
                        opsContact  : a.opsContact,
                        bizContact  : a.businessContact) {
                def sessionId = SessionManager.instance.put(user)
                def resHelper = resourceHelper
                def legacy = true
                def inventory = aBoss.findServiceInventoryByApplication(sessionId, a.id, PageControl.PAGE_ALL)
                
                for (appService in inventory) {
                    if (appService instanceof ServiceValue) {
                    	if (appService.metaClass.respondsTo(appService, "getResourceId")) {
                    		legacy = false
                    	}
                    	break
                    }
                }
                
                for (appService in inventory) {
                    if (appService instanceof ServiceValue) {
                        def resourceId = null
                        if (legacy) {
                        	def resource = resHelper.find('service':appService.id)
                        	resourceId = resource.id
                        } else {
                        	resourceId = appService.resourceId
                        }
                        Resource(id :          resourceId,
                                 name :        appService.name,
                                 description : appService.description)
                    }
                }
            }
        }
    }

    def list(params) {
        renderXml() {
            out << ApplicationsResponse() {
                out << getSuccessXML()
                for (app in appMan.getAllApplications(user, PageControl.PAGE_ALL)) {
                    out << getApplicationXML(app)
                }
            }
        }
    }

    /**
     * Validate <Resource> XML within an Application to ensure all passed
     * resources are service types.
     * @return true if Resources are valid, false otherwise.
     */
    private validateApplicationServices(xmlApplication) {
        for (xmlResource in xmlApplication['Resource']) {
            def rid = xmlResource.'@id'?.toInteger()
            def resource = resourceHelper.findById(rid)
            if (!resource.isService()) {
                failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                           "Invalid resource passed to create, " +
                                           resource.name + " is not a service")
                return false
            }
        }
        return true
    }

    /**
     * Create an Application via XML.
     *
     * @return the Created application or null if Application creation
     * failed.  In that case the caller should use failureXml to determine
     * the cause.
     */
    private createApplication(xmlApplication) {
        if (!validateApplicationServices(xmlApplication)) {
            return null
        }

        def appName = xmlApplication.'@name'
        def appLoc  = xmlApplication.'@location'
        def appDesc = xmlApplication.'@description'
        def appEng  = xmlApplication.'@engContact'
        def appOps  = xmlApplication.'@opsContact'
        def appBiz  = xmlApplication.'@bizContact'

        def applicationValue = new ApplicationValue()
        applicationValue.name            = appName
        applicationValue.location        = appLoc
        applicationValue.description     = appDesc
        applicationValue.engContact      = appEng
        applicationValue.opsContact      = appOps
        applicationValue.businessContact = appBiz

        def newApp
        try {
            applicationValue.applicationType = appMan.findApplicationType(1)
            newApp = appMan.createApplication(user, applicationValue, new ArrayList())
            // Initialize appServices to avoid NPE
            newApp.appServices = new ArrayList()
        } catch (AppdefDuplicateNameException e) {
            failureXml =  getFailureXML(ErrorCode.OBJECT_EXISTS,
                                        "Existing application with name " + appName +
                                        "already exists.")
            return null
        } catch (Exception e) {
            log.error("Error creating application", e)
            failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
                                       "Error creating application: " + e.message)
            return null
        }

        def resources = xmlApplication['Resource']
        updateAppServices(newApp, resources)
        return newApp
    }

    def create(params) {
        def createRequest = new XmlParser().parseText(getUpload('postdata'))
        def xmlApplication = createRequest['Application']

        def newApp
        if (!xmlApplication || xmlApplication.size() != 1) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Wrong number of Applications")
        } else {
            newApp = createApplication(xmlApplication[0])
        }

        renderXml() {
            ApplicationResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    out << getApplicationXML(newApp.applicationValue)
                }
            }
        }
    }

    /**
     * Update an Application via XML.
     *
     * @return the Created application or null if Application creation
     * failed.  In that case the caller should use failureXml to determine
     * the cause.
     */
    private updateApplication(xmlApplication) {
        def appId = xmlApplication.'@id'?.toInteger()
        if (!appId) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "No application id found")
            return null
        }

        if (!validateApplicationServices(xmlApplication)) {
            return null
        }

        def appName = xmlApplication.'@name'
        def appLoc  = xmlApplication.'@location'
        def appDesc = xmlApplication.'@description'
        def appEng  = xmlApplication.'@engContact'
        def appOps  = xmlApplication.'@opsContact'
        def appBiz  = xmlApplication.'@bizContact'

        def updateApp
        try {
            updateApp = appMan.findApplicationById(user, appId)
        } catch (Exception e) {
            log.error("Error finding application" + e)
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                       "Unable to find application with " +
                                       "id " + appId)
            return null
        }

        def applicationValue = updateApp.getApplicationValue()
        applicationValue.name            = appName
        applicationValue.location        = appLoc
        applicationValue.description     = appDesc
        applicationValue.engContact      = appEng
        applicationValue.opsContact      = appOps
        applicationValue.businessContact = appBiz

        try {
            appMan.updateApplication(user, applicationValue)
        } catch (AppdefDuplicateNameException e) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "There is already an application named " +
                                       appName)
            return null
        } catch (Exception e) {
            log.error("Error updating application", e)
            failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
            return null
        }

        def resources = xmlApplication['Resource']
        updateAppServices(updateApp, resources)
        return getApplication(appId)
    }

    def update(params) {
        def updateRequest = new XmlParser().parseText(getUpload('postdata'))
        def xmlApplication = updateRequest['Application']

        def updatedApp
        if (!xmlApplication || xmlApplication.size() != 1) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                       "Wrong number of Applications")
        } else {
            updatedApp = updateApplication(xmlApplication[0])
        }

        renderXml() {
            ApplicationResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    out << getApplicationXML(updatedApp.applicationValue)
                }
            }
        }
    }

    def sync(params) {
        def syncRequest = new XmlParser().parseText(getUpload('postdata'))

        def applications = []
        for (xmlApplication in syncRequest['Application']) {
            def appId = xmlApplication.'@id'?.toInteger()
            if (!appId) {
                applications << createApplication(xmlApplication)
            } else {
                applications << updateApplication(xmlApplication)
            }

            if (failureXml) {
                // Break out early on errors.
                break
            }
        }

        renderXml() {
            ApplicationsResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (app in applications) {
                        out << getApplicationXML(app)
                    }
                }
            }
        }
    }

    def delete(params) {
        def id = params.getOne('id')?.toInteger()

        if (id == null) {
            renderXml() {
                out << StatusResponse() {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                }
            }
            return
        }

        def app = getApplication(id)
        if (!app) {
            renderXml() {
                out << StatusResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                }
            }
            return
        }

        try {
            appMan.removeApplication(user, id)
        } catch (Exception e) {
            renderXml() {
                log.error("Error removing application", e)
                StatusResponse() {
                    out << getFailureXML(ErrorCode.UNEXPECTED_ERROR)
                }
            }
            return
        }

        renderXml() {
            StatusResponse() {
                out << getSuccessXML()
            }
        }
    }

    private updateAppServices(app, resources) {
        def svcList = [] // List of AppdefEntityID's to add to the application

        if (resources) {
            resources.each { res ->
                def rid = res.'@id'?.toInteger()
                def sid = resMan.findResourceById(rid)?.instanceId
                def entId = AppdefEntityID.newServiceID(sid)
                if (!svcList.contains(entId)) {
                    svcList.add(entId)
                }
            }
        }

        // Setting the application services does not remove any app services
        // that may have been removed from the application.  It will also add
        // duplicates, so we need to iterate the list, first removing services
        // not present in the list, then adding the new entries.
        def sessionId = SessionManager.instance.put(user)
        def dao = new AppServiceDAO(DAOFactory.getDAOFactory());

        def svcListExisting = [] // List of AppdefEntityID's existing in the Application
        def svcListToRemove = [] // List of app service id's to remove
        for (appService in aBoss.findServiceInventoryByApplication(sessionId, app.id, PageControl.PAGE_ALL)) {
            if (appService instanceof ServiceValue) {
                def entId = AppdefEntityID.newServiceID(appService.id)
                svcListExisting << entId
                if (!svcList.contains(entId)) {
                    def appSvc = dao.findByAppAndService(app.id, appService.id)
                    svcListToRemove << appSvc.id
                }
            }
        }

        def toAdd = svcList - svcListExisting
        appMan.setApplicationServices(user, app.id, toAdd)

        // Remove all deleted services
        for (appSvcId in svcListToRemove) {
            appMan.removeAppService(user, app.id, appSvcId)
        }
    }

    private getApplication(id) {
        try {
            return appMan.findApplicationById(user, id)
        } catch (Exception e) {
            return null
        }
    }
}
