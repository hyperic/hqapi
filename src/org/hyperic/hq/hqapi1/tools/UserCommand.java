package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;
import org.hyperic.hq.hqapi1.types.UsersResponse;

import java.util.Arrays;
import java.util.List;

public class UserCommand extends Command {

    private static String CMD_LIST = "list";
    private static String CMD_SYNC = "sync";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC };

    private static String OPT_ID   = "id";
    private static String OPT_NAME = "name";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }

    protected void handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            System.exit(-1);
        }

        if (args[0].equals(CMD_LIST)) {
            list(trim(args));
        } else if (args[0].equals(CMD_SYNC)) {
            sync(trim(args));
        } else {
            printUsage();
            System.exit(-1);
        }
    }

    private void list(String[] args) throws Exception {

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

    private void sync(String[] args) throws Exception {

        OptionParser p = getOptionParser();
        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        UserApi userApi = api.getUserApi();

        UsersResponse resp = XmlUtil.deserialize(UsersResponse.class,
                                                 System.in);
        List<User> users = resp.getUser();

        StatusResponse syncResponse = userApi.syncUsers(users);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + users.size() + " users.");
    }
}
