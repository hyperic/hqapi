import org.hyperic.hq.hqapi1.ErrorCode
import org.hyperic.hq.appdef.server.session.Application
import org.hyperic.hq.appdef.server.session.ApplicationType
import org.hyperic.hq.appdef.server.session.ApplicationManagerEJBImpl as AppMan
import org.hyperic.hq.bizapp.server.session.AppdefBossEJBImpl as ABoss
import org.hyperic.hq.authz.server.session.ResourceManagerEJBImpl as ResMan
import org.hyperic.hq.authz.server.session.ResourceGroupManagerEJBImpl as GroupMan
import org.hyperic.util.pager.PageControl
import org.hyperic.util.config.ConfigResponse
import org.hyperic.hq.auth.shared.SessionManager
import org.hyperic.hq.appdef.shared.AppdefGroupValue
import org.hyperic.hq.appdef.shared.AppdefEntityConstants
import org.hyperic.hq.appdef.shared.AppdefEntityID
import org.hyperic.hq.appdef.shared.AppdefResourceValue
import org.hyperic.hq.appdef.shared.ApplicationValue
import org.hyperic.hq.appdef.shared.AppServiceValue
import org.hyperic.hq.appdef.shared.DependencyTree
import org.hyperic.hq.appdef.shared.ServiceValue
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.authz.server.session.ResourceGroupSortField

class ApplicationController extends ApiController {

