package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.UsersResponse;

public class SSL_test extends HQApiTestBase {

    public SSL_test(String name) {
        super(name);
    }

    public void testSSL() throws Exception {

        HQApi api = getApi(true);
        UserApi userApi = api.getUserApi();

        UsersResponse response = userApi.getUsers();
        hqAssertSuccess(response);

        assert(response.getUser().size() > 0);
    }
}
