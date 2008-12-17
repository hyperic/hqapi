package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RolesResponse;
import org.hyperic.hq.hqapi1.types.Operation;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class RoleTestBase extends UserTestBase {

    static final String GUEST_ROLENAME = "Guest Role";
    static final String SUPER_USER_ROLENAME = "Super User Role";

    static final String TESTROLE_NAME_PREFIX = "API Test Role ";
    static final String TESTROLE_DESCRIPTION = "API Test Role Description";

    // Collections of Operations for test purposes.
    static final List<Operation> VIEW_OPS;
    static final List<Operation> MODIFY_OPS;

    static {
        VIEW_OPS = new ArrayList<Operation>();
        VIEW_OPS.add(Operation.VIEW_APPLICATION);
        VIEW_OPS.add(Operation.VIEW_PLATFORM);
        VIEW_OPS.add(Operation.VIEW_RESOURCE_GROUP);
        VIEW_OPS.add(Operation.VIEW_ROLE);
        VIEW_OPS.add(Operation.VIEW_SERVER);
        VIEW_OPS.add(Operation.VIEW_SERVICE);
        VIEW_OPS.add(Operation.VIEW_SUBJECT);

        MODIFY_OPS = new ArrayList<Operation>();
        MODIFY_OPS.add(Operation.MODIFY_APPLICATION);
        MODIFY_OPS.add(Operation.MODIFY_ESCALATION);
        MODIFY_OPS.add(Operation.MODIFY_PLATFORM);
        MODIFY_OPS.add(Operation.MODIFY_RESOURCE_GROUP);
        MODIFY_OPS.add(Operation.MODIFY_RESOURCE_TYPE);
        MODIFY_OPS.add(Operation.MODIFY_ROLE);
        MODIFY_OPS.add(Operation.MODIFY_SERVER);
        MODIFY_OPS.add(Operation.MODIFY_SERVICE);
        MODIFY_OPS.add(Operation.MODIFY_SUBJECT);
    }

    public RoleTestBase(String name) {
        super(name);
    }

    public RoleApi getRoleApi() {
        return getApi().getRoleApi();
    }

    public RoleApi getRoleApi(String name, String password){
    	return getApi(name, password).getRoleApi();
    }
    /**
     * Generate a valid Role object that's guaranteed to have a unique Name
     * @return A valid Role object.
     */
    public Role generateTestRole() {

        Random r = new Random();

        Role role = new Role();
        role.setName(TESTROLE_NAME_PREFIX + r.nextInt());
        role.setDescription(TESTROLE_DESCRIPTION);

        return role;
    }

    /**
     * Clean up test roles after each test run.
     */
    public void tearDown() throws Exception {

        RoleApi api = getRoleApi();
        RolesResponse response = api.getRoles();

        for (Role r : response.getRole()) {
            if (r.getName().startsWith(TESTROLE_NAME_PREFIX)) {
                api.deleteRole(r.getId());
            }
        }

        super.tearDown();
    }
}
