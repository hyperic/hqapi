package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.HQApi;
import com.hyperic.hq.hqapi1.jaxb.User;
import com.hyperic.hq.hqapi1.jaxb.CreateUserResponse;
import com.hyperic.hq.hqapi1.jaxb.ResponseStatus;
import com.hyperic.hq.hqapi1.jaxb.SyncUserResponse;
import com.hyperic.hq.hqapi1.jaxb.GetUserResponse;

public class UserSync_test extends UserTestBase {

    public UserSync_test(String name) {
        super(name);
    }

    public void testSync() throws Exception {

        HQApi api = getApi();

        User user = generateTestUser();

        CreateUserResponse createResponse = api.createUser(user, PASSWORD);
        assertEquals(ResponseStatus.SUCCESS, createResponse.getStatus());

        String FIRST   = "Updated FirstName";
        String LAST    = "Updated LastName";
        String EMAIL   = "Updated EmailAddress";
        String DEPT    = "Updated Department";
        String SMS     = "Updated SMS";
        String PHONE   = "Updated Phone";
        boolean ACTIVE = !user.isActive();
        boolean HTML   = !user.isHtmlEmail();

        user.setFirstName(FIRST);
        user.setLastName(LAST);
        user.setEmailAddress(EMAIL);
        user.setDepartment(DEPT);
        user.setSMSAddress(SMS);
        user.setPhoneNumber(PHONE);
        user.setActive(ACTIVE);
        user.setHtmlEmail(HTML);
        SyncUserResponse syncResponse = api.syncUser(user);
        assertEquals(ResponseStatus.SUCCESS, syncResponse.getStatus());

        // Test the name has been updated
        GetUserResponse getResponse = api.getUser(user.getName());
        assertEquals(ResponseStatus.SUCCESS, getResponse.getStatus());
        User u = getResponse.getUser();
        assertEquals(FIRST,  u.getFirstName());
        assertEquals(LAST,   u.getLastName());
        assertEquals(EMAIL,  u.getEmailAddress());
        assertEquals(DEPT,   u.getDepartment());
        assertEquals(SMS,    u.getSMSAddress());
        assertEquals(PHONE,  u.getPhoneNumber());
        assertEquals(ACTIVE, u.isActive());
        assertEquals(HTML,   u.isHtmlEmail());
    }

    public void testSyncNoPermission() throws Exception {

        HQApi api = getApi();

        User user = generateTestUser();

        CreateUserResponse createResponse = api.createUser(user, PASSWORD);
        assertEquals(ResponseStatus.SUCCESS, createResponse.getStatus());

        // Reconnect as the new user

        HQApi apiNewUser = getApi(user.getName(), PASSWORD);

        User u = new User();
        u.setName("hqadmin");
        u.setFirstName("Updated FirstName");

        SyncUserResponse syncResponse = apiNewUser.syncUser(u);
        assertEquals(ResponseStatus.FAILURE, syncResponse.getStatus());
        assertEquals("PermissionDenied", syncResponse.getError().getErrorCode());
    }
}
