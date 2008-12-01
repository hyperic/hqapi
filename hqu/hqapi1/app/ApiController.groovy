import org.hyperic.hq.hqu.rendit.BaseController

import groovy.xml.StreamingMarkupBuilder
import org.hyperic.hq.hqapi1.ErrorCode

class ApiController extends BaseController {

    /**
     * Get the ResponseStatus Success XML.
     */
    protected Closure getSuccessXML() {
        { doc -> 
            Status("Success")
        }
    }

    /**
     * Get the ResponseStatus Failure XML.
     */
    protected Closure getFailureXML(ErrorCode code) {
        return getFailureXML(code, code.getReasonText())
    }

    /**
     * Get the ResponseStatus Failure XML with the specified reason text.
     */
    protected Closure getFailureXML(ErrorCode code, String reason) {
        { doc ->
            Status("Failure")
            Error() {
                ErrorCode(code.getErrorCode())
                ReasonText(reason)
            }
        }
    }

    /**
     * Get the XML for a User
     */
    protected Closure getUserXML(u) {
        { doc ->
            User(id          : u.id,
                 name        : u.name,
                 firstName   : u.firstName,
                 lastName    : u.lastName,
                 department  : (u.department ? u.department : ''),
                 emailAddress: u.emailAddress,
                 SMSAddress  : (u.SMSAddress ? u.SMSAddress : ''),
                 phoneNumber : (u.phoneNumber ? u.phoneNumber : ''),
                 active      : u.active,
                 htmlEmail   : u.htmlEmail)
        }
    }

    protected Closure getResourceXML(r) {
        { doc ->
            Resource(id : r.id,
                     name : r.name,
                     description : r.description) {
                r.getConfig().each { k, v ->
                    if (v.type.equals("configResponse")) {
                        ResourceConfig(key: k, value: v.value)
                    }
                }
                ResourcePrototype(id : r.prototype.id,
                                  name : r.prototype.name)
            }
        }
    }

    /**
     * Get the resource based on the given id.  If the resource is not found,
     * null is returned.
     */
    protected getResource(id) {
        def resource = resourceHelper.findById(id)

        if (!resource) {
            return null
        } else {
            //XXX: ResourceHelper needs some work here..
            try {
                resource.name // Check the object really exists
                resource.entityId // Check the object is an appdef object
                return resource
            } catch (Throwable t) {
                return null
            }
        }
    }

    /**
     * Get a User by id or name
     * @return The user by the given id.  If the passed in id is null then
     * the user by the given name is returned.  If no user could be found
     * for either the id or name, null is returned.
     */
    protected getUser(Integer id, String name) {
        if (id) {
            return userHelper.getUser(id)
        } else {
            return userHelper.findUser(name)
        }
    }
    
    def index(params) {
        render(locals:[plugin: getPlugin()])
    }
}
