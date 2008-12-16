package org.hyperic.hq.hqapi1.tools;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.Response;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

import joptsimple.OptionSet;
import joptsimple.OptionParser;

public abstract class ToolsBase {

    static final String OPT_HOST     = "host";
    static final String OPT_PORT     = "port";
    static final String OPT_USER     = "user";
    static final String OPT_PASS     = "password";
    static final String[] OPT_SECURE = {"s", "secure"};
    static final String[] OPT_HELP   = {"h","help"};

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

        parser.accepts(OPT_HOST).withRequiredArg().
                describedAs("The HQ server host").ofType(String.class);
        parser.accepts(OPT_PORT).withRequiredArg().
                describedAs("The HQ server port. Defaults to 7080").ofType(Integer.class);
        parser.accepts(OPT_USER).withRequiredArg().
                describedAs("The user to connect as").ofType(String.class);
        parser.accepts(OPT_PASS).withRequiredArg().
                describedAs("The password for the given user").ofType(String.class);
        parser.accepts(OPT_SECURE[0], OPT_SECURE[1]).withOptionalArg().
                describedAs("Connect using SSL");
        parser.accepts(OPT_HELP[0], OPT_HELP[1]).withOptionalArg().
                describedAs("Show this message");

        return parser;
    }

    static OptionSet getOptions(OptionParser p, String[] args) {
        //TODO: help
        return p.parse(args);
    }

    static HQApi getApi(OptionSet s) {
        String host = (String)s.valueOf(OPT_HOST);
        Integer port;
        if (s.hasArgument(OPT_PORT)) {
            port = (Integer)s.valueOf(OPT_PORT);
        } else {
            port = 7080;
        }
        String user = (String)s.valueOf(OPT_USER);
        String password = (String)s.valueOf(OPT_PASS);
        Boolean secure = false;
        if(s.hasArgument(OPT_SECURE[0])) {
            secure = true;
        }

        return new HQApi(host, port, secure, user, password);
    }

    static Object getRequired(OptionSet s, String opt) {

        Object o = s.valueOf(opt);

        if (o == null) {
            System.err.println("Required argument " + opt + " not given.");
            System.exit(-1);
        }

        return o;
    }

    static void checkSuccess(Response r) {
        if (r.getStatus() != ResponseStatus.SUCCESS) {
            System.err.println("Error running command: " + r.getError().getReasonText());
            System.exit(-1);
        }
    }
}
