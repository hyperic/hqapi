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
import org.hyperic.hq.hqapi1.types.GroupsResponse;
import org.hyperic.hq.hqapi1.types.Resource;

import java.util.ArrayList;
import java.util.List;

public class GroupGet_test extends GroupTestBase {

    private final int SYNC_NUM = 3;

    public GroupGet_test(String name) {
        super(name);
    }

    public void testGet() throws Exception {
        GroupApi api = getApi().getGroupApi();

        List<Group> groups = new ArrayList<Group>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Group g = generateTestGroup();
            groups.add(g);
        }

        GroupsResponse response = api.syncGroups(groups);
        hqAssertSuccess(response);

        GroupsResponse getResponse = api.getGroups();
        hqAssertSuccess(response);

        for (Group g : getResponse.getGroup()) {
            validateGroup(g);
        }

        // Remove original synced groups
        cleanup(response.getGroup());
    }

    public void testGetGroupById() throws Exception {

        GroupApi api = getApi().getGroupApi();

        List<Group> groups = new ArrayList<Group>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Group g = generateTestGroup();
            groups.add(g);
        }

        GroupsResponse response = api.syncGroups(groups);
        hqAssertSuccess(response);

        for (Group g : response.getGroup()) {
            // Re-lookup the group by id
            GroupResponse gResponse = api.getGroup(g.getId());
            hqAssertSuccess(gResponse);
            validateGroup(gResponse.getGroup());
        }

        cleanup(response.getGroup());
    }

    public void testGetGroupInvalidId() throws Exception {

        GroupApi api = getApi().getGroupApi();

        GroupResponse groupResponse = api.getGroup(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(groupResponse);
    }

    public void testGetGroupByName() throws Exception {

        GroupApi api = getApi().getGroupApi();

        List<Group> groups = new ArrayList<Group>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Group g = generateTestGroup();
            groups.add(g);
        }

        GroupsResponse response = api.syncGroups(groups);
        hqAssertSuccess(response);

        for (Group g : response.getGroup()) {
            // Re-lookup the group by name
            GroupResponse gResponse = api.getGroup(g.getName());
            hqAssertSuccess(gResponse);
            validateGroup(gResponse.getGroup());
        }

        cleanup(response.getGroup());
    }

    public void getGetGroupByInvalidName() throws Exception {

        GroupApi api = getApi().getGroupApi();

        GroupResponse groupResponse = api.getGroup("Non-existant group");
        hqAssertFailureObjectNotFound(groupResponse);
    }

    public void testGetCompatibleGroups() throws Exception {

        GroupApi api = getApi().getGroupApi();
        Resource platform = getLocalPlatformResource(false, false);

        List<Group> groups = new ArrayList<Group>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Group g = generateTestGroup();
            g.setResourcePrototype(platform.getResourcePrototype());
            groups.add(g);
        }

        GroupsResponse syncResponse = api.syncGroups(groups);
        hqAssertSuccess(syncResponse);

        GroupsResponse getCompatibleResponse = api.getCompatibleGroups();
        hqAssertSuccess(getCompatibleResponse);
        assertTrue("No compatible groups found!",
                   getCompatibleResponse.getGroup().size() > 0);
        for (Group g : getCompatibleResponse.getGroup()) {
            validateGroup(g);
            // All compatible groups have prototypes
            assertNotNull("No prototype found for " + g.getName(),
                          g.getResourcePrototype());
        }

        cleanup(syncResponse.getGroup());
    }

    public void testGetMixedGroups() throws Exception {

        GroupApi api = getApi().getGroupApi();

        List<Group> groups = new ArrayList<Group>();
        for (int i = 0; i < SYNC_NUM; i++) {
            Group g = generateTestGroup();
            groups.add(g);
        }

        GroupsResponse syncResponse = api.syncGroups(groups);
        hqAssertSuccess(syncResponse);

        GroupsResponse getMixedResponse = api.getMixedGroups();
        hqAssertSuccess(getMixedResponse);
        assertTrue("No compatible groups found!",
                   getMixedResponse.getGroup().size() > 0);
        for (Group g : getMixedResponse.getGroup()) {
            validateGroup(g);
            assertNull("Prototype is set for mixed group " + g.getName(),
                       g.getResourcePrototype());
        }

        cleanup(syncResponse.getGroup());
    }
}
