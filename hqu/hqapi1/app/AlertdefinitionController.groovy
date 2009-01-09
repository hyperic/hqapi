
import org.hyperic.hq.hqapi1.ErrorCode

import org.hyperic.hq.events.AlertSeverity
import org.hyperic.hq.measurement.shared.ResourceLogEvent
import org.hyperic.hq.events.EventConstants

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
                d.conditions.each { c ->
                    if (c.type == EventConstants.TYPE_THRESHOLD) {
                        def metric = getTemplate(c.measurementId, d.typeBased)
                        if (!metric) {
                            log.warn "Unable to find metric " + c.measurementId +
                                     "for definition " + d.name
                        } else {
                            AlertConditionThresholdCondition(required: c.required,
                                                             metric: metric.name,
                                                             comparator: c.comparator,
                                                             absolute: c.threshold)
                        }
                    } else if (c.type == EventConstants.TYPE_BASELINE) {
                        def metric = getTemplate(c.measurementId, d.typeBased)
                        if (!metric) {
                            log.warn "Unable to find metric " + c.measurementId +
                                     "for definition " + d.name
                        } else {
                            AlertConditionBaselineCondition(required: c.required,
                                                            metric: metric.name,
                                                            comparator: c.comparator,
                                                            percentage: c.threshold,
                                                            baseline: c.optionStatus)
                        }
                    } else if (c.type == EventConstants.TYPE_CHANGE) {
                        def metric = getTemplate(c.measurementId, d.typeBased)
                        if (!metric) {
                            log.warn "Unable to find metric " + c.measurementId +
                                     "for definition " + d.name
                        } else {
                            AlertConditionChangeCondition(required: c.required,
                                                          metric: metric.name)
                        }
                    } else if (c.type == EventConstants.TYPE_CUST_PROP) {
                        AlertConditionPropertyCondition(required: c.required,
                                                        property: c.name)
                    } else if (c.type == EventConstants.TYPE_LOG) {
                        int level = c.name.toInteger()
                        AlertConditionLogCondition(required: c.required,
                                                   level: ResourceLogEvent.getLevelString(level),
                                       matches: c.optionStatus)
                    } else if (c.type == EventConstants.TYPE_ALERT) {
                        def alert = alertHelper.getById(c.measurementId)
                        if (alert == null) {
                            log.warn("Unable to find recover condition " +
                                     c.measurementId + " for " + c.name)
                        } else {
                            AlertConditionRecoveryCondition(required: c.required,
                                                            recover: alert?.name)
                        }
                    } else if (c.type == EventConstants.TYPE_CFG_CHG) {
                            AlertConditionConfigCondition(required: c.required,
                                                          match: c.optionStatus)
                    } else {
                        log.warn("Unhandled condition type " + c.type +
                                 " for condition " + c.name)
                    }
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
}