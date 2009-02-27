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

package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.RoleApi;
import org.hyperic.hq.hqapi1.types.Operation;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.RolesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoleTestBase extends UserTestBase {

    static final String GUEST_ROLENAME = "Guest Role";
    static final String SUPER_USER_ROLENAME = "Super User Role";

    static final String TESTROLE_NAME_PREFIX = "API Test Role ";
    static final String TESTROLE_DESCRIPTION = "API Test Role Description";

    // Collections of Operations for test purposes.
    static final List<Operation> VIEW_OPS;
    static final List<Operation> MODIFY_OPS;

    static {
        VIEW_OPS = new ArrayList<Operation>();
        VIEW_OPS.add(Operation.VIEW_APPLICATION);
        VIEW_OPS.add(Operation.VIEW_PLATFORM);
        VIEW_OPS.add(Operation.VIEW_RESOURCE_GROUP);
        VIEW_OPS.add(Operation.VIEW_ROLE);
        VIEW_OPS.add(Operation.VIEW_SERVER);
        VIEW_OPS.add(Operation.VIEW_SERVICE);
        VIEW_OPS.add(Operation.VIEW_SUBJECT);

        MODIFY_OPS = new ArrayList<Operation>();
        MODIFY_OPS.add(Operation.MODIFY_APPLICATION);
        MODIFY_OPS.add(Operation.MODIFY_ESCALATION);
        MODIFY_OPS.add(Operation.MODIFY_PLATFORM);
        MODIFY_OPS.add(Operation.MODIFY_RESOURCE_GROUP);
        MODIFY_OPS.add(Operation.MODIFY_RESOURCE_TYPE);
        MODIFY_OPS.add(Operation.MODIFY_ROLE);
        MODIFY_OPS.add(Operation.MODIFY_SERVER);
        MODIFY_OPS.add(Operation.MODIFY_SERVICE);
        MODIFY_OPS.add(Operation.MODIFY_SUBJECT);
    }

    public RoleTestBase(String name) {
        super(name);
    }

    public RoleApi getRoleApi() {
        return getApi().getRoleApi();
    }

    public RoleApi getRoleApi(String name, String password){
    	return getApi(name, password).getRoleApi();
    }
    /**
     * Generate a valid Role object that's guaranteed to have a unique Name
     * @return A valid Role object.
     */
    public Role generateTestRole() {

        Random r = new Random();

        Role role = new Role();
        role.setName(TESTROLE_NAME_PREFIX + r.nextInt());
        role.setDescription(TESTROLE_DESCRIPTION);

        return role;
    }

    /**
     * Clean up test roles after each test run.
     */
    public void tearDown() throws Exception {

        RoleApi api = getRoleApi();
        RolesResponse response = api.getRoles();

        for (Role r : response.getRole()) {
            if (r.getName().startsWith(TESTROLE_NAME_PREFIX)) {
                api.deleteRole(r.getId());
            }
        }

        super.tearDown();
    }
}
