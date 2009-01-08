package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;
import org.hyperic.hq.hqapi1.types.UsersRequest;
import org.hyperic.hq.hqapi1.types.UsersResponse;

import java.util.List;

public class UserCopy_test extends UserTestBase {

    public UserCopy_test(String name) {
        super(name);
    }
  
    /**
     * This test will create a user then deletes the same user and re-create the user 
     * using the same User object that had created initially and test the user login. 
     */
    public void testCopy() throws Exception {

        UserApi api = getUserApi();

        User user = generateTestUser();
        
        //create the new user     
        UserResponse createResponse = api.createUser(user, PASSWORD);
        assertEquals(ResponseStatus.SUCCESS, createResponse.getStatus());
        
        // Assert the new user exists
        UserResponse getResponse = api.getUser(user.getName());
        hqAssertSuccess(getResponse);
        
        // store the user that was just created before deleting 
        User createdUser = getResponse.getUser();

        // Delete the user that was just created.
        StatusResponse deleteResponse = api.deleteUser(createdUser.getId());
        hqAssertSuccess(deleteResponse);

        // Assert that user is no longer available
        UserResponse getResponse2 = api.getUser(createdUser.getId());
        hqAssertFailureObjectNotFound(getResponse2);

        // create the user using the existing user object
        UsersRequest syncRequest = new UsersRequest();
        List<User> users = syncRequest.getUser();
        users.add(createdUser);
        StatusResponse syncResponse = api.syncUsers(users);
        assertEquals(ResponseStatus.SUCCESS, syncResponse.getStatus());
        
        // test login
        UserApi api2 = getUserApi(user.getName(), PASSWORD);
        UsersResponse getResponse3 = api2.getUsers();
        hqAssertSuccess(getResponse3);        
    }
}
