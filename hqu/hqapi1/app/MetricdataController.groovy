import org.hyperic.hq.hqapi1.ErrorCode;

import org.hyperic.hq.measurement.server.session.MeasurementStartupListener as MListener
import org.hyperic.hq.measurement.server.session.DataPoint as DP

class MetricdataController extends ApiController {

    def put(params) {
        def inserter = null
        def failureXml = null

        def dataRequest = new XmlParser().parseText(getPostData())
        def metricId = dataRequest.'@metricId'?.toInteger()

        def metric = metricHelper.findMeasurementById(metricId)
        if (!metric) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND,
                                       "Unable to find metric with id = " +
                                       metricId)
        } else {
            try {
            	def points = []
            	for (dp in dataRequest["DataPoint"]) {
                	long ts = dp.'@timestamp'?.toLong()
                	double val = dp.'@value'?.toDouble()
                	points << createDataPoint(metric, val, ts)
            	}
            	log.info("Inserting " + points.size() + " metrics for " + metric.template.name)

            	if (metric.getTemplate().isAvailability()) {
            		inserter = MListener.availDataInserter
            	} else {
                	inserter = MListener.dataInserter
                }
                inserter.insertMetrics(points)
            } catch (IllegalArgumentException ia) {
            	failureXml = getFailureXML(ErrorCode.INVALID_PARAMETERS,
            							   ia.getMessage()) 
            } catch (Exception e) {
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR,
                                           "Error inserting metrics: " +
                                           e.getMessage())
                log.warn("Error inserting metrics", e)
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
    
    private createDataPoint(metric, value, timestamp) {
    	if (metric.getTemplate().isAvailability()) {
    		if (value != 0.0 && value != 1.0 && value != -0.01) {
    			throw new IllegalArgumentException("Invalid availability data point: " + value)
    		}
    	}
    	
    	return new DP(metric.id, value, timestamp)
    }
}