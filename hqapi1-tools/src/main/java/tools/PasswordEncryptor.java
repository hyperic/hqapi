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

package org.hyperic.hq.hqapi1.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.springframework.context.support.ClassPathXmlApplicationContext;



public class PasswordEncryptor {

    private static String decryptPassword(String encryptedPassword, String encryptionKey) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(encryptionKey);
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        return PropertyValueEncryptionUtils.decrypt(encryptedPassword, encryptor);
    }
    
    private static String encryptPassword(String plainPassword, String encryptionKey) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(encryptionKey);
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        return PropertyValueEncryptionUtils.encrypt(plainPassword, encryptor);
    }    
    
    public static void main(String[] args) throws Exception {
        String password1 = String.valueOf(PasswordField.getPassword(System.in,
            "Enter password: "));
        String password2 = String.valueOf(PasswordField.getPassword(System.in,
            "Re-Enter password: "));
        String encryptionKey = "defaultkey";
        if (password1.equals(password2)) {
            System.out.print("Encryption Key: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            encryptionKey = in.readLine();
            if (encryptionKey.length() < 8) {
                System.out.println("Encryption key too short. Please use at least 8 characters");
                System.exit(-1);
            }
        } else {
            System.out.println("Passwords don't match");
            System.exit(-1);
        }
        
        System.out.println("The encrypted password is " + encryptPassword(password1, encryptionKey));
    }
}
