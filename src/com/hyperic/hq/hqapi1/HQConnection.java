package com.hyperic.hq.hqapi1;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.util.Map;
import java.util.Iterator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.net.SocketException;

public abstract class HQConnection {

    private static Log _log = LogFactory.getLog(HQConnection.class);

    private String  _host;
    private int     _port;
    private boolean _isSecure;
    private String  _user;
    private String  _password;

    HQConnection(String host, int port, boolean isSecure, String user,
                 String password) {
        _host     = host;
        _port     = port;
        _isSecure = isSecure;
        _user     = user;
        _password = password;
    }

    private HttpClient getHttpClient() {
        HttpClient client = new HttpClient();

        client.getParams().setAuthenticationPreemptive(true);
        Credentials defaultcreds = new UsernamePasswordCredentials(_user,
                                                                   _password);
        client.getState().setCredentials(AuthScope.ANY, defaultcreds);
        return client;
    }

    private Object deserialize(Class res, InputStream is)
        throws JAXBException
    {
        String pkg = res.getPackage().getName();
        JAXBContext jc = JAXBContext.newInstance(pkg);
        Unmarshaller u = jc.createUnmarshaller();
        return res.cast(u.unmarshal(is));
    }

    private GetMethod getHttpGetMethod() {
        GetMethod m = new GetMethod();
        m.setDoAuthentication(true);
        return m;
    }

    private String urlEncode(String s)
        throws IOException
    {
        return URLEncoder.encode(s, "UTF-8");
    }

    Object getRequest(String path, Map params, Class resultClass)
        throws IOException, JAXBException
    {
        GetMethod method = getHttpGetMethod();
        String protocol = _isSecure ? "https" : "http";
        StringBuffer query = new StringBuffer(path);
        if (query.charAt(query.length() - 1) != '?') {
            query.append("?");
        }

        for (Iterator i = params.keySet().iterator(); i.hasNext(); ) {
            String key = (String)i.next();
            String value = (String)params.get(key);
            if (value != null) {
                query.append(urlEncode(key)).append("=").append(urlEncode(value));
            }
            if (i.hasNext()) {
                query.append("&");
            }
        }

        URL url = new URL(protocol, _host, _port, query.toString());
        _log.debug("HTTP Request: " + url.toString());
        method.setURI(new URI(url.toString(), true));

        int code;
        try {
            code = getHttpClient().executeMethod(method);
        } catch (SocketException e) {
            throw new HttpException("Error issuing request", e);
        }

        if (code == 200) {
            // We only deal with HTTP_OK responses
            if (resultClass != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("HTTP Response: " +
                               method.getResponseBodyAsString());
                }
                
                InputStream is = method.getResponseBodyAsStream();
                return deserialize(resultClass, is);
            }
        } else {
            throw new HttpException("Invalid HTTP return code " + code);
        }

        return null;   
    }
}
