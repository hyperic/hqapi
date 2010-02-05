/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008-2010], Hyperic, Inc.
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

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.types.AlertDefinitionResponse;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.Operation;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlertDefinitionGet_test extends AlertDefinitionTestBase {

    public AlertDefinitionGet_test(String name) {
        super(name);
    }

    public void testGetAllDefinitions() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        AlertDefinitionsResponse response = api.getAlertDefinitions(false);
        hqAssertSuccess(response);

        for (AlertDefinition d : response.getAlertDefinition()) {
            validateDefinition(d);
            // Parent will be null or a valid id, never 0.
            assertTrue("Invalid parent id " + d.getParent() +
                       " for definition " + d.getName(),
                       (d.getParent() == null || d.getParent() != 0));
            assertTrue("No Resource found for AlertDefinition",
                       d.getResource() != null);
        }
    }

    public void testGetAllDefinitionsExcludingTypeBased() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        AlertDefinitionsResponse response = api.getAlertDefinitions(true);
        hqAssertSuccess(response);

        for (AlertDefinition d : response.getAlertDefinition()) {
            validateDefinition(d);
            // Non-resource type alerts will have parent == null.
            assertTrue("Alert definition " + d.getName() + " has parent " +
                       d.getParent(), d.getParent() == null);
            assertTrue("No Resource found for AlertDefinition",
                       d.getResource() != null);            
        }
    }
    
    public void testGetNoViewPermission() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        // Create alert definition as hqadmin
        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);
        assertEquals(response.getAlertDefinition().size(), 1);
        AlertDefinition def = response.getAlertDefinition().get(0);
        validateDefinition(def);
        
        // Create user/group/role with insufficient permissions
        List<User> users = createTestUsers(1);
        User user = users.get(0);
        Role alertRole = createRole(Collections.singletonList(user),
                                    Collections.singletonList(Operation.MANAGE_PLATFORM_ALERTS));
        Group groupWithRole = createGroup(Collections.singletonList(platform),
                                          Collections.singletonList(alertRole));
        
        // get alert definition with insufficient permissions
        AlertDefinitionApi unprivApi = 
            getApi(user.getName(), TESTUSER_PASSWORD).getAlertDefinitionApi();
        AlertDefinitionResponse unprivResponse = unprivApi.getAlertDefinition(def.getId());
        hqAssertFailurePermissionDenied(unprivResponse);

        // cleanup
        cleanup(Collections.singletonList(def));
        deleteTestUsers(users);
        cleanupRole(alertRole);
        cleanupGroup(groupWithRole);
    }
}
