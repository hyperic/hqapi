package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLRole_test extends WADLTestBase {

    public void testRoleList() throws Exception {
        HttpLocalhost8080HquHqapi1.RoleListHqu roleList = new HttpLocalhost8080HquHqapi1.RoleListHqu();

        RolesResponse response = roleList.getAsRolesResponse();
        hqAssertSuccess(response);
    }

    public void testRoleGet() throws Exception {
        HttpLocalhost8080HquHqapi1.RoleGetHqu roleGet = new HttpLocalhost8080HquHqapi1.RoleGetHqu();

        RoleResponse response = roleGet.getAsRoleResponse("Some role");
        hqAssertFailure(response);
    }

    public void testRoleDelete() throws Exception {
        HttpLocalhost8080HquHqapi1.RoleDeleteHqu roleDelete = new HttpLocalhost8080HquHqapi1.RoleDeleteHqu();

        StatusResponse response = roleDelete.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(response);
    }

    public void testRoleSync() throws Exception {
        HttpLocalhost8080HquHqapi1.RoleSyncHqu roleSync = new HttpLocalhost8080HquHqapi1.RoleSyncHqu();
        RolesRequest request = new RolesRequest();

        StatusResponse response = roleSync.postApplicationXmlAsStatusResponse(request);
        hqAssertSuccess(response);
    }
}
