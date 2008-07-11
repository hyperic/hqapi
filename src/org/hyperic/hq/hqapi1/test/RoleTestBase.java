package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;

public class RoleTestBase extends HQApiTestBase {

    public RoleTestBase(String name) {
        super(name);
    }

    public RoleApi getRoleApi() {
        return getApi().getRoleApi();
    }
}
