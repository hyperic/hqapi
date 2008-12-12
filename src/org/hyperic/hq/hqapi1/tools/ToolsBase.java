package org.hyperic.hq.hqapi1.tools;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.Response;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.apache.log4j.PropertyConfigurator;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

public abstract class ToolsBase {

    static final String PROP_HOST   = "host";
    static final String PROP_PORT   = "port";
    static final String PROP_SECURE = "secure";
    static final String PROP_USER   = "user";
    static final String PROP_PASS   = "password";

    static final String PROP_HELP   = "help";

    static final String[][] REQURIED =
            { {PROP_HOST, "The HQ server host"},
              {PROP_USER, "The user to connect as"},
              {PROP_PASS, "The password for the given user" } };
    static final String[][] OPTIONAL =
            { {PROP_PORT, "The HQ server port.  Defaults to 7080."},
              {PROP_SECURE, "Controls whether communication will use SSL" },
              {PROP_HELP, "Prints help information"} };

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
    
    static Map<String,String> parseParameters(String[] args) throws Exception {

        Map<String,String> params = new HashMap<String,String>();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                int idx = arg.indexOf("=");
                if (idx > 0) {
                    params.put(arg.substring(2,idx), arg.substring(idx+1));
                } else {
                    params.put(arg.substring(2), "true");
                }
            }

            // Handle -D
        }

        return params;
    }

    static HQApi getApi(Map<String,String> params) {
        String host     = params.get(PROP_HOST);
        int port;
        String user     = params.get(PROP_USER);
        String password = params.get(PROP_PASS);

        try {
            port = Integer.parseInt(params.get(PROP_PORT));
        } catch (NumberFormatException e) {
            port = 7080;
        }

        boolean secure  = (port != 7080) || params.containsKey(PROP_SECURE);

        return new HQApi(host, port, secure, user, password);
    }

    static boolean isSuccess(Response r) {
        if (r.getStatus() != ResponseStatus.SUCCESS) {
            System.err.println("Error running command: " + r.getError().getReasonText());
            return false;
        }
        return true;
    }

    static boolean checkHelp(Map<String,String> params) {
        if (params.containsKey(PROP_HELP)) {
            System.err.println("Required parameters:");
            for (String[] param : REQURIED) {
                System.err.println("    --" + param[0] + ": " + param[1]);
            }
            System.err.println("Optional parameters:");
            for (String[] param : OPTIONAL) {
                System.err.println("    --" + param[0] + ": " + param[1]);
            }
            return true;
        }
        return false;
    }
}
