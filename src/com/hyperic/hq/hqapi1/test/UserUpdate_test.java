package com.hyperic.hq.hqapi1.test;

import com.hyperic.hq.hqapi1.UserApi;
import com.hyperic.hq.hqapi1.types.User;
import com.hyperic.hq.hqapi1.types.CreateUserResponse;
import com.hyperic.hq.hqapi1.types.ResponseStatus;
import com.hyperic.hq.hqapi1.types.UpdateUserResponse;
import com.hyperic.hq.hqapi1.types.GetUserResponse;

public class UserUpdate_test extends UserTestBase {

    public UserUpdate_test(String name) {
        super(name);
    }

    public void testUpdate() throws Exception {

        UserApi api = getUserApi();

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
        UpdateUserResponse updateResponse = api.updateUser(user);
        assertEquals(ResponseStatus.SUCCESS, updateResponse.getStatus());

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

    public void testUpdateNoPermission() throws Exception {

        UserApi api = getUserApi();

        User user = generateTestUser();

        CreateUserResponse createResponse = api.createUser(user, PASSWORD);
        assertEquals(ResponseStatus.SUCCESS, createResponse.getStatus());

        // Reconnect as the new user

        UserApi apiNewUser = getUserApi(user.getName(), PASSWORD);

        User u = new User();
        u.setName("hqadmin");
        u.setFirstName("Updated FirstName");

        UpdateUserResponse updateResponse = apiNewUser.updateUser(u);
        assertEquals(ResponseStatus.FAILURE, updateResponse.getStatus());
        assertEquals("PermissionDenied", updateResponse.getError().getErrorCode());
    }
}
