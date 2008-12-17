package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;

public class UserCreate_test extends UserTestBase {

    public UserCreate_test(String name) {
        super(name);
    }

    public void testCreateValidParameters() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();

        UserResponse response = api.createUser(user, PASSWORD);

        hqAssertSuccess(response);

        User createdUser = response.getUser();
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getFirstName(), createdUser.getFirstName());
        assertEquals(user.getLastName(), createdUser.getLastName());
    }

    public void testCreateDuplicate() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();

        UserResponse response = api.createUser(user, PASSWORD);
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());

        // Attempt to create the same user again
        UserResponse response2 = api.createUser(user, PASSWORD);
        hqAssertFailureObjectExists(response2);
    }

    public void testCreateEmptyUser() throws Exception {
        UserApi api = getUserApi();

        User user = new User();
        UserResponse response = api.createUser(user, PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyName() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();
        user.setName(null);

        UserResponse response = api.createUser(user, PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyFirstName() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();
        user.setFirstName(null);

        UserResponse response = api.createUser(user, PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyLastName() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();
        user.setLastName(null);

        UserResponse response = api.createUser(user, PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyEmailAddress() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();
        user.setEmailAddress(null);

        UserResponse response = api.createUser(user, PASSWORD);

        hqAssertFailureInvalidParameters(response);
    }

    public void testCreateEmptyPassword() throws Exception {
        UserApi api = getUserApi();

        User user = generateTestUser();

        UserResponse response = api.createUser(user, null);

        hqAssertFailureInvalidParameters(response);
    }
    
    public void testCreateValidNoPermission() throws Exception {
    	UserApi api = getUserApi();

        User user = generateTestUser();

        UserResponse response = api.createUser(user, PASSWORD);

        hqAssertSuccess(response);
        
        UserApi api1 = getUserApi(user.getName(), PASSWORD);
        
        User user1 = generateTestUser();
        
        UserResponse response1 = api1.createUser(user1, PASSWORD);
        
        hqAssertFailurePermissionDenied(response1);
    }
}
