package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLRole_test extends WADLTestBase {

    public void testRoleList() throws Exception {
        Endpoint.RoleListHqu roleList = new Endpoint.RoleListHqu();

        RolesResponse response = roleList.getAsRolesResponse();
        hqAssertSuccess(response);
    }

    public void testRoleGet() throws Exception {
        Endpoint.RoleGetHqu roleGet = new Endpoint.RoleGetHqu();

        RoleResponse response = roleGet.getAsRoleResponse("Some role");
        hqAssertFailure(response);
    }

    public void testRoleDelete() throws Exception {
        Endpoint.RoleDeleteHqu roleDelete = new Endpoint.RoleDeleteHqu();

        StatusResponse response = roleDelete.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(response);
    }

    public void testRoleSync() throws Exception {
        Endpoint.RoleSyncHqu roleSync = new Endpoint.RoleSyncHqu();
        RolesRequest request = new RolesRequest();

        StatusResponse response = roleSync.postAsStatusResponse(request);
        hqAssertSuccess(response);
    }
}
