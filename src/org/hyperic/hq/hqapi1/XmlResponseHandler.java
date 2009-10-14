/* Copyright 2009 SpringSource Inc. All Rights Reserved. */

package org.hyperic.hq.hqapi1;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.ServiceError;

/**
 * Implementation of {@link ResponseHandler} that uses JAXB to convert a method
 * response body from XML to a java response object of a specified type
 * 
 * @author Jennifer Hickey
 * 
 */
public class XmlResponseHandler implements ResponseHandler {

	private static Log _log = LogFactory.getLog(XmlResponseHandler.class);

	public <T> T handleResponse(int responseCode, HttpMethodBase method,
			Class<T> resultClass) throws IOException {
	    
	    ServiceError error;
	    switch (responseCode) {
            case 200:
                // We only deal with HTTP_OK responses
                InputStream is = method.getResponseBodyAsStream();
                try {
                    return XmlUtil.deserialize(resultClass, is);
                } catch (JAXBException e) {
                    error = new ServiceError();
                    error.setErrorCode("UnexpectedError");
                    error.setReasonText("Unable to deserialize result");
                    if (_log.isDebugEnabled()) {
                        _log.debug("Unable to deserialize result", e);
                    }
                    return getErrorResponse(resultClass, error);
                }
            case 401:
                // Unauthorized
                error = new ServiceError();
                error.setErrorCode("LoginFailure");
                error.setReasonText("The given username and password could " +
                                    "not be validated");
                return getErrorResponse(resultClass, error);
            default:
                // Some other server blow up.
                error = new ServiceError();
                error.setErrorCode("UnexpectedError");
                error.setReasonText("An unexpected error occured");
                return getErrorResponse(resultClass, error);
        }
		
	
	}
	
	 /**
     * Generate an response object with the given Error.  In some cases the
     * HQ server will not give us a result, so we generate one ourselves.
     * XXX: It would be nice here if we could get JAXB to generate an
     *      interface for all response objects so we don't need to use
     *      reflection here.
     * 
     * @param res The return Class
     * @param error The ServiceError to include in the response
     * @return A response object of the given type with the given service error.
     * @throws IOException If an error occurs generating the error object.
     */
    public <T> T getErrorResponse(Class<T> res, ServiceError error)
        throws IOException
    {
        try {
            T ret = res.newInstance();

            Method setResponse = res.getMethod("setStatus", ResponseStatus.class);
            setResponse.invoke(ret, ResponseStatus.FAILURE);

            Method setError = res.getMethod("setError", ServiceError.class);
            setError.invoke(ret, error);

            return ret;
        } catch (Exception e) {
            // This shouldn't happen unless programmer error.  For instance,
            // a result object not containing a Status or Error field.
            if (_log.isDebugEnabled()) {
                _log.debug("Error generating error response", e);
            }

            throw new IOException("Error generating Error response");
        }
    }



}
