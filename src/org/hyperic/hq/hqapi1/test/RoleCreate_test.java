package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.CreateRoleResponse;
import org.hyperic.hq.hqapi1.types.Operation;
import org.hyperic.hq.hqapi1.RoleApi;

import java.util.List;
import java.util.ArrayList;

public class RoleCreate_test extends RoleTestBase {

    public RoleCreate_test(String name) {
        super(name);
    }

    public void testRoleCreateNoOps() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        CreateRoleResponse response = api.createRole(r);
        hqAssertSuccess(response);
    }

    public void testRoleCreateViewOps() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        List<Operation> viewOps = new ArrayList<Operation>();
        viewOps.add(Operation.VIEW_APPLICATION);
        viewOps.add(Operation.VIEW_PLATFORM);
        viewOps.add(Operation.VIEW_RESOURCE_GROUP);
        viewOps.add(Operation.VIEW_ROLE);
        viewOps.add(Operation.VIEW_SERVER);
        viewOps.add(Operation.VIEW_SERVICE);
        viewOps.add(Operation.VIEW_SUBJECT);

        r.getOperation().addAll(viewOps);

        CreateRoleResponse response = api.createRole(r);
        hqAssertSuccess(response);

        Role role = response.getRole();
        for (Operation o : viewOps) {
            assertTrue("Created role does not contain operation " + o.value(),
                       role.getOperation().contains(o));
        }
    }

    public void testRoleCreateDuplicate() throws Exception {

        RoleApi api = getRoleApi();
        Role r = generateTestRole();

        CreateRoleResponse response = api.createRole(r);
        hqAssertSuccess(response);

        CreateRoleResponse existsResponse = api.createRole(r);
        hqAssertFailureObjectExists(existsResponse);
    }
}
