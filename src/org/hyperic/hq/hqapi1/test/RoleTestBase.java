package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.Role;

import java.util.Random;

public class RoleTestBase extends HQApiTestBase {

    static final String GUEST_ROLENAME = "Guest Role";
    static final String SUPER_USER_ROLENAME = "Super User Role";

    static final String TESTROLE_NAME_PREFIX = "API Test Role ";
    static final String TESTROLE_DESCRIPTION = "API Test Role Description";

    public RoleTestBase(String name) {
        super(name);
    }

    public RoleApi getRoleApi() {
        return getApi().getRoleApi();
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
}