    def appMan = AppMan.one
    def aBoss = ABoss.one
    def resMan = ResMan.one
    def groupMan = GroupMan.one

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
                for (appService in aBoss.findServiceInventoryByApplication(sessionId, a.id, PageControl.PAGE_ALL)) {
                    if (appService instanceof ServiceValue) {
                        def resource = resourceHelper.find('service':appService.id)
                        Resource(id :          resource.id,
                                 name :        resource.name,
                                 description : resource.description)
                    } else {
                        Group(id :          appService.id,
                              name :        appService.name,
                              description : appService.description,
                              location :    appService.location)
                    }
                }
            }
        }
    }

    def list(params) {
        def failureXml = null

        renderXml() {
            out << ApplicationsResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (app in appMan.getAllApplications(user, PageControl.PAGE_ALL)) {
                        out << getApplicationXML(app)
                    }
                }
            }
        }
    }

    def create(params) {
        def createRequest = new XmlParser().parseText(getUpload('postdata'))
        def xmlApplication = createRequest['Application']

        if (!xmlApplication || xmlApplication.size() != 1) {
            renderXml() {
                ApplicationResponse() {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                }
            }
            return
        }

        def appName = xmlApplication[0].'@name'
        def appLoc = xmlApplication[0].'@location'
        def appDesc = xmlApplication[0].'@description'
        def appEng = xmlApplication[0].'@engContact'
        def appOps = xmlApplication[0].'@opsContact'
        def appBiz = xmlApplication[0].'@bizContact'

        def applicationValue = new ApplicationValue()
        applicationValue.name = appName
        applicationValue.location = appLoc
        applicationValue.description = appDesc
        applicationValue.engContact = appEng
        applicationValue.opsContact = appOps
        applicationValue.businessContact = appBiz

        Application newApp = null;

        try {
            applicationValue.applicationType = appMan.findApplicationType(1)
            newApp = appMan.createApplication( user, applicationValue, new ArrayList()) 
            //def sessionId = SessionManager.instance.put(user)
            //def newAppValue = aBoss.createApplication( sessionId, applicationValue, new ArrayList(), new ConfigResponse())
            //newApp = appMan.findApplicationById(user, newAppValue.id)
            // had to initialize appServices to avoid NPE
            newApp.appServices = new ArrayList() 
        } catch (Exception e) {
            renderXml() {
                log.error("Error creating application", e)
                ApplicationResponse() {
                    out << getFailureXML(ErrorCode.UNEXPECTED_ERROR)
                }
            }
            return
        }

        def resource = xmlApplication['Resource']
        if (resource) {
            updateAppServices(newApp, resource)
        }
        def group = xmlApplication['Group']
        if (group) {
            updateAppGroups(newApp, group)
        }

        renderXml() {
            ApplicationResponse() {
                out << getSuccessXML()
                out << getApplicationXML(newApp.applicationValue)
            }
        }
    }

    def update(params) {
        def updateRequest = new XmlParser().parseText(getUpload('postdata'))
        def xmlApplication = updateRequest['Application']

        if (!xmlApplication || xmlApplication.size() != 1) {
            renderXml() {
                ApplicationResponse() {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                }
            }
            return
        }

        def appId = xmlApplication[0].'@id'?.toInteger()
        if (!appId) {
            renderXml() {
                ApplicationResponse() {
                    out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                }
            }
            return
        }

        def appName = xmlApplication[0].'@name'
        def appLoc = xmlApplication[0].'@location'
        def appDesc = xmlApplication[0].'@description'
        def appEng = xmlApplication[0].'@engContact'
        def appOps = xmlApplication[0].'@opsContact'
        def appBiz = xmlApplication[0].'@bizContact'

        def updateApp = null
        try {
            updateApp = appMan.findApplicationById(user, appId)
        } catch (Exception e) {
            log.error("ERROR: " + e)
            renderXml() {
                ApplicationResponse() {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                }
            }
            return
        }
    
        def applicationValue = updateApp.getApplicationValue()

        applicationValue.name = appName
        applicationValue.location = appLoc
        applicationValue.description = appDesc
        applicationValue.engContact = appEng
        applicationValue.opsContact = appOps
        applicationValue.businessContact = appBiz

        def retAppValue = null;

        try {
            retAppValue = appMan.updateApplication(user, applicationValue) 
        } catch (Exception e) {
            renderXml() {
                log.error("Error updating application", e)
                ApplicationResponse() {
                    out << getFailureXML(ErrorCode.UNEXPECTED_ERROR)
                }
            }
            return
        }

        def resource = xmlApplication['Resource']
        if (resource) {
            updateAppServices(updateApp, resource)
        }
        def group = xmlApplication['Group']
        if (group) {
            updateAppGroups(updateApp, group)
        }

        renderXml() {
            ApplicationResponse() {
                out << getSuccessXML()
                out << getApplicationXML(retAppValue)
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
        def failureXml = null

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
        def svcList = new ArrayList()
        resources.each { res ->
            def rid = res.'@id'?.toInteger()
            def sid = resMan.findResourceById(rid)?.instanceId
            def svcAeid = AppdefEntityID.newServiceID(sid)
            svcList.add(svcAeid)
        }
        
        appMan.setApplicationServices(user, app.id, svcList)
              
    }

    private updateAppGroups(app, groups) {
        def groupIds = new ArrayList()
        groups.each { grp ->
            def gid = grp.'@id'?.toInteger()
            groupIds.add(gid)
        }

        def appRes = resMan.findResourceById(app.resource.id)

        // find groups containing the application
        def allPi = PageInfo.create(PageControl.PAGE_ALL, ResourceGroupSortField.NAME)
        def containing = groupMan.findGroupsContaining(user, appRes, [], allPi)
        def removeList = new ArrayList()
        containing.each { contain -> 
            if (groupIds.contains(contain.id)) {
                // remove from list of ids since it is already part of the group
                def ix = groupIds.indexOf(contain.id)
                if (ix >=0) {
                    groupIds.remove(ix)
                }
            }
            else {
                removeList.add(contain)
            }
        }

        // remove the application from old groups
        removeList.each { group ->
            groupMan.removeResources(user, group, [appRes])
        }

        // add app to new groups that don't already contain the app
        groupIds.each { id ->
            def group = groupMan.findResourceGroupById(id)
            groupMan.addResource(user, group, appRes)
        }
    }

    private getApplication(id) {
        try {
            return appMan.findApplicationById(user, id)
        }
        catch (Exception e) {
            return null
        }
    }
}
