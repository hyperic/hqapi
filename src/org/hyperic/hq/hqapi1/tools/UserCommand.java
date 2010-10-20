/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RoleResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;
import org.hyperic.hq.hqapi1.types.UsersResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
@Component
public class UserCommand extends AbstractCommand {

    private static String CMD_LIST = "list";
    private static String CMD_SYNC = "sync";
    private static String CMD_CREATE = "create";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC, CMD_CREATE };

    private static String OPT_ID   = "id";
    private static String OPT_NAME = "name";

    // create options
    private static String OPT_FIRSTNAME = "firstName";
    private static String OPT_LASTNAME = "lastName";
    private static String OPT_DEPARTMENT = "department";
    private static String OPT_EMAILADDRESS = "emailAddress";
    private static String OPT_SMSADDRESS = "SMSAddress";
    private static String OPT_PHONENUMBER = "phoneNumber";
    private static String OPT_HTMLEMAIL = "htmlEmail";
    private static String OPT_SETPASSWORD = "setPassword";
    private static String OPT_ASSIGNTOROLE = "assignToRole";
    private static String OPT_ASSIGNTOROLEID = "assignToRoleId";
    
    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }
    
    public String getName() {
        return "user";
     }

    public int handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return 1;
        }

        if (args[0].equals(CMD_LIST)) {
            list(trim(args));
        } else if (args[0].equals(CMD_SYNC)) {
            sync(trim(args));
        } else if (args[0].equals(CMD_CREATE)) {
        	create(trim(args));
        } else {
            printUsage();
            return 1;
        }
        return 0;
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

        InputStream is = getInputStream(options);
        UsersResponse resp = XmlUtil.deserialize(UsersResponse.class, is);
        List<User> users = resp.getUser();

        StatusResponse syncResponse = userApi.syncUsers(users);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + users.size() + " users.");
    }
    
    private void create(String[] args) throws Exception {
        String[] OPT_REQUIRED = { OPT_NAME, OPT_FIRSTNAME, OPT_LASTNAME, 
        						  OPT_EMAILADDRESS, OPT_SETPASSWORD };
        boolean ACTIVE = true;   // Assume a newly created user should be active
        
        OptionParser p = getOptionParser();

        p.accepts(OPT_NAME, "Create user with username specified. (required)").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_FIRSTNAME, "Specifies the firstName field for the user. (required)").
        		withRequiredArg().ofType(String.class);
        p.accepts(OPT_LASTNAME, "Specifies the lastName field for the user. (required)").
        		withRequiredArg().ofType(String.class);
        p.accepts(OPT_DEPARTMENT, "Specifies the department field for the user.").
        		withRequiredArg().ofType(String.class);
        p.accepts(OPT_EMAILADDRESS, "Specifies the emailAddress field for the user. (required)").
        		withRequiredArg().ofType(String.class);
        p.accepts(OPT_SMSADDRESS, "Specifies the SMSAddress email address field for the user.").
        		withRequiredArg().ofType(String.class);
        p.accepts(OPT_PHONENUMBER, "Specifies the phoneNumber field for the user.").
				withRequiredArg().ofType(String.class);
        p.accepts(OPT_SETPASSWORD, "Specifies the plain text password for the user. (prompted it not specified)").
				withRequiredArg().ofType(String.class);
        p.accepts(OPT_HTMLEMAIL, "Specifies to send html formatted email to the user.");
        p.accepts(OPT_ASSIGNTOROLE, "Assigns the user to the specified role by name").
        		withRequiredArg().ofType(String.class);
        p.accepts(OPT_ASSIGNTOROLEID, "Assigns the user to the specified role by id").
				withRequiredArg().ofType(Integer.class);
        OptionSet options = getOptions(p, args);

        if (!options.has(OPT_NAME) && !options.has(OPT_FIRSTNAME) && !options.has(OPT_LASTNAME) && !options.has(OPT_EMAILADDRESS)) {
        	System.err.println("All of required options: " + Arrays.toString(OPT_REQUIRED) + " missing");
    		System.exit(-1);
        }
        
        HQApi api = getApi(options);
        UserApi userApi = api.getUserApi();
        RoleApi roleApi = api.getRoleApi();
        Role role = new Role();
        RoleResponse roleResponse = null;
        
        String password = null;
    	
        // Validate the role name
        if (options.has(OPT_ASSIGNTOROLE)) {
        	roleResponse = roleApi.getRole(options.valueOf(OPT_ASSIGNTOROLE).toString());
        	checkSuccess(roleResponse);
        	role = roleResponse.getRole();
        } else if (options.has((OPT_ASSIGNTOROLEID))) {
        	roleResponse = roleApi.getRole((Integer)options.valueOf(OPT_ASSIGNTOROLEID));
        	checkSuccess(roleResponse);
        	role = roleResponse.getRole();
        }
        
    	if (!options.has(OPT_SETPASSWORD)) {
    		// prompt for password
    		try {
                char[] passwordArray = PasswordField.getPassword(System.in,
                                                                 "Enter password: ");
                char[] passwordConfirmArray = PasswordField.getPassword(System.in,
                												"Repeat password: ");
                if (String.valueOf(passwordArray).equals(String.valueOf(passwordConfirmArray))) {
                	password = String.valueOf(passwordArray);
                } else {
                	System.out.println("Passwords do not match!");
                	System.exit(-1);
                }
            } catch (IOException ioe) {
                System.err.println("Error reading password");
                System.exit(-1);
            }		
    	} else {
    		password = options.valueOf(OPT_SETPASSWORD).toString();
    	}
    	
        User user = new User();
        user.setName(options.valueOf(OPT_NAME).toString());
        user.setFirstName(options.valueOf(OPT_FIRSTNAME).toString());
        user.setLastName(options.valueOf(OPT_LASTNAME).toString());
        user.setEmailAddress(options.valueOf(OPT_EMAILADDRESS).toString());
        if (options.has(OPT_DEPARTMENT)) {
        	user.setDepartment(options.valueOf(OPT_DEPARTMENT).toString());
        }
        if (options.has(OPT_SMSADDRESS)) {
        	user.setSMSAddress(options.valueOf(OPT_SMSADDRESS).toString());
        }      
        if (options.has(OPT_EMAILADDRESS)) {
        	user.setEmailAddress(options.valueOf(OPT_EMAILADDRESS).toString());
        }      
        if (options.has(OPT_HTMLEMAIL)) {
        	System.out.println("Setting htmlEmail to true");
        	user.setHtmlEmail(true);
        } else {
        	System.out.println("Setting htmlEmail to false");
        	user.setHtmlEmail(false);
        }
        user.setActive(ACTIVE);
        
        UserResponse response = userApi.createUser(user, password);
        
        checkSuccess(response);
        System.out.println("Successfully created: " + user.getName() + " with id " + response.getUser().getId());
        
        if (role != null) {
        	role.getUser().add(user);
        	checkSuccess(roleApi.updateRole(role));
        	System.out.println("Successfully assigned " + user.getName() + " to role " + role.getName());
        }
        
    }
}
