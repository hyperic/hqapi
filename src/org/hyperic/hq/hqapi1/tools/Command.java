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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.log4j.PropertyConfigurator;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.Response;
import org.hyperic.hq.hqapi1.types.ResponseStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Enumeration;

public abstract class Command {

    static final String OPT_HOST     = "host";
    static final String OPT_PORT     = "port";
    static final String OPT_USER     = "user";
    static final String OPT_PASS     = "password";
    static final String[] OPT_SECURE = {"s", "secure"};
    static final String[] OPT_HELP   = {"h","help"};
    static final String OPT_FILE     = "file";
    static final String OPT_PROPERTIES = "properties";

    // Ripped out from PluginMain.java
    private static final String[][] LOG_PROPS = {
        { "log4j.appender.R", "org.apache.log4j.ConsoleAppender" },
        { "log4j.appender.R.layout.ConversionPattern", "%-5p [%t] [%c{1}] %m%n" },
        { "log4j.appender.R.layout", "org.apache.log4j.PatternLayout" }
    };

    private static void configureLogging(String level) {
        Properties props = new Properties();
        props.setProperty("log4j.rootLogger", level.toUpperCase() + ", R");
        props.setProperty("log4j.logger.httpclient.wire", level.toUpperCase());
        props.setProperty("log4j.logger.org.apache.commons.httpclient",
                          level.toUpperCase());

        for (String[] PROPS : LOG_PROPS) {
            props.setProperty(PROPS[0], PROPS[1]);
        }

        props.putAll(System.getProperties());
        PropertyConfigurator.configure(props);
    }

    static {
        // Quiet all logging
        configureLogging("fatal");
    }

    static OptionParser getOptionParser() {
        OptionParser parser = new OptionParser();

        parser.accepts(OPT_HOST, "The HQ server host").
                withRequiredArg().ofType(String.class);
        parser.accepts(OPT_PORT, "The HQ server port. Defaults to 7080").
                withOptionalArg().ofType(Integer.class);
        parser.accepts(OPT_USER, "The user to connect as").
                withRequiredArg().ofType(String.class);
        parser.accepts(OPT_PASS, "The password for the given user").
                withRequiredArg().ofType(String.class);

        parser.acceptsAll(Arrays.asList(OPT_SECURE), "Connect using SSL");
        parser.acceptsAll(Arrays.asList(OPT_HELP), "Show this message");
        parser.accepts(OPT_FILE, "If specified, use the given file for " +
                                 "commands that take XML input.  If " +
                                 "not specified, stdin will be used.").
                withRequiredArg().ofType(String.class);

        parser.accepts(OPT_PROPERTIES, "File to read for connection properties. " +
                                       "Path must be absolute and defaults to ~/.hq/client.properties")
                .withRequiredArg().ofType(String.class);

        return parser;
    }

    protected OptionSet getOptions(OptionParser p, String[] args) throws IOException {
        OptionSet o = p.parse(args);

        if (o.has(OPT_HELP[0]) || o.has(OPT_HELP[1])) {
            p.printHelpOn(System.err);
            System.exit(0);
        }

        return o;
    }

    private Properties getClientProperties(String file) {
        Properties props = new Properties();

        File clientProperties;

        if (file != null) {
            clientProperties = new File(file);
            if (!clientProperties.exists()) {
                System.err.println("Error: " + clientProperties.toString() +
                                   " does not exist");
                System.exit(-1);
            }
        } else {
            String home = System.getProperty("user.home");
            File hq = new File(home, ".hq");
            clientProperties = new File(hq, "client.properties");
        }

        if (clientProperties.exists()) {
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

    /**
     * TODO: This is not symmetrical
     * @param s The OptionSet parsed from the command line arguments.
     * @return A FileInputStream to the given file argument
     * @throws IOException If the file does not exist.
     */
    protected InputStream getInputStream(OptionSet s) throws IOException {
        String file = (String)s.valueOf(OPT_FILE);
        if (file == null) {
            return System.in;
        } else {
            File f = new File(file);
            return new FileInputStream(f);
        }
    }

    protected HQApi getApi(OptionSet s) {

        Properties clientProps =
                getClientProperties((String)s.valueOf(OPT_PROPERTIES));

        String host = (String)s.valueOf(OPT_HOST);
        if (host == null) {
            host = clientProps.getProperty(OPT_HOST);
        }

        Integer port;
        if (s.hasArgument(OPT_PORT)) {
            port = (Integer)s.valueOf(OPT_PORT);
        } else {
            port = Integer.parseInt(clientProps.getProperty(OPT_PORT, "7080"));
        }

        String user = (String)s.valueOf(OPT_USER);
        if (user == null) {
            user = clientProps.getProperty(OPT_USER);
        }

        String password = (String)s.valueOf(OPT_PASS);
        if (password == null) {
            password = clientProps.getProperty(OPT_PASS);
        }

        if (password == null) {
            // Prompt for password
            try {
                char[] passwordArray = PasswordField.getPassword(System.in,
                                                                 "Enter password: ");
                password = String.valueOf(passwordArray);
            } catch (IOException ioe) {
                System.err.println("Error reading password");
                System.exit(-1);
            }
        }

        Boolean secure = s.has(OPT_SECURE[0]) ||
                         Boolean.valueOf(clientProps.getProperty(OPT_SECURE[1],
                                                                 "false"));

        return new HQApi(host, port, secure, user, password);
    }

    protected Object getRequired(OptionSet s, String opt) {

        Object o = s.valueOf(opt);

        if (o == null) {
            System.err.println("Required argument " + opt + " not given.");
            System.exit(-1);
        }

        return o;
    }

    protected void checkSuccess(Response r) {
        if (r.getStatus() != ResponseStatus.SUCCESS) {
            System.err.println("Error running command: " + r.getError().getReasonText());
            System.exit(-1);
        }
    }

    /**
     * Trim the first argument from an array returning the resulting elements.
     * 
     * @param args The argument array to trim.
     * @return The trimmed array as defined as Array[1..len]
     */
    protected static String[] trim(String[] args) {
        String[] cmdArgs = new String[args.length -1];
        System.arraycopy(args, 1, cmdArgs, 0, args.length -1);
        return cmdArgs;
    }

    protected abstract void handleCommand(String[] args) throws Exception;
}
