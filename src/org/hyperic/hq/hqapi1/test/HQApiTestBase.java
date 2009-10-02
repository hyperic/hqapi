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

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.ErrorCode;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.AgentsResponse;
import org.hyperic.hq.hqapi1.types.PingAgentResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.Response;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;

import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public abstract class HQApiTestBase extends TestCase {

    private static boolean _logConfigured = false;

    private static final String  HOST        = "localhost";
    private static final int     PORT        = 7080;
    private static final int     SSL_PORT    = 7443;
    private static final boolean IS_SECURE   = false;
    private static final String  USER        = "hqadmin";
    private static final String  PASSWORD    = "hqadmin";

    static final String  TESTUSER_PASSWORD    = "apitest";
    static final String  TESTUSER_NAME_PREFIX = "apitest";
    static final String  TESTUSER_FIRSTNAME   = "API";
    static final String  TESTUSER_LASTNAME    = "Test";
    static final String  TESTUSER_EMAIL       = "apitest@hyperic.com";
    static final boolean TESTUSER_ACTIVE      = true;

    private Log _log = LogFactory.getLog(HQApiTestBase.class);

    private static Agent    _localAgent = null;

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

        if (_localAgent != null) {
            return _localAgent;
        }

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
                _localAgent = a;
                return _localAgent;
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

    public Resource createControllableResource(HQApi api)
        throws Exception
    {
        ResourceApi rApi = api.getResourceApi();

        ResourcePrototypeResponse protoResponse =
                rApi.getResourcePrototype("FileServer File");
        hqAssertSuccess(protoResponse);

        Resource localPlatform = getLocalPlatformResource(false, false);

        Map<String,String> config = new HashMap<String,String>();
        // TODO: Fix for windows
        config.put("path", "/usr/bin/true");

        Random r = new Random();
        String name = "Controllable-Resource-" + r.nextInt();

        ResourceResponse resourceCreateResponse =
                rApi.createService(protoResponse.getResourcePrototype(),
                                   localPlatform, name, config);

        hqAssertSuccess(resourceCreateResponse);

        return resourceCreateResponse.getResource();
    }

    public void cleanupControllableResource(HQApi api, Resource r)
        throws Exception
    {
        pauseTest();

        ResourceApi rApi = api.getResourceApi();

        StatusResponse response = rApi.deleteResource(r.getId());
        hqAssertSuccess(response);
    }

    /**
     * Generate a valid User object that's guaranteed to have a unique Name
     * @return A valid User object.
     */
    public User generateTestUser() {

        Random r = new Random();

        User user = new User();
        user.setName(TESTUSER_NAME_PREFIX + r.nextInt());
        user.setFirstName(TESTUSER_FIRSTNAME);
        user.setLastName(TESTUSER_LASTNAME);
        user.setEmailAddress(TESTUSER_EMAIL);
        user.setActive(TESTUSER_ACTIVE);
        return user;
    }

    /**
     * Create a List of Users.
     *
     * @param num The number of users to generate
     * @return The list of create Users
     * @exception Exception If an error occurs while creating the users.
     */
    public List<User> createTestUsers(int num) throws Exception {
        ArrayList<User> users = new ArrayList<User>();
        for (int i = 0; i < num; i++) {
            User u = generateTestUser();
            UserResponse createResponse = getApi().getUserApi().createUser(u, TESTUSER_PASSWORD);
            hqAssertSuccess(createResponse);
            users.add(createResponse.getUser());
        }
        return users;
    }

    public void deleteTestUsers(List<User> users) throws Exception {
        UserApi api = getApi().getUserApi();

        for (User u : users) {
            StatusResponse response = api.deleteUser(u.getId());
            hqAssertSuccess(response);
        }
    }
    
    /**
     * Need to pause test because HQ does not like it when resources
     * are modified or deleted so quickly after being created.
     * 
     * TODO: This issue needs to be fixed in the HQ Core code
     */
    void pauseTest(long timeMillis) {
        try {
            Thread.sleep(timeMillis);
        } catch (InterruptedException e) {
            // Ignore
        }
    }
    
    void pauseTest() {
        // default pause time
        pauseTest(2500);
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
