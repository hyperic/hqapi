/* Copyright 2009 SpringSource Inc. All Rights Reserved. */

package org.hyperic.hq.hqapi1;

import java.io.IOException;

import org.apache.commons.httpclient.HttpMethodBase;
import org.hyperic.hq.hqapi1.types.ServiceError;

/**
 * Handles responses from Web Service calls
 * @author Jennifer Hickey
 * @author Greg Turnquist
 *
 */
public interface ResponseHandler<T> {

	/**
	 * Generate an response object with the given Error. In some cases the HQ
	 * server will not give us a result, so we generate one ourselves. XXX: It
	 * would be nice here if we could get JAXB to generate an interface for all
	 * response objects so we don't need to use reflection here.
     *
	 * @param error
     *            The ServiceError to include in the response
	 * @return A response object of the given type with the given service error.
	 * @throws IOException
	 *             If an error occurs generating the error object.
	 */
	 T getErrorResponse(ServiceError error)
		throws IOException;
	/**
	 * Handles the method response and creates a response object of the given class
	 *
	 * @param responseCode The status code from the Method execution
	 * @param method The method that was executed
	 * @return A response object of the given type
	 * @throws IOException If there is an error handling the response.
	 */
	T handleResponse(int responseCode, HttpMethodBase method) throws IOException;
}
