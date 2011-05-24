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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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



public class Shell {

    private final Map<String, Command> _commands;

    public Shell(Map<String, Command> commands) {
        _commands = commands;
    }
    
    static private Properties getClientProperties(String file) {
        Properties props = new Properties();

        File clientProperties = null;

        if (file != null) {
            clientProperties = new File(file);
            if (!clientProperties.exists()) {
                System.err.println("Error: " + clientProperties.toString() +
                                   " does not exist");
                System.exit(-1);
            }
        } else {
            InputStream is = Shell.class.getResourceAsStream("/client.properties");
            try {
                if (is != null) {
                    props.load(is);
                }
            } catch (IOException e) {
                // System.err..etc..
                System.exit(-1);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignore) {}
                }
            }
            
            if (is == null) {
                // Default to ~/.hq/client.properties
                String home = System.getProperty("user.home");
                File hq = new File(home, ".hq");
                clientProperties = new File(hq, "client.properties");
            }
        }

        if (clientProperties != null && clientProperties.exists()) {
            FileInputStream fis = null;
            props = new Properties();
            try {
                fis = new FileInputStream(clientProperties);
                props.load(fis);
            } catch (IOException e) {
                return props;
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

        // Trim property values
        for (Enumeration e = props.propertyNames(); e.hasMoreElements(); ) {
            String prop = (String)e.nextElement();
            props.setProperty(prop, props.getProperty(prop).trim());
        }

        return props;
    }
    
    static void initConnectionProperties(final String[] args) throws Exception {
        final List<String> connectionArgs = new ArrayList<String>(5);
        for (int i=0;i <args.length;i++) {
            final String arg = args[i];
            if (arg.trim().startsWith("--" + OptionParserFactory.OPT_HOST)
                    || arg.trim().startsWith(
                            "--" + OptionParserFactory.OPT_PORT)
                    || arg.trim().startsWith(
                            "--" + OptionParserFactory.OPT_PASS)
                    || arg.trim().startsWith(
                            "--" + OptionParserFactory.OPT_USER)
                    || arg.trim().startsWith(
                            "--" + OptionParserFactory.OPT_SECURE)
                    || arg.trim().startsWith(
                            "--" + OptionParserFactory.OPT_PROPERTIES)) {
                connectionArgs.add(arg);
                if( i != args.length-1 && !(args[i+1].startsWith("--"))) {
                    connectionArgs.add(args[i+1]);
                }
            }
        }
        final OptionParser optionParser = (OptionParser) new OptionParserFactory()
                .getObject();
        final OptionSet options = optionParser.parse(connectionArgs
                .toArray(new String[connectionArgs.size()]));
     
        Properties clientProps =
                getClientProperties((String)options.valueOf(OptionParserFactory.OPT_PROPERTIES));

        String host = (String)options.valueOf(OptionParserFactory.OPT_HOST);
        if (host == null) {
            host = clientProps.getProperty(OptionParserFactory.OPT_HOST);
        }
        if (host != null) {
            System.setProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + OptionParserFactory.OPT_HOST, host);
        }

        Integer port;
        if (options.hasArgument(OptionParserFactory.OPT_PORT)) {
            port = (Integer)options.valueOf(OptionParserFactory.OPT_PORT);
        } else {
            port = Integer.parseInt(clientProps.getProperty(OptionParserFactory.OPT_PORT, "7080"));
        }
        if (port != null) {
            System.setProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + OptionParserFactory.OPT_PORT, port.toString());
        }

        String user = (String)options.valueOf(OptionParserFactory.OPT_USER);
        if (user == null) {
            user = clientProps.getProperty(OptionParserFactory.OPT_USER);
        }
        if (user != null) {
            System.setProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + OptionParserFactory.OPT_USER, user);
        }

        String password = (String)options.valueOf(OptionParserFactory.OPT_PASS);
        if (password == null) {
            password = clientProps.getProperty(OptionParserFactory.OPT_PASS);
            // Check for encrypted password
            if (password == null || password.isEmpty()) {
                String encryptionKey = clientProps.getProperty(OptionParserFactory.OPT_ENCRYPTIONKEY);
                String encryptedPassword = clientProps.getProperty(OptionParserFactory.OPT_ENCRYPTEDPASSWORD);
                password = decryptPassword(encryptedPassword, encryptionKey);
            }
        }

        if (host != null && port != null && user != null && password == null) {
            // Prompt for password, but only if other connection properties
            // have been specified.
            try {
                char[] passwordArray = PasswordField.getPassword(System.in,
                                                                 "Enter password: ");
                password = String.valueOf(passwordArray);
            } catch (IOException ioe) {
                System.err.println("Error reading password");
                System.exit(-1);
            }
        }
        if (password != null) {
            System.setProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + OptionParserFactory.OPT_PASS,password);
        }


        Boolean secure = options.hasArgument(OptionParserFactory.OPT_SECURE[0]) ||
                         Boolean.valueOf(clientProps.getProperty(OptionParserFactory.OPT_SECURE[1],
                                                                 "false"));
        System.setProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + OptionParserFactory.OPT_SECURE[1], secure.toString());  
    }

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
    
    private void printHelp() {
        System.out.println("HQ Api Command Shell");
        System.out.println("");
        System.out.println("Available commands:");
        for (String command : _commands.keySet()) {
            System.out.println("    " + command);
        }
    }
    
    public int dispatchCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printHelp();
            return 1;
        }
        Command cmd = _commands.get(args[0]);
        if (cmd == null) {
            printHelp();
            return 1;
        }
        return cmd.handleCommand(AbstractCommand.trim(args));
    }

    public static void main(String[] args) throws Exception {
        try {
            initConnectionProperties(args);
        } catch (Exception e) {
           System.err.println("Error parsing command line connection properties.  Cause: "
                            + e.getMessage());
           e.printStackTrace(System.err);
           System.exit(1);
        }
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
              new String[] { "classpath:/META-INF/spring/hqapi-context.xml","classpath*:/META-INF/**/*commands-context.xml"});
        try {
            final int exitCode = ((Shell) applicationContext
                    .getBean("commandDispatcher")).dispatchCommand(args);
            System.exit(exitCode);
        } catch (Exception e) {
            System.err.println("Error running command: " + e.getMessage());
            e.printStackTrace(System.err);
           System.exit(1);
        }
    }
}
