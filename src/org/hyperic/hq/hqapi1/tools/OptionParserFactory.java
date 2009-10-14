/* Copyright 2009 SpringSource Inc. All Rights Reserved. */
package org.hyperic.hq.hqapi1.tools;

import java.util.Arrays;

import org.springframework.beans.factory.FactoryBean;

import joptsimple.OptionParser;

/**
 * Factory responsible for generating {@link OptionParser}s and initializing
 * with command line options accepted by all commands (i.e. connection
 * parameters and help)
 * 
 * @author Jennifer Hickey
 * 
 */
public class OptionParserFactory implements FactoryBean<OptionParser> {
	static final String OPT_HOST = "host";
	static final String OPT_PORT = "port";
	static final String OPT_PORT_SSL = "portSSL";
	static final String OPT_PORT_DEFAULTED = "portDefaulted";
	static final String OPT_USER = "user";
	static final String OPT_PASS = "password";
	static final String[] OPT_SECURE = {"s", "secure"};
	static final String[] OPT_HELP   = {"h","help"};
	public static final String SYSTEM_PROP_PREFIX = "scripting.client.";
	static final String OPT_FILE     = "file";
	static final String OPT_PROPERTIES = "properties";

	public OptionParser getObject() throws Exception {
	    
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

        parser.accepts(OPT_PROPERTIES, "Specify the file to read for connection " +
                                      "properties.  Defaults to ~/.hq/client.properties")
                .withRequiredArg().ofType(String.class);

        return parser;
	}

	public Class<OptionParser> getObjectType() {
		return OptionParser.class;
	}

	public boolean isSingleton() {
		return false;
	}

}
