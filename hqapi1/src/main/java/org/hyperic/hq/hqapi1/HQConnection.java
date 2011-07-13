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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;
import org.hyperic.hq.hqapi1.types.ServiceError;

class HQConnection implements Connection {

    private static Log _log = LogFactory.getLog(HQConnection.class);

    private String _host;
    private int _port;
    private boolean _isSecure;
    private String _user;
    private String _password;

    HQConnection(java.net.URI uri, String user, String password) {
    	this(uri.getHost(),
    		 uri.getPort(),
    		 uri.getScheme().equalsIgnoreCase("https") ? true : false,
    		 user,
    		 password);
    }
    
    HQConnection(String host,
                 int port,
                 boolean isSecure,
                 String user,
                 String password)
    {
        _host = host;
        _port = port;
        _isSecure = isSecure;
        _user = user;
        _password = password;
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
     * @throws  
     */
    public <T> T doGet(String path, Map<String, String[]> params, ResponseHandler<T> responseHandler)
    throws IOException
    {
        return runMethod(new HttpGet(), buildUri(path, params), responseHandler);
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
        return runMethod(new HttpGet(), buildUri(path, params), responseHandler);
    }

    public <T> T doPost(String path, Map<String, String[]> params, ResponseHandler<T> responseHandler)
    throws IOException {
    	HttpPost post = new HttpPost();
    	
    	if (params != null && !params.isEmpty()) {
	    	List<NameValuePair> postParams = new ArrayList<NameValuePair>();
	    	
	    	for (Map.Entry<String, String[]> entry : params.entrySet()) {
	    		for (String value : entry.getValue()) {
	    			postParams.add(new BasicNameValuePair(entry.getKey(), value));	    			
	    		}
	    	}
        
	    	post.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
    	}
    	
        return runMethod(post, buildUri(path, params), responseHandler);
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
    public <T> T doPost(String path, Map<String, String> params, File file, ResponseHandler<T> responseHandler)
    throws IOException {
        HttpPost post = new HttpPost();
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        
        multipartEntity.addPart(file.getName(), new FileBody(file));
        
        for (Map.Entry<String, String> paramEntry : params.entrySet()) {
        	multipartEntity.addPart(new FormBodyPart(paramEntry.getKey(), new StringBody(paramEntry.getValue())));
        }
        
        post.setEntity(multipartEntity);
        
        return runMethod(post, path, responseHandler);
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
    throws IOException {
        HttpPost post = new HttpPost();
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
       
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        
        multipartEntity.addPart("postdata", new StringBody(bos.toString("UTF-8"), Charset.forName("UTF-8")));
        post.setEntity(multipartEntity);

        return runMethod(post, path, responseHandler);
    }

    private <T> T runMethod(HttpRequestBase method, String uri, ResponseHandler<T> responseHandler)
            throws IOException
    {
        String protocol = _isSecure ? "https" : "http";
        ServiceError error;
        URL url = new URL(protocol, _host, _port, uri);
        
        try {
        	method.setURI(url.toURI());
        } catch(URISyntaxException e) {
        	throw new IllegalArgumentException("The syntax of request url [" + uri + "] is invalid", e);
        }
        
        _log.debug("Setting URI: " + url.toString());

        DefaultHttpClient client = new DefaultHttpClient();
        
        if (_isSecure) {
            // To allow for self signed certificates
            (new UntrustedSSLProtocolSocketFactory()).register(client);
        }

        // Validate user & password inputs
        if (_user == null || _user.length() == 0) {
        	error = new ServiceError();
            error.setErrorCode("LoginFailure");
            error.setReasonText("User name cannot be null or empty");
            
            return responseHandler.getErrorResponse(error);
        }

        if (_password == null || _password.length() == 0) {
            error = new ServiceError();
            error.setErrorCode("LoginFailure");
            error.setReasonText("Password cannot be null or empty");
        
            return responseHandler.getErrorResponse(error);
        }

        // Set Basic auth creds
        UsernamePasswordCredentials defaultcreds = new UsernamePasswordCredentials(_user, _password);
            
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, defaultcreds);
            
        // Preemptive authentication
        method.getParams().setParameter(ClientPNames.HANDLE_AUTHENTICATION, true);

        // Disable re-tries
        client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, true));

        HttpResponse response = client.execute(method);
            
        return responseHandler.handleResponse(response);        
    }
}
