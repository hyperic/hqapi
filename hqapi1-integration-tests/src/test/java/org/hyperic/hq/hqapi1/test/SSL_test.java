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

package org.hyperic.hq.hqapi1.test;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.UsersResponse;

public class SSL_test extends HQApiTestBase {
    public SSL_test(String name) {
        super(name);
    }

    private KeyStore keystore;
    
    @Override
	public void setUp() throws Exception {
		super.setUp();
		
		// generate keystore for testing...
		File file = new File("SSL_test_keystore");
		
		if (file.exists()) {
			file.delete();
		}
		
        String javaHome = System.getProperty("java.home");
        String keytool = javaHome + File.separator + "bin" + File.separator + "keytool";
        String[] args = {
            keytool,
            "-genkey",
            "-dname",  		"CN=ssltest_cert, OU=HQ, O=hyperic.net, L=Unknown, ST=Unknown, C=US",
            "-alias",     "ssltest",
            "-keystore",  "SSL_test_keystore",
            "-storepass", "ssltest",
            "-keypass",   "ssltest",
            "-keyalg",    "RSA"
        };

        Runtime.getRuntime().exec(args);
        
        if (file.exists()) {
        	FileInputStream keyStoreFileInputStream = null;
        	
        	try {
        		keyStoreFileInputStream = new FileInputStream(file);
	        	keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	        	
	        	keystore.load(keyStoreFileInputStream, "ssltest".toCharArray());
        	} finally {
        		if (keyStoreFileInputStream != null) {
        			keyStoreFileInputStream.close();
        			keyStoreFileInputStream = null;
        		}
        	}
        }
	}
   
	public void testSSLNoValidation() throws Exception {
		// non-validating SSL connection...maintains backwards compatibility
		makeSSLRequest();
    }
    
    public void testSSLWithValidation() throws Exception {
    	/*
    	System.setProperty("javax.net.ssl.keyStore", "SSL_test_keystore");
    	System.setProperty("javax.net.ssl.keyStorePassword", "ssltest");
    	
    	try {
    		// First time we fail...
    		makeSSLRequest();
    	} catch(Exception e) {
    		// TODO Need to do some work to import a cert on demand
    		
    		// ...this time should succeed
    		makeSSLRequest();
    	}
    	*/
    }

    private void makeSSLRequest() throws Exception {
    	HQApi api = getApi(true);
        UserApi userApi = api.getUserApi();

        UsersResponse response = userApi.getUsers();
        hqAssertSuccess(response);

        assert(response.getUser().size() > 0);
    }
}
