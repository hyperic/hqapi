/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

package org.hyperic.hq.hqapi1;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

// For use with Commons-HTTPClient
class UntrustedSSLProtocolSocketFactory {
	private SSLSocketFactory factory;
    private Scheme defaultSSL;

    private boolean isRegistered(HttpClient client) {
    	Scheme https = client.getConnectionManager().getSchemeRegistry().get("https");
    	boolean isRegistered = https.getSocketFactory() instanceof UntrustedSSLProtocolSocketFactory;
        
    	if (!isRegistered) {
            this.defaultSSL = https;
        }
        
    	return isRegistered;
    }

    public void register(HttpClient client) {
        //don't croak on self-signed certs
        if (!isRegistered(client)) {
        	try {
	           	X509TrustManager customTrustManager = new X509TrustManager() {
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
	    	        
	    		SSLContext sslContext = SSLContext.getInstance("TLS");
	    	        
	    	    sslContext.init(null, new TrustManager[] { customTrustManager }, new SecureRandom());
	    	        
	    	    // XXX Should we use ALLOW_ALL_HOSTNAME_VERIFIER (least restrictive) or 
	    	    //     BROWSER_COMPATIBLE_HOSTNAME_VERIFIER (moderate restrictive) or
	    	    //     STRICT_HOSTNAME_VERIFIER (most restrictive)???
	    	    this.factory = new SSLSocketFactory(sslContext, new X509HostnameVerifier() {
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
							//ignore
						}
					}
	    	    });
	    	        
	    	    client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, this.factory));
        	} catch (KeyManagementException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
        }
    }

    public void unregister(HttpClient client) {
        if (isRegistered(client)) {
        	client.getConnectionManager().getSchemeRegistry().register(defaultSSL);
        }
    }
}