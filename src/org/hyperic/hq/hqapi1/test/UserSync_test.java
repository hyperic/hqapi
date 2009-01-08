package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.UserApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.UserResponse;
import org.hyperic.hq.hqapi1.types.UsersResponse;

import java.util.ArrayList;
import java.util.List;

public class UserSync_test extends UserTestBase {

    public UserSync_test(String name) {
        super(name);
    }

    public void testSyncCreate() throws Exception {

        UserApi api = getUserApi();
        User u = generateTestUser();

        List<User> users = new ArrayList<User>();
        users.add(u);

        StatusResponse response = api.syncUsers(users);
        hqAssertSuccess(response);

        UserResponse getResponse = api.getUser(u.getName());
        hqAssertSuccess(getResponse);
    }

    public void testSyncUpdate() throws Exception {

        UserApi api = getUserApi();
        User u = generateTestUser();

        // Create a new user
        UserResponse createResponse = api.createUser(u, PASSWORD);
        hqAssertSuccess(createResponse);

        // Sync the user with new information
        User newUser = createResponse.getUser();

        String FIRST   = "Updated FirstName";
        String LAST    = "Updated LastName";
        String EMAIL   = "Updated EmailAddress";
        String DEPT    = "Updated Department";
        String SMS     = "Updated SMS";
        String PHONE   = "Updated Phone";
        boolean ACTIVE = !newUser.isActive();
        boolean HTML   = !newUser.isHtmlEmail();
        String HASHED_PWD = "UpdatedPassword";
        
        newUser.setFirstName(FIRST);
        newUser.setLastName(LAST);
        newUser.setEmailAddress(EMAIL);
        newUser.setDepartment(DEPT);
        newUser.setSMSAddress(SMS);
        newUser.setPhoneNumber(PHONE);
        newUser.setActive(ACTIVE);
        newUser.setHtmlEmail(HTML);
        newUser.setPasswordHash(HASHED_PWD);
        
        List<User> users = new ArrayList<User>();
        users.add(newUser);
        StatusResponse syncResponse = api.syncUsers(users);
        hqAssertSuccess(syncResponse);

        // Assert the fields were properly updated
        UserResponse getResponse = api.getUser(newUser.getId());
        hqAssertSuccess(getResponse);

        User syncedUser = getResponse.getUser();
        assertEquals(FIRST,  syncedUser.getFirstName());
        assertEquals(LAST,   syncedUser.getLastName());
        assertEquals(EMAIL,  syncedUser.getEmailAddress());
        assertEquals(DEPT,   syncedUser.getDepartment());
        assertEquals(SMS,    syncedUser.getSMSAddress());
        assertEquals(PHONE,  syncedUser.getPhoneNumber());
        assertEquals(ACTIVE, syncedUser.isActive());
        assertEquals(HTML,   syncedUser.isHtmlEmail());
        assertEquals(HASHED_PWD, syncedUser.getPasswordHash());
    }

    public void testMultiSyncCreateAndUpdate() throws Exception {

        UserApi api = getUserApi();

        // Create 10 users via sync()
        List<User> toCreate = new ArrayList<User>();
        for (int i = 0; i < 10; i++) {
            User u = generateTestUser();
            toCreate.add(u);
        }

        StatusResponse syncCreateResponse = api.syncUsers(toCreate);
        hqAssertSuccess(syncCreateResponse);

        // Update user's firstname.
        UsersResponse getUsersRespose = api.getUsers();
        hqAssertSuccess(getUsersRespose);

        String FIRST = "Synced FirstName";

        List<User> users = getUsersRespose.getUser();
        for (User u : users) {
            // Only sync users created by the test suite
            if (u.getName().startsWith(TESTUSER_NAME_PREFIX)) {
                u.setFirstName(FIRST);
            }
        }

        StatusResponse syncResponse = api.syncUsers(users);
        hqAssertSuccess(syncResponse);

        UsersResponse getSyncedResponse = api.getUsers();
        hqAssertSuccess(getSyncedResponse);
        for (User u : getSyncedResponse.getUser()) {
            // See above, only test suite users are synced
            if (u.getName().startsWith(TESTUSER_NAME_PREFIX)) {
                assertEquals(FIRST, u.getFirstName());
            }
        }
    }
}
