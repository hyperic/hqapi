package org.hyperic.hq.hqapi1.tools;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.UsersResponse;
import org.hyperic.hq.hqapi1.types.UserResponse;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class UserList extends ToolsBase {

    private static String OPT_ID = "id";
    private static String OPT_NAME = "name";

    private static void listUsers(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "If specified, return the user with the given id.").
                withRequiredArg().ofType(Integer.class);
        p.accepts(OPT_NAME, "If specified, return the user with the given name").
                withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        UserApi userApi = api.getUserApi();

        UsersResponse users;

        if (options.has(OPT_ID)) {
            Integer id = (Integer)options.valueOf(OPT_ID);
            UserResponse user = userApi.getUser(id);
            checkSuccess(user);
            users = new UsersResponse();
            users.setStatus(ResponseStatus.SUCCESS);
            users.getUser().add(user.getUser());
        } else if (options.has(OPT_NAME)) {
            String name = (String)options.valueOf(OPT_NAME);
            UserResponse user = userApi.getUser(name);
            checkSuccess(user);
            users = new UsersResponse();
            users.setStatus(ResponseStatus.SUCCESS);
            users.getUser().add(user.getUser());
        } else {
            users = userApi.getUsers();
            checkSuccess(users);
        }

        XmlUtil.serialize(users, System.out, Boolean.TRUE);
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
