package org.hyperic.hq.hqapi1.tools;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.Response;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

import jargs.gnu.CmdLineParser;

public abstract class ToolsBase {

    static final CmdLineParser.Option OPT_HOST = new CmdLineParser.Option.StringOption("host");
    static final CmdLineParser.Option OPT_PORT = new CmdLineParser.Option.IntegerOption("port");
    static final CmdLineParser.Option OPT_USER = new CmdLineParser.Option.StringOption("user");
    static final CmdLineParser.Option OPT_PASS = new CmdLineParser.Option.StringOption("password");
    static final CmdLineParser.Option OPT_SECURE = new CmdLineParser.Option.BooleanOption('s', "secure");
    static final CmdLineParser.Option OPT_HELP = new CmdLineParser.Option.BooleanOption('h', "help");

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

    static Parser getParser() {
        Parser p = new Parser();

        p.addOption(OPT_HOST, true, "The HQ server host.");
        p.addOption(OPT_PORT, false, "The HQ server port.  Defaults to 7080.");
        p.addOption(OPT_USER, true, "The user to connect as.");
        p.addOption(OPT_PASS, true, "The passord for the given user.");
        p.addOption(OPT_SECURE, false, "Controls whether communication will use SSL.");
        p.addOption(OPT_HELP, false, "Show this help meesage");

        return p;
    }

    static HQApi getApi(Parser p) {
        String host = (String)p.getOptionValue(OPT_HOST);
        Integer port = (Integer)p.getOptionValue(OPT_PORT, 7080);
        String user = (String)p.getOptionValue(OPT_USER);
        String password = (String)p.getOptionValue(OPT_PASS);
        Boolean secure = (Boolean)p.getOptionValue(OPT_SECURE, false);

        return new HQApi(host, port, secure, user, password);
    }

    static boolean isSuccess(Response r) {
        if (r.getStatus() != ResponseStatus.SUCCESS) {
            System.err.println("Error running command: " + r.getError().getReasonText());
            return false;
        }
        return true;
    }
}
