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
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.hyperic.hq.hqapi1.types.ServiceError;
import org.springframework.util.StringUtils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;

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

    private String _host;
    private int _port;
    private boolean _isSecure;
    private String _user;
    private String _password;

    public HQConnection(java.net.URI uri, String user, String password) {
    	this(uri.getHost(),
    		 uri.getPort(),
    		 uri.getScheme().equalsIgnoreCase("https") ? true : false,
    		 user,
    		 password);
    }
    
    public HQConnection(String host,
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

    public HQConnection(File clientProperties) 
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
        initConnectionProperties(props);
    }

    public HQConnection(Properties props) {
        initConnectionProperties(props);
    }

    private void initConnectionProperties(Properties props) {
        _host       = props.getProperty(OPT_HOST, "localhost");
        _port       = Integer.parseInt(props.getProperty(OPT_PORT, "7080"));
        _isSecure   = Boolean.valueOf(props.getProperty(OPT_SECURE, "false"));
        _user       = props.getProperty(OPT_USER, "hqadmin");
        _password   = props.getProperty(OPT_PASS, "");
        if (_password.isEmpty()) {
            String encryptionKey = props.getProperty(OPT_ENCRYPTIONKEY, "");
            String encryptedPassword = props.getProperty(OPT_ENCRYPTEDPASSWORD, "");
            if (encryptionKey.isEmpty() || encryptedPassword.isEmpty()) {
                _log.error(OPT_PASS + ", " + OPT_ENCRYPTIONKEY + ", " + OPT_ENCRYPTEDPASSWORD + 
                    " not set");
            }
            _password = decryptPassword(encryptedPassword, encryptionKey);
        }

        if (_isSecure) {
            // To allow for self signed certificates
            UntrustedSSLProtocolSocketFactory.register();
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
            configureSSL(client);
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
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        HttpHost host = new HttpHost(_host, _port, protocol);

        authCache.put(host, basicAuth);

        BasicHttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);        

        method.getParams().setParameter(ClientPNames.HANDLE_AUTHENTICATION, true);

        // Disable re-tries
        client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, true));

        HttpResponse response = client.execute(method, localContext);

        return responseHandler.handleResponse(response);        
    }

    private KeyStore getKeyStore(String keyStorePath, String keyStorePassword) throws KeyStoreException, IOException {
        FileInputStream keyStoreFileInputStream = null;

        try {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            File file = new File(keyStorePath);
            char[] password = null;

            if (!file.exists()) {
                // ...if file doesn't exist, and path was user specified throw IOException...
                if (StringUtils.hasText(keyStorePath)) {
                    throw new IOException("User specified keystore [" + keyStorePath + "] does not exist.");
                }

                password = keyStorePassword.toCharArray();
            }

            // ...keystore file exist, so init the file input stream...
            keyStoreFileInputStream = new FileInputStream(file);

            keystore.load(keyStoreFileInputStream, password);

            return keystore;
        } catch (NoSuchAlgorithmException e) {
            // can't check integrity of keystore, if this happens we're kind of screwed
            // is there anything we can do to self heal this problem?
            throw new KeyStoreException(e);
        } catch (CertificateException e) {
            // there are some corrupted certificates in the keystore, a bad thing
            // is there anything we can do to self heal this problem?
            throw new KeyStoreException(e);
        } finally {
            if (keyStoreFileInputStream != null) {
                keyStoreFileInputStream.close();
                keyStoreFileInputStream = null;
            }
        }
    }

    private KeyManagerFactory getKeyManagerFactory(final KeyStore keystore, final String password) throws KeyStoreException {
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            keyManagerFactory.init(keystore, password.toCharArray());

            return keyManagerFactory;
        } catch (NoSuchAlgorithmException e) {
            // no support for algorithm, if this happens we're kind of screwed
            // we're using the default so it should never happen
            throw new KeyStoreException(e);
        } catch (UnrecoverableKeyException e) {
            // invalid password, should never happen
            throw new KeyStoreException(e);
        }
    }

    private TrustManagerFactory getTrustManagerFactory(final KeyStore keystore) throws KeyStoreException, IOException {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keystore);

            return trustManagerFactory;
        } catch (NoSuchAlgorithmException e) {
            // no support for algorithm, if this happens we're kind of screwed
            // we're using the default so it should never happen
            throw new KeyStoreException(e);
        }
    }
    
    private void configureSSL(HttpClient client) throws IOException {
        final String keyStorePath = System.getProperty("javax.net.ssl.keyStore");
        final String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        final boolean validateSSLCertificates = StringUtils.hasText(keyStorePath) && StringUtils.hasText(keyStorePassword);

        X509TrustManager customTrustManager = null;
        KeyManager[] keyManagers = null;

        try {
            if (validateSSLCertificates) {
                // Use specified key store and perform SSL validation...
                KeyStore keystore = getKeyStore(keyStorePath, keyStorePassword);
                KeyManagerFactory keyManagerFactory = getKeyManagerFactory(keystore, keyStorePassword);
                TrustManagerFactory trustManagerFactory = getTrustManagerFactory(keystore);

                keyManagers = keyManagerFactory.getKeyManagers();
                customTrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
            } else {
                // Revert to previous functionality and ignore SSL certs...
                customTrustManager = new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}

                    //required for jdk 1.3/jsse 1.0.3_01
                    public boolean isClientTrusted(X509Certificate[] chain) {
                        return true;
                    }

                    //required for jdk 1.3/jsse 1.0.3_01
                    public boolean isServerTrusted(X509Certificate[] chain) {
                        return true;
                    }

                    public X509Certificate[] getAcceptedIssuers(){
                        return null;
                    }
                };
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(keyManagers, new TrustManager[] { customTrustManager }, new SecureRandom());

            // XXX Should we use ALLOW_ALL_HOSTNAME_VERIFIER (least restrictive) or 
            //     BROWSER_COMPATIBLE_HOSTNAME_VERIFIER (moderate restrictive) or
            //     STRICT_HOSTNAME_VERIFIER (most restrictive)???
            // For now allow all, and make it configurable later...

            X509HostnameVerifier hostnameVerifier = null;

            if (validateSSLCertificates) {
                hostnameVerifier = new AllowAllHostnameVerifier();
            } else {
                hostnameVerifier = new X509HostnameVerifier() {
                    private AllowAllHostnameVerifier internalVerifier = new AllowAllHostnameVerifier();

                    public boolean verify(String host, SSLSession session) {
                        return internalVerifier.verify(host, session);
                    }

                    public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                        internalVerifier.verify(host, cns, subjectAlts);
                    }

                    public void verify(String host, X509Certificate cert) throws SSLException {
                        internalVerifier.verify(host, cert);
                    }

                    public void verify(String host, SSLSocket ssl) throws IOException {
                        try {
                            internalVerifier.verify(host, ssl);
                        } catch(SSLPeerUnverifiedException e) {
                            // ignore
                        }
                    }
                };
            }

            client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, new SSLSocketFactory(sslContext, hostnameVerifier)));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
