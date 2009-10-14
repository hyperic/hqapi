/* Copyright 2009 SpringSource Inc. All Rights Reserved. */

package org.hyperic.hq.hqapi1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.ServiceError;

/**
 * Implementation of {@link ResponseHandler} responsible for retrieving the
 * contents of a file from the method response, writing to the local filesystem,
 * and returning a status response of a given type
 * 
 * @author Jennifer Hickey
 * 
 */
public class FileResponseHandler implements ResponseHandler {

	private static Log _log = LogFactory.getLog(FileResponseHandler.class);

	private final File targetFile;

	/**
	 * 
	 * @param targetFile
	 *            The local file to which the contents of the method response
	 *            body should be written
	 */
	public FileResponseHandler(File targetFile) {
		this.targetFile = targetFile;
	}

	public <T> T getErrorResponse(Class<T> res, ServiceError error)
			throws IOException {
		try {
			T ret = res.newInstance();

			Method setResponse = res.getMethod("setStatus",
					ResponseStatus.class);
			setResponse.invoke(ret, ResponseStatus.FAILURE);

			Method setError = res.getMethod("setError", ServiceError.class);
			setError.invoke(ret, error);

			return ret;
		} catch (Exception e) {
			// This shouldn't happen unless programmer error. For instance,
			// a result object not containing a Status or Error field.
			if (_log.isDebugEnabled()) {
				_log.debug("Error generating error response", e);
			}

			throw new IOException("Error generating Error response");
		}
	}
	
	private <T> T getSuccessResponse(Class<T> res) throws IOException {
		try {
			T ret = res.newInstance();

			Method setResponse = res.getMethod("setStatus",
					ResponseStatus.class);
			setResponse.invoke(ret, ResponseStatus.SUCCESS);
			return ret;
		} catch (Exception e) {
			// This shouldn't happen unless programmer error. For instance,
			// a result object not containing a Status field.
			if (_log.isDebugEnabled()) {
				_log.debug("Error generating error response", e);
			}

			throw new IOException("Error generating Error response");
		}
	}

	public <T> T handleResponse(int responseCode, HttpMethodBase method,
			Class<T> resultClass)  throws IOException {
		ServiceError error;
		switch (responseCode) {
		case 200:
			FileOutputStream fileOutputStream = null;
			InputStream in = method.getResponseBodyAsStream();
			try {
				fileOutputStream = new FileOutputStream(targetFile
						.getAbsolutePath());
				final byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					fileOutputStream.write(buf, 0, len);
				}
				return getSuccessResponse(resultClass);
			} catch (Exception e) {
				error = new ServiceError();
				error.setErrorCode("UnexpectedError");
				error.setReasonText("Unable to deserialize result");
				_log.warn("Unable to deserialize result", e);
				return getErrorResponse(resultClass, error);
			} finally {
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						_log.warn("Unable to close output stream to file: "
								+ targetFile + ".  Cause: " + e.getMessage());
					}
				}
			}
		case 401:
			// Unauthorized
			error = new ServiceError();
			error.setErrorCode("LoginFailure");
			error.setReasonText("The given username and password could "
					+ "not be validated");
			return getErrorResponse(resultClass, error);
		default:
			error = new ServiceError();
			error.setErrorCode("Unexpected Error");
			if (method.getStatusText() != null) {
				error.setReasonText(method.getStatusText());
			} else {
				error.setReasonText("An unexpected error occured");
			}
			return getErrorResponse(resultClass, error);
		}
	}

}
