package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;

public class GroupDelete_test extends GroupTestBase {

    public GroupDelete_test(String name) {
        super(name);
    }

    public void testDeleteInvalidId() throws Exception {
        GroupApi api = getApi().getGroupApi();

        StatusResponse resp = api.deleteGroup(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testDelete() throws Exception {

        GroupApi groupApi = getApi().getGroupApi();

        Group g = generateTestGroup();

        GroupResponse response = groupApi.createGroup(g);
        hqAssertSuccess(response);

        GroupResponse groupResponse = groupApi.getGroup(g.getName());
        hqAssertSuccess(groupResponse);

        Group createdGroup = groupResponse.getGroup();
        validateGroup(groupResponse.getGroup());

        StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup.getId());
        hqAssertSuccess(deleteResponse);
    }
}
