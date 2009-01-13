
import org.hyperic.hq.hqapi1.ErrorCode

import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.appdef.shared.AppdefEntityID
import org.hyperic.hq.events.AlertSeverity
import org.hyperic.hq.events.EventConstants
import org.hyperic.hq.events.shared.AlertConditionValue
import org.hyperic.hq.events.shared.AlertDefinitionValue
import org.hyperic.hq.measurement.shared.ResourceLogEvent

public class AlertdefinitionController extends ApiController {

    /**
     * Seems as though the measurementId column for alert conditions can
     * equal 0 (or something else not found in the DB?)
     *
     * We safely avoid any problems by returning 'Unknown' for templates
     * we can't find.
     */
    private getTemplate(int mid, typeBased) {
        if (typeBased) {
            try {
                return metricHelper.findTemplateById(mid)
            } catch (Exception e) {
                log.warn("Lookup of template id=${mid} failed", e)
            }
        }
        else {
            try {
                return metricHelper.findMeasurementById(mid).template
            } catch (Exception e) {
                log.warn("Lookup of metric id=${mid} failed", e)
            }
        }
        return null
    }

    private Closure getAlertDefinitionXML(d) {
        { out ->
            def attrs = [id: d.id,
                         name: d.name,
                         description: d.description,
                         priority: d.priority,
                         active: d.active,
                         enabled: d.enabled,
                         frequency: d.frequencyType,
                         count: d.count,
                         range: d.range,
                         willRecover: d.willRecover,
                         notifyFiltered: d.notifyFiltered,
                         controlFiltered: d.controlFiltered]

            // parent is nullable.
            if (d.parent != null) {
                attrs['parent'] = d.parent.id
            }

            AlertDefinition(attrs) {

                if (d.resource) {
                    if (d.parent != null && d.parent.id == 0) {
                        ResourcePrototype(id: d.resource.id,
                                          name: d.resource.name)
                    } else {
                        Resource(id : d.resource.id,
                                 name : d.resource.name)
                    }
                }
                if (d.escalation) {
                    Escalation(id : d.escalation.id,
                               name : d.escalation.name)
                }
                for (c in d.conditions) {
                    // Attributes common to all conditions
                    def conditionAttrs = [required: c.required,
                                          type: c.type]

                    if (c.type == EventConstants.TYPE_THRESHOLD) {
                        def metric = getTemplate(c.measurementId, d.typeBased)
                        if (!metric) {
                            log.warn("Unable to find metric " + c.measurementId +
                                     "for definition " + d.name)
                            continue
                        } else {
                            conditionAttrs["thresholdMetric"] = metric.name
                            conditionAttrs["thresholdComparator"] = c.comparator
                            conditionAttrs["thresholdValue"] = c.threshold
                        }
                    } else if (c.type == EventConstants.TYPE_BASELINE) {
                        def metric = getTemplate(c.measurementId, d.typeBased)
                        if (!metric) {
                            log.warn("Unable to find metric " + c.measurementId +
                                     "for definition " + d.name)
                            continue
                        } else {
                            conditionAttrs["baselineMetric"] = metric.name
                            conditionAttrs["baselineComparator"] = c.comparator
                            conditionAttrs["baselinePercentage"] = c.threshold
                            conditionAttrs["baselineType"] = c.optionStatus
                        }
                    } else if (c.type == EventConstants.TYPE_CHANGE) {
                        def metric = getTemplate(c.measurementId, d.typeBased)
                        if (!metric) {
                            log.warn("Unable to find metric " + c.measurementId +
                                     "for definition " + d.name)
                            continue
                        } else {
                            conditionAttrs["metricChange"] = metric.name
                        }
                    } else if (c.type == EventConstants.TYPE_CUST_PROP) {
                        conditionAttrs["property"] = c.name
                    } else if (c.type == EventConstants.TYPE_LOG) {
                        int level = c.name.toInteger()
                        conditionAttrs["logLevel"] = ResourceLogEvent.getLevelString(level)
                        conditionAttrs["logMatches"] = c.optionStatus
                    } else if (c.type == EventConstants.TYPE_ALERT) {
                        def alert = alertHelper.getById(c.measurementId)
                        if (alert == null) {
                            log.warn("Unable to find recover condition " +
                                     c.measurementId + " for " + c.name)
                            continue
                        } else {
                            conditionAttrs["recover"] = alert.name
                        }
                    } else if (c.type == EventConstants.TYPE_CFG_CHG) {
                        conditionAttrs["configMatch"] = c.optionStatus
                    } else if (c.type == EventConstants.TYPE_CONTROL) {
                        conditionAttrs["controlAction"] = c.name
                        conditionAttrs["controlStatus"] = c.optionStatus
                    } else {
                        log.warn("Unhandled condition type " + c.type +
                                 " for condition " + c.name)
                    }
                    // Write it out
                    AlertCondition(conditionAttrs)
                }
            }
        }
    }

    def listDefinitions(params) {

        def excludeTypeBased = params.getOne('excludeTypeBased')?.toBoolean()
        if (excludeTypeBased == null) {
            excludeTypeBased = false;
        }
        def definitions = alertHelper.findDefinitions(AlertSeverity.LOW, null,
                                                      excludeTypeBased)

        renderXml() {
            out << AlertDefinitionsResponse() {
                out << getSuccessXML()
                for (definition in definitions) {
                    out << getAlertDefinitionXML(definition)
                }
            }
        }
    }

