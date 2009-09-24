package org.hyperic.hq.hqapi1;

/**
 * The Hyperic HQ API.
 *
 * This is the main entry point into the HQ Api.
 */
public class HQApi {

    private final UserApi            _userApi;
    private final RoleApi            _roleApi;
    private final MetricApi          _metricApi;
    private final GroupApi           _groupApi;
    private final EscalationApi      _escalationApi;
    private final AutodiscoveryApi   _autodiscoveryApi;
    private final ResourceApi        _resourceApi;
    private final AgentApi           _agentApi;
    private final AlertDefinitionApi _alertDefinitionApi;
    private final ApplicationApi     _applApi;

    /**
     * @param host The hostname of the HQ Server to connect to.
     * @param port The port on the HQ server to connect to.
     * @param isSecure Set to true if connecting via SSL.
     * @param user The user to connect as.
     * @param password The password for the given user.
     */
    public HQApi(String host, int port, boolean isSecure, String user,
                 String password) {
        HQConnection connection = new HQConnection(host, port, isSecure,
                                                   user, password);
        _userApi          = new UserApi(connection);
        _roleApi          = new RoleApi(connection);
        _groupApi         = new GroupApi(connection);
        _metricApi        = new MetricApi(connection);
        _escalationApi    = new EscalationApi(connection);
        _autodiscoveryApi = new AutodiscoveryApi(connection);
        _resourceApi      = new ResourceApi(connection);
        _agentApi         = new AgentApi(connection);
        _alertDefinitionApi = new AlertDefinitionApi(connection);
        _applApi          = new ApplicationApi(connection);
    }

    /**
     * Add, remove and update users.
     * 
     * @return The API for operating on users.
     */
    public UserApi getUserApi() {
        return _userApi;
    }

    /**
     * Add, remove and list resource and resource prototypes.
     *
     * @return The API for operating on resource and resource prototypes.
     */
    public ResourceApi getResourceApi() {
        return _resourceApi;
    }
    
    /**
     * Add, remove and update roles.
     *
     * @return The API for operating on roles.
     */
    public RoleApi getRoleApi() {
        return _roleApi;
    }

    /**
     * Add, remove and modify groups.
     *
     * @return The API for operating on groups.
     */
    public GroupApi getGroupApi() {
        return _groupApi;
    }

    /**
     * Import, export measurement data and modify measurement schedules.
     *
     * @return The API for operating on measurement data and schedules.
     */
    public MetricApi getMetricApi() {
        return _metricApi;
    }

    /**
     * Add, remove and assign escalations.
     *
     * @return The API for operating on esclations.
     */
    public EscalationApi getEscalationApi() {
        return _escalationApi;
    }

    /**
     * List and approve auto-discovered resources.
     *
     * @return The API for operating on auto-discovered resources.
     */
    public AutodiscoveryApi getAutodiscoveryApi() {
        return _autodiscoveryApi;
    }

    /**
     * Get and ping agents.
     *
     * @return The API for operating on agent resources.
     */
    public AgentApi getAgentApi() {
        return _agentApi;
    }

    /**
     * Add, remove and list alert definitions.
     *
     * @return The API for operating on alert definitions.
     */
    public AlertDefinitionApi getAlertDefinitionApi() {
        return _alertDefinitionApi;
    }

    /**
     * List, create and update Applications.
     *
     * @return The API for operating on Applications.
     */
    public ApplicationApi getApplicationApi() {
        return _applApi;
    }
}
