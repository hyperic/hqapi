import org.hyperic.hq.hqu.rendit.BaseController

import org.hyperic.hq.hqapi1.ErrorCode;

class MetricController extends ApiController {

    private Closure getMetricXML(m) {
        { doc -> 
            Metric(id             : m.id,
                   interval       : m.interval,
                   enabled        : m.enabled,
                   name           : m.template.name,
                   defalutOn      : m.template.defaultOn,
                   indicator      : m.template.designate,
                   collectionType : m.template.collectionType) {
            MetricTemplate(id              : m.template.id,
                           name            : m.template.name,
                           alias           : m.template.alias,
                           units           : m.template.units,
                           plugin          : m.template.plugin,
                           indicator       : m.template.designate,
                           defaultOn       : m.template.defaultOn,
                           collectionType  : m.template.collectionType,
                           defaultInterval : m.template.defaultInterval)
            }
        }
    }

        private Closure getMetricTemplateXML(t) {
        { doc -> 
            MetricTemplate(id              : t.id,
                           name            : t.name,
                           alias           : t.alias,
                           units           : t.units,
                           plugin          : t.plugin,
                           indicator       : t.designate,
                           defaultOn       : t.defaultOn,
                           collectionType  : t.collectionType,
                           defaultInterval : t.defaultInterval)
        }
    }

    private Closure getMetricDataXML(d) {
        { doc ->
            MetricData(timestamp : d.timestamp,
                       value     : d.value)
        }
    }

    private validInterval(long interval) {
        return interval > 0 && interval%60000 == 0
    }