    def listTypeDefinitions(params) {

        def definitions = alertHelper.findTypeBasedDefinitions()

        renderXml() {
            out << AlertDefinitionsResponse() {
                out << getSuccessXML()
                for (definition in definitions) {
                    out << getAlertDefinitionXML(definition)
                }
            }
        }
    }

    def delete(params) {
        def id   = params.getOne("id")?.toInteger()

        def alertdefinition = alertHelper.getById(id)
        def failureXml = null

        if (!alertdefinition) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                       "Alert definition with id " + id +
                                       " not found")
        } else {
            try {
                alertdefinition.delete(user)
            } catch (PermissionException e) {
                failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
            } catch (Exception e) {
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
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

    def sync(params) {
        def syncRequest = new XmlParser().parseText(getUpload('postdata'))

        for (xmlDef in syncRequest['AlertDefinition']) {
            def failureXml = null

            def resource // Can be a resource or a prototype in the case of type alerts
            boolean typeBased
            def existing = null
            Integer id = xmlDef.'@id'?.toInteger()
            if (id) {
                existing = alertHelper.getById(id)
                resource = existing.resource
            } else {
                if (xmlDef['Resource'].size() ==1 &&
                    xmlDef['ResourcePrototype'].size() == 1) {
                    failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                               "Only one of Resource or " +
                                               "ResourcePrototype required for " +
                                               xmlDef.'@name')
                } else if (xmlDef['Resource'].size() == 1) {
                    typeBased = false
                    def rid = xmlDef['Resource'][0].'@id'?.toInteger()
                    resource = getResource(rid)
                    if (!resource) {
                        failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                                   "Cannot find resource with " +
                                                   "id " + id)
                    }
                } else if (xmlDef['ResourcePrototype'].size() == 1) {
                    typeBased = true
                    def name = xmlDef['ResourcePrototype'][0].'@name'
                    resource = resourceHelper.findResourcePrototype(name)
                    if (!resource) {
                        failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                                   "Cannot find resource type " +
                                                   name + " for definition " +
                                                   xmlDef.'@name')
                    }
                } else {
                    failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                               "A single Resource or " +
                                               "ResourcePrototype is required for " +
                                               xmlDef.'@name')
                }
            }

            // Required attributes, basically everything but description
            ['controlFiltered', 'notifyFiltered', 'willRecover', 'range', 'count',
             'frequency', 'enabled', 'active', 'priority',
             'name'].each { attr ->
                if (xmlDef."@${attr}" == null) {
                    failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                              "Required attribute " + attr +
                                              " not found for definition " +
                                              xmlDef.'@name')
                }
            }

            // At least one condition is always required
            if (!xmlDef['AlertCondition'] || xmlDef['AlertCondition'].size() < 1) {
                failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                           "At least 1 AlertCondition is " +
                                           "required for definition " +
                                           xmlDef.'@name')
            }

            if (failureXml) {
                renderXml() {
                    StatusResponse() {
                        out << failureXml
                    }
                }
                return
            }

            AlertDefinitionValue adv = new AlertDefinitionValue();
            adv.id = existing?.id
            adv.name = xmlDef.'@name'
            adv.description = xmlDef.'@description'
            adv.appdefType = resource.entityId.type
            adv.appdefId = resource.entityId.id
            adv.priority = xmlDef.'@priority'?.toInteger()
            adv.enabled = xmlDef.'@enabled'.toBoolean()
            adv.active = xmlDef.'@active'.toBoolean()
            adv.willRecover = xmlDef.'@willRecover'.toBoolean()
            adv.notifyFiltered = xmlDef.'@notifyFiltered'
            adv.frequencyType = xmlDef.'@frequency'
            adv.count = xmlDef.'@count'
            adv.range = xmlDef.'@range'

            for (xmlCond in alertDef['AlertCondition']) {
                AlertConditionValue acv = new AlertConditionValue()

                ['required', 'type'].each { cond ->
                    if (xmlCond."@${cond}" == null) {
                        failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                                   "Required AlertCondition " +
                                                   "attribute " + cond +
                                                   " not given for " +
                                                   adv.name)
                    }
                }

                acv.required = xmlCond.'@required'.toBoolean()
                acv.type = xmlCond.'@type'.toInteger()

                switch (acv.type) {
                    case EventConstants.TYPE_THRESHOLD:
                        break
                    case EventConstants.TYPE_BASELINE:
                        break
                    case EventConstants.TYPE_CONTROL:
                        break
                    case EventConstants.TYPE_CHANGE:
                        break
                    case EventConstants.TYPE_ALERT:
                        break
                    case EventConstants.TYPE_CUST_PROP:
                        break
                    case EventConstants.TYPE_LOG:
                        break
                    case EventConstants.TYPE_CFG_CHG:
                        break
                    default:
                        failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
                                                   "Unhandled AlertCondition " +
                                                   "type " + acv.type + " for " +
                                                   adv.name)
                }

            }
            // TODO: Save or Update
        }

        renderXml() {
            StatusResponse() {
                out << getSuccessXML()
            }
        }
    }
}