package org.hyperic.hq.hqapi1;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;


//XXX s/com.sun/javax/g will not work with jdk 1.3 and jsse 1.0.3_01
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * The bogus trust manager allows for non-validated remote
 * SSL entities.  In the case of people using self-signed
 * certificates, this allows a connection, since it is unlikely
 * that they are using a CA, or that the CA will be in the client's
 * cacerts of jssecerts files.
 */
public class BogusTrustManager 
    implements X509TrustManager
{
    public void checkClientTrusted(X509Certificate[] chain,
                                   String authType)
    {
    }

    public void checkServerTrusted(X509Certificate[] chain,
                                   String authType)
    {
    }

    //required for jdk 1.3/jsse 1.0.3_01
    public boolean isClientTrusted(X509Certificate[] chain)
    {
        return true;
    }

    //required for jdk 1.3/jsse 1.0.3_01
    public boolean isServerTrusted(X509Certificate[] chain)
    {
        return true;
    }

    public X509Certificate[] getAcceptedIssuers(){
        return null;
    }
}
