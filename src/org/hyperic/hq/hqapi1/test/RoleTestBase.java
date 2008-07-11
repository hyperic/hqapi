package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;

public class RoleTestBase extends HQApiTestBase {

    static final String GUEST_ROLENAME = "Guest Role";
    static final String SUPER_USER_ROLENAME = "Super User Role";

    public RoleTestBase(String name) {
        super(name);
    }

    public RoleApi getRoleApi() {
        return getApi().getRoleApi();
    }
}
