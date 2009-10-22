package org.hyperic.hq.hqapi1;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface Connection {
    
    <T> T doGet(String path, Map<String, String[]> params, File targetFile, Class<T> resultClass) 
        throws IOException;

    /**
     * Issue a GET against the API.
     * 
     * @param path
     *            The web service endpoint.
     * @param params
     *            A Map of key value pairs that are converted into query
     *            arguments.
     * @param resultClass
     *            The response object type.
     * @return The response object from the operation. This response will be of
     *         the type given in the resultClass argument.
     * @throws IOException
     *             If a network error occurs during the request.
     */
    <T> T doGet(String path, Map<String, String[]> params, Class<T> resultClass)
            throws IOException;

    /**
     * Issue a POST against the API.
     * 
     * @param path
     *            The web service endpoint
     * @param params
     *            A Map of key value pairs that are converted into query
     *            arguments.
     * @param o
     *            The object to POST. This object will be serialized into XML
     *            prior to being sent.
     * @param resultClass
     *            The result object type.
     * @return The response object from the operation. This response will be of
     *         the type given in the resultClass argument.
     * @throws IOException
     *             If a network error occurs during the request.
     */
    <T> T doPost(String path, Map<String, String[]> params, Class<T> resultClass)
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
     * @param resultClass
     *            The result object type.
     * @return The response object from the operation. This response will be of
     *         the type given in the resultClass argument.
     * @throws IOException
     *             If a network error occurs during the request.
     */
    <T> T doPost(String path, Map<String, String> params, File file,
            Class<T> resultClass) throws IOException;

    /**
     * Issue a POST against the API.
     * 
     * @param path
     *            The web service endpoint
     * @param o
     *            The object to POST. This object will be serialized into XML
     *            prior to being sent.
     * @param resultClass
     *            The result object type.
     * @return The response object from the operation. This response will be of
     *         the type given in the resultClass argument.
     * @throws IOException
     *             If a network error occurs during the request.
     */
   <T> T doPost(String path, Object o, Class<T> resultClass)
            throws IOException;
}