    def disableMetric(params) {
        def failureXml
        def metric
        def metricId = params.getOne("id")?.toInteger()
        if (!metricId) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            metric = metricHelper.findMeasurementById(metricId)
            if (!metric) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            } else {
                try {
                    metric.disableMeasurement(user)
                } catch (Exception e) {
                    log.error("UnexpectedError: " + e.getMessage(), e)
                    failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
                }
            }
        }

        renderXml() {
            DisableMetricResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def listTemplates(params) {
        def prototype = params.getOne("prototype")

        def failureXml = null
        def templates
        if (!prototype) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            // Make sure the prototype exists.
            def proto = resourceHelper.find(prototype: prototype)
            if (!proto) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            } else {
                templates = metricHelper.find(all:'templates',
                                              resourceType: prototype)
            }
        }

        renderXml() {
            ListMetricTemplatesResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (t in templates) {
                        out << getMetricTemplateXML(t)
                    }
                }
            }
        }
    }

    def listMetrics(params) {
        def failureXml
        def metrics
        def resourceId = params.getOne("resourceId")?.toInteger()
        def enabled = params.getOne("enabled")?.toBoolean()

        if (!resourceId) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            def res = getResource(resourceId)
            if (!res) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            } else {
                try {
                    if (enabled != null && enabled) {
                        metrics = res.enabledMetrics
                    } else {
                        metrics = res.metrics
                    }                    
                } catch (Exception e) {
                    log.error("UnexpectedError: " + e.getMessage(), e)
                    failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
                }
            }
        }

        renderXml() {
            ListMetricsResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (m in metrics) {
                        out << getMetricXML(m)
                    }
                }
            }
        }
    }

    def getMetric(params) {
        def failureXml
        def metric
        def metricId = params.getOne("id")?.toInteger()

        if (!metricId) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            try {
                metric = metricHelper.findMeasurementById(metricId);
                if (!metric) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                }
            } catch (Exception e) {
                log.error("UnexpectedError: " + e.getMessage(), e)
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
            }
        }
        
        renderXml() {
            GetMetricResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    out << getMetricXML(metric)
                }
            }
        }
    }

    def enableMetric(params) {
        def failureXml = null
        def metricId = params.getOne("id")?.toInteger()
        def interval = params.getOne("interval")?.toLong()

        if (!metricId || !interval) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            if (!validInterval(interval)) {
                failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
            } else {
                def metric = metricHelper.findMeasurementById(metricId)
                if (!metric) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                } else {
                    try {
                        metric.enableMeasurement(user, interval)
                    } catch (Exception e) {
                        log.error("UnexpectedError: " + e.getMessage(), e)
                        failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
                    }
                }
            }
        }

        renderXml() {
            EnableMetricResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def setInterval(params) {
        def failureXml = null
        def metricId = params.getOne("id")?.toInteger()
        def interval = params.getOne("interval")?.toInteger()

        if (!metricId || !interval) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            if (!validInterval(interval)) {
                failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
           } else {
                def metric = metricHelper.findMeasurementById(metricId)
                if (!metric) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                } else {
                    try {
                        metric.updateMeasurementInterval(user, interval)
                    } catch (Exception e) {
                        log.error("UnexpectedError: " + e.getMessage(), e)
                        failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
                    }
                }
            }
        }

        renderXml() {
            SetMetricIntervalResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def setDefaultOn(params) {
        def failureXml
        def templateId = params.getOne("templateId")?.toInteger()
        def on = params.getOne("on")?.toBoolean()

        if (!templateId) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            def template = metricHelper.findTemplateById(templateId)
            if (!template) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            } else {
                try {
                    template.setDefaultOn(user, on)
                } catch (Exception e) {
                    log.error("UnexpectedError: " + e.getMessage(), e)
                    failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
                }
            }
        }
        
        renderXml() {
            SetMetricDefaultOnResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def setDefaultIndicator(params) {
        def failureXml
        def templateId = params.getOne("templateId")?.toInteger()
        def on = params.getOne("on")?.toBoolean()

        if (!templateId) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            def template = metricHelper.findTemplateById(templateId)
            if (!template) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            } else {
                try {
                    template.setDefaultIndicator(user, on)
                } catch (Exception e) {
                    log.error("UnexpectedError: " + e.getMessage(), e)
                    failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
                }
            }
        }

        renderXml() {
            SetMetricDefaultIndicatorResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def setDefaultInterval(params) {
        def failureXml = null
        def templateId = params.getOne("templateId")?.toInteger()
        def interval = params.getOne("interval")?.toInteger()

        if (!templateId || !interval) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        } else {
            if (!validInterval(interval)) {
                failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
            } else {
                def template = metricHelper.findTemplateById(templateId)
                if (!template) {
                    failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                } else {
                    try {
                        template.setDefaultInterval(user, interval)
                    } catch (Exception e) {
                        log.error("UnexpectedError: " + e.getMessage(), e)
                        failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
                    }
                }
            }
        }
        
        renderXml() {
            SetMetricDefaultIntervalResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def getData(params) {
        def metricId = params.getOne("metricId")?.toInteger()
        def start = params.getOne("start")?.toLong()
        def end = params.getOne("end")?.toLong()

        def failureXml = null
        if (!metricId || !start || !end) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        }

        if (end < start) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        }

        def metric = metricHelper.findMeasurementById(metricId)
        if (!metric) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
        }

        def data;
        if (!failureXml) {
            try {
                data = metric.getData(start, end)
            } catch (Exception e) {
                log.error("UnexpectedError: " + e.getMessage(), e);
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
            }
        }

        renderXml() {
            GetMetricDataResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    for (dp in data) {
                        out << getMetricDataXML(dp) 
                    }
                }
            }
        }
    }

    def getGroupData(params) {
        def groupId = params.getOne("groupId")?.toInteger()
        def templateId = params.getOne("templateId")?.toInteger()
        def start = params.getOne("start")?.toLong()
        def end = params.getOne("end")?.toLong()

        def failureXml = null

        if (!groupId || !templateId || !start || !end) {
            failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS)
        }

        renderXml() {
            GetMetricsDataResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getFailureXML(ErrorCode.NOT_IMPLEMENTED)
                }
            }
        }
    }
}
