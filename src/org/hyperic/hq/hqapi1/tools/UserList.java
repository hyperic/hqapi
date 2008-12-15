package org.hyperic.hq.hqapi1.tools;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.GetUsersResponse;
import org.hyperic.hq.hqapi1.types.User;

public class UserList extends ToolsBase {

    private static void listUsers(String[] args) throws Exception {

        Parser p = getParser();

        try {
            p.parse(args);
        } catch (Exception e) {
            System.err.println("Error parsing command line: " + e.getMessage());
            System.exit(-1);
        }

        if (Boolean.TRUE.equals(p.getOptionValue(OPT_HELP))) {
            p.printUsage();
            System.exit(0);
        }

        HQApi api = getApi(p);
        UserApi userApi = api.getUserApi();

        GetUsersResponse users = userApi.getUsers();
        if (!isSuccess(users)) {
            System.exit(-1);
        }

        XmlUtil.serialize(users, System.out);
    }

    public static void main(String[] args) throws Exception {
        try {
            listUsers(args);
        } catch (Exception e) {
            System.err.println("Error listing users: " + e.getMessage());
            System.exit(-1);
        }
    }
}
