package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.Arrays;
import java.util.List;
import java.io.InputStream;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.ServerConfigApi;
import org.hyperic.hq.hqapi1.types.ServerConfigResponse;
import org.hyperic.hq.hqapi1.types.ServerConfig;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.springframework.stereotype.Component;
@Component
public class ServerConfigCommand extends AbstractCommand {

    private static String CMD_GET           = "get";
    private static String CMD_GET_PARAMETER = "getParameter";
    private static String CMD_SET           = "set";
    private static String CMD_SET_PARAMETER = "setParameter";

    private static String[] COMMANDS = { CMD_GET, CMD_GET_PARAMETER,
                                         CMD_SET, CMD_SET_PARAMETER};

    private static String OPT_KEY   = "key";
    private static String OPT_VALUE = "value";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }
    
    public String getName() {
        return "serverConfig";
     }

    public int handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return 1;
        }

        if (args[0].equals(CMD_GET)) {
            get(trim(args));
        } else if (args[0].equals(CMD_GET_PARAMETER)) {
            getParameter(trim(args));
        } else if (args[0].equals(CMD_SET)) {
            set(trim(args));
        } else if (args[0].equals(CMD_SET_PARAMETER)) {
            setParameter(trim(args));
        } else {
            printUsage();
            return 1;
        }
        return 0;
    }

    private void get(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ServerConfigApi configApi = api.getServerConfigApi();

        ServerConfigResponse response = configApi.getConfig();
        checkSuccess(response);

        XmlUtil.serialize(response, System.out, Boolean.TRUE);
    }

    private void getParameter(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_KEY, "The configuration parameter to retreive").
                withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ServerConfigApi configApi = api.getServerConfigApi();

        String key = (String)getRequired(options, OPT_KEY);

        ServerConfigResponse response = configApi.getConfig();
        checkSuccess(response);

        for (ServerConfig c : response.getServerConfig()) {
            if (c.getKey().equals(key)) {
                System.out.println("Current value for " + key + " = " + c.getValue());
                return;
            }
        }
        System.err.print("Unknown configuration parameter " + key);
    }

    private void set(String[] args) throws Exception {

        OptionParser p = getOptionParser();
        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        ServerConfigApi serverConfigApi = api.getServerConfigApi();

        InputStream is = getInputStream(options);
        ServerConfigResponse resp = XmlUtil.deserialize(ServerConfigResponse.class, is);
        List<ServerConfig> config = resp.getServerConfig();

        StatusResponse response = serverConfigApi.setConfig(config);
        checkSuccess(response);

        System.out.println("Successfully updated HQ configuration.");
    }

    private void setParameter(String[] args) throws Exception {
        OptionParser p = getOptionParser();

        p.accepts(OPT_KEY, "The configuration parameter to set").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_VALUE, "The new value for the specified key").
                withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        ServerConfigApi configApi = api.getServerConfigApi();

        String key = (String)getRequired(options, OPT_KEY);
        String value = (String)getRequired(options, OPT_VALUE);

        ServerConfigResponse response = configApi.getConfig();
        checkSuccess(response);

        List<ServerConfig> config = response.getServerConfig();
        boolean found = false;
        for (ServerConfig c : config) {
            if (c.getKey().equals(key)) {
                c.setValue(value);
                found = true;
            }
        }

        if (!found) {
            System.err.print("Unknown configuration parameter " + key);
        } else {
            StatusResponse setResponse = configApi.setConfig(config);
            checkSuccess(setResponse);
            System.out.println("Successfully updated HQ configuration.");
        }
    }
}
