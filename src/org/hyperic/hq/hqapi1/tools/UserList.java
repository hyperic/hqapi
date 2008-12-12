package org.hyperic.hq.hqapi1.tools;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.GetUsersResponse;
import org.hyperic.hq.hqapi1.types.User;

import java.util.Map;

public class UserList extends ToolsBase {

    private static void listUsers(String[] args) throws Exception {

        Map<String,String> params = parseParameters(args);

        if (checkHelp(params)) {
            return;
        }

        HQApi api = getApi(params);
        UserApi userApi = api.getUserApi();

        GetUsersResponse users = userApi.getUsers();
        if (!isSuccess(users)) {
            return;
        }

        for (User u : users.getUser()) {
            System.out.println(u.getId() + " " + u.getName());
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            listUsers(args);
        } catch (Exception e) {
            System.err.println("Error listing users: " + e.getMessage());
        }
    }
}
