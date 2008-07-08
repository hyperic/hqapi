package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.SyncUsersResponse;
import org.hyperic.hq.hqapi1.types.GetUserResponse;
import org.hyperic.hq.hqapi1.types.CreateUserResponse;
import org.hyperic.hq.hqapi1.types.GetUsersResponse;
import org.hyperic.hq.hqapi1.UserApi;

import java.util.List;
import java.util.ArrayList;

public class UserSync_test extends UserTestBase {

    public UserSync_test(String name) {
        super(name);
    }

    public void testSyncCreate() throws Exception {

        UserApi api = getUserApi();
        User u = generateTestUser();

        List<User> users = new ArrayList<User>();
        users.add(u);

        SyncUsersResponse response = api.syncUsers(users);
        hqAssertSuccess(response);

        GetUserResponse getResponse = api.getUser(u.getName());
        hqAssertSuccess(getResponse);
    }

    public void testSyncUpdate() throws Exception {

        UserApi api = getUserApi();
        User u = generateTestUser();

        // Create a new user
        CreateUserResponse createResponse = api.createUser(u, PASSWORD);
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

        newUser.setFirstName(FIRST);
        newUser.setLastName(LAST);
        newUser.setEmailAddress(EMAIL);
        newUser.setDepartment(DEPT);
        newUser.setSMSAddress(SMS);
        newUser.setPhoneNumber(PHONE);
        newUser.setActive(ACTIVE);
        newUser.setHtmlEmail(HTML);

        List<User> users = new ArrayList<User>();
        users.add(newUser);
        SyncUsersResponse syncResponse = api.syncUsers(users);
        hqAssertSuccess(syncResponse);

        // Assert the fields were properly updated
        GetUserResponse getResponse = api.getUser(newUser.getId());
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
    }

    public void testSync() throws Exception {

        UserApi api = getUserApi();
        GetUsersResponse getUsersRespose = api.getUsers();
        hqAssertSuccess(getUsersRespose);

        String FIRST = "Synced FirstName";

        List<User> users = getUsersRespose.getUser();
        for (User u : users) {
            // Only sync users created by the test suite
            if (u.getName().startsWith(TESTUSER_NAME_PREFIX)) {
                u.setFirstName(FIRST);
            }
        }

        SyncUsersResponse syncResponse = api.syncUsers(users);
        hqAssertSuccess(syncResponse);

        GetUsersResponse getSyncedResponse = api.getUsers();
        hqAssertSuccess(getSyncedResponse);
        for (User u : getSyncedResponse.getUser()) {
            // See above, only test suite users are synced
            if (u.getName().startsWith(TESTUSER_NAME_PREFIX)) {
                assertEquals(FIRST, u.getFirstName());
            }
        }
    }
}
