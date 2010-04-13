package org.hyperic.hq.hqapi1;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface Connection {
    
    <T> T doGet(String path, Map<String, String[]> params, File targetFile, ResponseHandler<T> responseHandler) 
        throws IOException;

    /**
     * Issue a GET against the API.
     * 
     * @param path
     *            The web service endpoint.
     * @param params
     *            A Map of key value pairs that are converted into query
     *            arguments.
     * @param responseHandler
     *            The @{link ResponseHandler} that will process this response.
     * @return The response object from the operation. This response will be of
     *         the type given in the resultClass argument.
     * @throws IOException
     *             If a network error occurs during the request.
     */
    <T> T doGet(String path, Map<String, String[]> params, ResponseHandler<T> responseHandler)
            throws IOException;

    /**
     * Issue a POST against the API.
     * 
     * @param path
     *            The web service endpoint
     * @param params
     *            A Map of key value pairs that are converted into query
     *            arguments.
     * @param responseHandler
     *            The @{link ResponseHandler} that will process this response.
     * @return The response object from the operation. This response will be of
     *         the type given in the resultClass argument.
     * @throws IOException
     *             If a network error occurs during the request.
     */
    <T> T doPost(String path, Map<String, String[]> params, ResponseHandler<T> responseHandler)
           throws IOException;

    /**
     * Issue a POST against the API.
     * 
     * @param path
     *            The web service endpoint
     * @param params
     *            A Map of key value pairs that are added to the post data
     * @param file
     *            The file to post
     * @param responseHandler
     *            The @{link ResponseHandler} that will process this response.
     * @return The response object from the operation. This response will be of
     *         the type given in the resultClass argument.
     * @throws IOException
     *             If a network error occurs during the request.
     */
    <T> T doPost(String path, Map<String, String> params, File file,
                 ResponseHandler<T> responseHandler) throws IOException;

    /**
     * Issue a POST against the API.
     * 
     * @param path
     *            The web service endpoint
     * @param o
     *            The object to POST. This object will be serialized into XML
     *            prior to being sent.
     * @param responseHandler
     *            The @{link ResponseHandler} that will process this response.
     * @return The response object from the operation. This response will be of
     *         the type given in the resultClass argument.
     * @throws IOException
     *             If a network error occurs during the request.
     */
   <T> T doPost(String path, Object o, ResponseHandler<T> responseHandler)
            throws IOException;
}
