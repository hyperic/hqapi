package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.SyncRolesResponse;
import org.hyperic.hq.hqapi1.types.CreateRoleRequest;
import org.hyperic.hq.hqapi1.types.CreateRoleResponse;

import java.util.List;
import java.util.ArrayList;

public class RoleSyncRoles_test extends RoleTestBase {

    public RoleSyncRoles_test(String name) {
        super(name);
    }

    public void testSyncCreate() throws Exception {

        RoleApi api = getRoleApi();

        List<Role> roles = new ArrayList<Role>();
        for (int i = 0; i < 5; i++) {
            Role r = generateTestRole();
            r.getOperation().addAll(VIEW_OPS);
        }

        SyncRolesResponse response = api.syncRoles(roles);
        hqAssertSuccess(response);
    }

    public void testSyncUpdate() throws Exception {

        RoleApi api = getRoleApi();

        List<Role> createdRoles = new ArrayList<Role>();
        for (int i = 0; i < 5; i++) {
            Role r = generateTestRole();
            r.getOperation().addAll(VIEW_OPS);

            CreateRoleResponse createResponse = api.createRole(r);
            hqAssertSuccess(createResponse);
            createdRoles.add(createResponse.getRole());
        }

        final String UPDATED_ROLENAME    = TESTROLE_NAME_PREFIX + " (Updated)";
        final String UPDATED_DESCRIPTION = TESTROLE_DESCRIPTION + " (Updated)";

        for (Role r : createdRoles) {
            r.setName(UPDATED_ROLENAME);
            r.setDescription(UPDATED_DESCRIPTION);
            r.getOperation().clear();
            r.getOperation().addAll(MODIFY_OPS);
        }

        SyncRolesResponse response = api.syncRoles(createdRoles);
        hqAssertSuccess(response);
    }
}
