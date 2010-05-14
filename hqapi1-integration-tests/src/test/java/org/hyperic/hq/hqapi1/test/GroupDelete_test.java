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

import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class GroupDelete_test extends GroupTestBase {

    public GroupDelete_test(String name) {
        super(name);
    }

    public void testDeleteInvalidId() throws Exception {
        GroupApi api = getApi().getGroupApi();

        StatusResponse resp = api.deleteGroup(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(resp);
    }

    public void testDelete() throws Exception {

        GroupApi groupApi = getApi().getGroupApi();

        Group g = generateTestGroup();

        GroupResponse response = groupApi.createGroup(g);
        hqAssertSuccess(response);

        GroupResponse groupResponse = groupApi.getGroup(g.getName());
        hqAssertSuccess(groupResponse);

        Group createdGroup = groupResponse.getGroup();
        validateGroup(groupResponse.getGroup());

        StatusResponse deleteResponse = groupApi.deleteGroup(createdGroup.getId());
        hqAssertSuccess(deleteResponse);
    }

    public void testDeleteSystemGroup() throws Exception {

        GroupApi groupApi = getApi().getGroupApi();

        StatusResponse deleteResponse = groupApi.deleteGroup(0);
        hqAssertFailureNotSupported(deleteResponse);        
    }
}
