
import org.hyperic.hq.hqapi1.ErrorCode

import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.events.AlertSeverity
import org.hyperic.hq.events.EventConstants
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
                    Resource(id : d.resource.id,
                             name : d.resource.name,
                             description : d.resource.description)                    
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
}