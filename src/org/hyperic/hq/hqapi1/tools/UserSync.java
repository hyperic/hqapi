package org.hyperic.hq.hqapi1.tools;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.GetUsersResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.SyncUsersResponse;

import java.util.List;

public class UserSync extends ToolsBase {

    private static void syncUsers(String[] args) throws Exception {

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

        GetUsersResponse resp = XmlUtil.deserialize(GetUsersResponse.class,
                                                    System.in);

        List<User> users = resp.getUser();

        SyncUsersResponse syncResponse = userApi.syncUsers(users);

        if (!isSuccess(syncResponse)) {
            System.exit(-1);
        }

        System.out.println("Successfully synced " + users.size() + " users.");
    }

    public static void main(String[] args) throws Exception {
        try {
            syncUsers(args);
        } catch (Exception e) {
            System.err.println("Error listing users: " + e.getMessage());
            System.exit(-1);
        }
    }
}
