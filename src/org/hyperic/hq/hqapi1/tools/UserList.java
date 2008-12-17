package org.hyperic.hq.hqapi1.tools;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.UsersResponse;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class UserList extends ToolsBase {

    private static void listUsers(String[] args) throws Exception {

        OptionParser p = getOptionParser();
        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        UserApi userApi = api.getUserApi();

        UsersResponse users = userApi.getUsers();
        checkSuccess(users);

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
