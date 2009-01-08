package org.hyperic.hq.hqapi1.test;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.ErrorCode;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.AgentsResponse;
import org.hyperic.hq.hqapi1.types.PingAgentResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.Response;
import org.hyperic.hq.hqapi1.types.ResponseStatus;

import java.util.List;
import java.util.Properties;

public class HQApiTestBase  extends TestCase {

    private static boolean _logConfigured = false;

    private static final String  HOST        = "localhost";
    private static final int     PORT        = 7080;
    private static final int     SSL_PORT    = 7443;
    private static final boolean IS_SECURE   = false;
    private static final String  USER        = "hqadmin";
    private static final String  PASSWORD    = "hqadmin";

    private Log _log = LogFactory.getLog(HQApiTestBase.class);

    public HQApiTestBase(String name) {
        super(name);
    }

    // Ripped out from PluginMain.java
    private static final String[][] LOG_PROPS = {
        { "log4j.appender.R", "org.apache.log4j.ConsoleAppender" },
        { "log4j.appender.R.layout.ConversionPattern", "%-5p [%t] [%c{1}] %m%n" },
        { "log4j.appender.R.layout", "org.apache.log4j.PatternLayout" }
    };

    private void configureLogging(String level) {
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

    protected Log getLog() {
        return _log;
    }

    public void setUp() throws Exception {
        super.setUp();

        if (!_logConfigured) {
            String level = System.getProperty("log");
            if (level != null) {
                configureLogging(level);
            } else {
                configureLogging("INFO");
            }
            _logConfigured = true;
        }
    }

    HQApi getApi() {
        return new HQApi(HOST, PORT, IS_SECURE, USER, PASSWORD);
    }

    HQApi getApi(boolean secure) {
        return new HQApi(HOST, SSL_PORT, secure, USER, PASSWORD);
    }

    HQApi getApi(String user, String password) {
        return new HQApi(HOST, PORT, IS_SECURE, user, password);
    }

    /**
     * Get the locally running agent.
     *
     * @return The locally running agent or null if one does not exist.
     * @throws Exception If a running agent could not be found.
     */
    protected Agent getRunningAgent() throws Exception {

        AgentApi api = getApi().getAgentApi();

        AgentsResponse response = api.getAgents();
        if (response.getStatus().equals(ResponseStatus.FAILURE)) {
            String err = "Error querying agents: " +
                    response.getError().getReasonText();
            _log.error(err);
            throw new Exception(err);
        }

        for (Agent a : response.getAgent()) {
            PingAgentResponse pingRespnse = api.pingAgent(a);
            if (pingRespnse.getStatus().equals(ResponseStatus.SUCCESS) &&
                pingRespnse.isUp()) {
                return a;
            }
        }

        String err = "No running agents found.";
        _log.error(err);
        throw new Exception(err);
    }

    protected Resource getLocalPlatformResource(boolean verbose,
                                                boolean children)
            throws Exception
    {
        Agent a = getRunningAgent();

        ResourceApi api = getApi().getResourceApi();
        ResourcesResponse resourceResponse = api.getResources(a, verbose,
                                                              children);
        hqAssertSuccess(resourceResponse);

        List<Resource> localPlatforms = resourceResponse.getResource();
        if (localPlatforms.size() == 0) {
            String err = "Unable to find platform associated with agent " +
                         a.getAddress() + ":" + a.getPort();
            getLog().error(err);
            throw new Exception(err);
        }
        return localPlatforms.get(0);
    }

    // Assert SUCCESS

    void hqAssertSuccess(Response response) {
        String error = (response.getError() != null) ?
            response.getError().getReasonText() : "";
        assertEquals(error, ResponseStatus.SUCCESS, response.getStatus());
    }

    // Assert a particular FAILURE
    
    void hqAssertFailureLoginFailure(Response response) {
        assertEquals(ResponseStatus.FAILURE, response.getStatus());
        assertEquals(response.getError().getReasonText(),
                     ErrorCode.LOGIN_FAILURE.getErrorCode(),
                     response.getError().getErrorCode());
    }

    void hqAssertFailureObjectNotFound(Response response) {
        assertEquals(ResponseStatus.FAILURE, response.getStatus());
        assertEquals(response.getError().getReasonText(),
                     ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                     response.getError().getErrorCode());
    }

    void hqAssertFailureObjectExists(Response response) {
        assertEquals(ResponseStatus.FAILURE, response.getStatus());
        assertEquals(response.getError().getReasonText(),
                     ErrorCode.OBJECT_EXISTS.getErrorCode(),
                     response.getError().getErrorCode());
    }

    void hqAssertFailureInvalidParameters(Response response) {
        assertEquals(ResponseStatus.FAILURE, response.getStatus());
        assertEquals(response.getError().getReasonText(),
                     ErrorCode.INVALID_PARAMETERS.getErrorCode(),
                     response.getError().getErrorCode());
    }

    // Unlikely, but here for completeness.
    void hqAssertFailureUnexpectedError(Response response) {
        assertEquals(ResponseStatus.FAILURE, response.getStatus());
        assertEquals(response.getError().getReasonText(),
                     ErrorCode.UNEXPECTED_ERROR.getErrorCode(),
                     response.getError().getErrorCode());
    }

    void hqAssertFailurePermissionDenied(Response response) {
        assertEquals(ResponseStatus.FAILURE, response.getStatus());
        assertEquals(response.getError().getReasonText(),
                     ErrorCode.PERMISSION_DENIED.getErrorCode(),
                     response.getError().getErrorCode());
    }

    void hqAssertFailureNotImplemented(Response response) {
        assertEquals(ResponseStatus.FAILURE, response.getStatus());
        assertEquals(response.getError().getReasonText(),
                     ErrorCode.NOT_IMPLEMENTED.getErrorCode(),
                     response.getError().getErrorCode());
    }

    void hqAssertFailureNotSupported(Response response) {
        assertEquals(ResponseStatus.FAILURE, response.getStatus());
        assertEquals(response.getError().getReasonText(),
                     ErrorCode.NOT_SUPPORTED.getErrorCode(),
                     response.getError().getErrorCode());
    }
}
