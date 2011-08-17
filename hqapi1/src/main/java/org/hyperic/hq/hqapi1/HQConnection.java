/*
 * NOTE: This copyright does *not* cover user programs that use HQ program
 * services by normal system calls through the application program interfaces
 * provided as part of the Hyperic Plug-in Development Kit or the Hyperic Client
 * Development Kit - this is merely considered normal use of the program, and
 * does *not* fall under the heading of "derived work". Copyright (C) [2008,
 * 2009], Hyperic, Inc. This file is part of HQ. HQ is free software; you can
 * redistribute it and/or modify it under the terms version 2 of the GNU General
 * Public License as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package org.hyperic.hq.hqapi1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.hqapi1.types.ServiceError;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;

public class HQConnection implements Connection {

    static final String OPT_HOST = "host";
    static final String OPT_PORT = "port";
    static final String OPT_USER = "user";
    static final String OPT_PASS = "password";
    static final String OPT_ENCRYPTEDPASSWORD = "encryptedPassword";
    static final String OPT_ENCRYPTIONKEY = "encryptionKey";
    static final String OPT_SECURE = "secure";

    
    private static Log _log = LogFactory.getLog(HQConnection.class);

    private static final int DEFAULT_MAX_HTTP_CONNECTIONS = 6;

    private String _host;
    private int _port;
    private boolean _isSecure;
    private String _user;
    private String _password;

    private String _protocol;

    private HttpClient _httpClient;
    private HttpConnectionManager _connectionManager;
    private HttpState _httpState;

    private boolean illegalUsername = false;
    private boolean illegalPassword = false;

    HQConnection(java.net.URI uri, String user, String password) {
    	this(uri.getHost(),
    		 uri.getPort(),
    		 uri.getScheme().equalsIgnoreCase("https") ? true : false,
    		 user,
    		 password);
    }

    HQConnection(java.net.URI uri, String user, String password, int maxHttpConnections) {
        this(uri.getHost(), uri.getPort(),
            uri.getScheme().equalsIgnoreCase("https") ? true : false, user, password,
            maxHttpConnections);
    }

    HQConnection(String host, int port, boolean isSecure, String user, String password) {
        this(host, port, isSecure, user, password, DEFAULT_MAX_HTTP_CONNECTIONS);
    }

    HQConnection(String host,
                 int port,
                 boolean isSecure,
                 String user,
                 String password,
                 int maxHttpConnections) {
        // Basic validation of user and password inputs
        // We only mark the problems due to backward compatibility. If it's not
        // needed then these should throw exceptions right here in the
        // constructor
        if (user == null || user.length() == 0) {
            illegalUsername = true;
        }
        if (password == null || password.length() == 0) {
            illegalPassword = true;
        }

        _host = host;
        _port = port;
        _isSecure = isSecure;
        _user = user;
        _password = password;
        _protocol = _isSecure ? "https" : "http";

        if (_isSecure) {
            // To allow for self signed certificates
            UntrustedSSLProtocolSocketFactory.register();
        }

        // Initialize connection manager - We use a private connection manager
        // per HQConnection, therefore the per-host and the total
        // connections are identical.
        _connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setDefaultMaxConnectionsPerHost(maxHttpConnections);
        params.setMaxConnectionsPerHost(null, maxHttpConnections);
        params.setMaxTotalConnections(maxHttpConnections);
        _connectionManager.setParams(params);

        // Initialize http state with the credentials
        _httpState = new HttpState();
        Credentials defaultcreds = new UsernamePasswordCredentials(_user, _password);
        _httpState.setCredentials(AuthScope.ANY, defaultcreds);

        // Create the http client instance
        _httpClient = new HttpClient(_connectionManager);
        // and make sure it tried to authenticate preemptively
        _httpClient.getParams().setAuthenticationPreemptive(true);

        // Disable re-tries
        DefaultHttpMethodRetryHandler retryhandler = new DefaultHttpMethodRetryHandler(0, true);
        _httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryhandler);
    }

    HQConnection(File clientProperties) 
        throws FileNotFoundException, IOException {
        Properties props = new Properties();;
        if (clientProperties != null && clientProperties.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(clientProperties);
                props.load(fis);
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException ioe) {
                    // Ignore
                }
            }
        }

        _host       = props.getProperty(OPT_HOST, "localhost");
        _port       = Integer.parseInt(props.getProperty(OPT_PORT, "7080"));
        _isSecure   = Boolean.valueOf(props.getProperty(OPT_SECURE, "false"));
        _user       = props.getProperty(OPT_USER, "hqadmin");
        _password   = props.getProperty(OPT_PASS);
        if (_password != null || _password.isEmpty()) {
            String encryptionKey = props.getProperty(OPT_ENCRYPTIONKEY);
            String encryptedPassword = props.getProperty(OPT_ENCRYPTEDPASSWORD);
            _password = decryptPassword(encryptedPassword, encryptionKey);
        }
    }

    private static String decryptPassword(String encryptedPassword, String encryptionKey) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(encryptionKey);
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        return PropertyValueEncryptionUtils.decrypt(encryptedPassword, encryptor);
    }
    
    
    private String urlEncode(String s) throws IOException {
        return URLEncoder.encode(s, "UTF-8");
    }

    /**
     * Issue a GET against the API.
     * 
     * @param path The web service endpoint.
     * @param params A Map of key value pairs that are converted into query
     *        arguments.
     * @param responseHandler
     *            The {@link org.hyperic.hq.hqapi1.ResponseHandler} to handle this response.
     * @return The response object from the operation. This response will be of
     *         the type given in the responseHandler argument.
     * @throws IOException If a network error occurs during the request.
     */
    public <T> T doGet(String path, Map<String, String[]> params,
                       ResponseHandler<T> responseHandler)
            throws IOException
    {
        GetMethod method = new GetMethod();
        method.setDoAuthentication(true);
        return runMethod(method, buildUri(path, params), responseHandler);
    }

    private String buildUri(String path, Map<String, String[]> params) throws IOException {
        StringBuffer uri = new StringBuffer(path);
        if (uri.charAt(uri.length() - 1) != '?') {
            uri.append("?");
        }

        boolean append = false;

        for (Map.Entry<String, String[]> e : params.entrySet()) {
            for (String val : e.getValue()) {
                if (val != null) {
                    if (append) {
                        uri.append("&");
                    }
                    uri.append(e.getKey()).append("=").append(urlEncode(val));
                    append = true;
                }
            }
        }
        return uri.toString();
    }

    public <T> T doGet(String path, Map<String, String[]> params, File targetFile,
                       ResponseHandler<T> responseHandler)
            throws IOException
    {
        GetMethod method = new GetMethod();
        method.setDoAuthentication(true);
        return runMethod(method, buildUri(path, params), responseHandler);
    }

    public <T> T doPost(String path, Map<String, String[]> params,
                        ResponseHandler<T> responseHandler)
            throws IOException
    {
        PostMethod method = new PostMethod();
        method.setDoAuthentication(true);
        return runMethod(method, buildUri(path, params), responseHandler);
    }
    
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
     *            The {@link org.hyperic.hq.hqapi1.ResponseHandler} to handle this response.
     * @return The response object from the operation. This response will be of
     *         the type given in the responseHandler argument.
     * @throws IOException
     *             If a network error occurs during the request.
     */
    public <T> T doPost(String path, Map<String, String> params, File file,
    		            ResponseHandler<T> responseHandler)
            throws IOException
    {
        PostMethod method = new PostMethod();
        method.setDoAuthentication(true);
        final List<Part> parts = new ArrayList<Part>();
        parts.add(new FilePart("filename", file.getName(), file));
        for (Map.Entry<String, String> paramEntry : params.entrySet()) {
            parts
                    .add(new StringPart(paramEntry.getKey(), paramEntry
                            .getValue()));
        }
        method.setRequestEntity(new MultipartRequestEntity(parts
                .toArray(new Part[parts.size()]), method.getParams()));
        return runMethod(method, path, responseHandler);
    }

    /**
     * Issue a POST against the API.
     * 
     * @param path The web service endpoint
     * @param o The object to POST. This object will be serialized into XML
     *        prior to being sent.
     * @param responseHandler
     *            The {@link org.hyperic.hq.hqapi1.ResponseHandler} to handle this response.
     * @return The response object from the operation. This response will be of
     *         the type given in the responseHandler argument.
     * @throws IOException If a network error occurs during the request.
     */
    public <T> T doPost(String path, Object o, ResponseHandler<T> responseHandler)
            throws IOException
    {
        PostMethod method = new PostMethod();
        method.setDoAuthentication(true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            XmlUtil.serialize(o, bos, Boolean.FALSE);
        } catch (JAXBException e) {
            ServiceError error = new ServiceError();
            error.setErrorCode("UnexpectedError");
            error.setReasonText("Unable to serialize response");
            if (_log.isDebugEnabled()) {
                _log.debug("Unable to serialize response", e);
            }
            return responseHandler.getErrorResponse(error);
        }

        Part[] parts = { new StringPart("postdata", bos.toString("utf-8"), "utf-8") };

        method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));

        return runMethod(method, path, responseHandler);
    }

    private <T> T runMethod(HttpMethodBase method, String uri,
                            ResponseHandler<T> responseHandler)
            throws IOException
    {
        ServiceError error;
        URL url = new URL(_protocol, _host, _port, uri);
        method.setURI(new URI(url.toString(), true));
        _log.debug("Setting URI: " + url.toString());

        try {
            // These tests are just here for backward compatibility, if the
            // error
            // responses are not part of the expected contract, then these
            // tests should move to the constructor
            if (illegalUsername) {
                error = new ServiceError();
                error.setErrorCode("LoginFailure");
                error.setReasonText("User name cannot be null or empty");
                return responseHandler.getErrorResponse(error);
            }
            if (illegalPassword) {
                error = new ServiceError();
                error.setErrorCode("LoginFailure");
                error.setReasonText("Password cannot be null or empty");
                return responseHandler.getErrorResponse(error);
            }

            // Execute the http method, using the state information (i.e. cookie
            // and authentication)
            int responseCode = _httpClient.executeMethod(null, method, _httpState);

            return responseHandler.handleResponse(responseCode, method);
        } catch (SocketException e) {
            throw new HttpException("Error issuing request", e);
        } finally {
            method.releaseConnection();
        }
    }
}
