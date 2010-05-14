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

import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.Resource;

import java.util.List;
import java.util.ArrayList;

public class AlertDefinitionGetByParent_test extends AlertDefinitionTestBase {

    public AlertDefinitionGetByParent_test(String name) {
        super(name);
    }

    public void testGetByParentBadId() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        AlertDefinition d = new AlertDefinition();
        d.setId(Integer.MAX_VALUE);
        AlertDefinitionsResponse response = api.getAlertDefinitions(d);
        hqAssertFailureObjectNotFound(response);   
    }

    public void testGetByParentNotTypeAlert() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        d = response.getAlertDefinition().get(0);

        // Test get by parent
        AlertDefinitionsResponse children = api.getAlertDefinitions(d);
        hqAssertFailureInvalidParameters(children);

        // Cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testValidGetByParent() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        d.getAlertCondition().add(AlertDefinitionBuilder.createPropertyCondition(true, "myProp"));
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);

        AlertDefinitionsResponse response = api.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        d = response.getAlertDefinition().get(0);

        // Test get by parent
        AlertDefinitionsResponse children = api.getAlertDefinitions(d);
        hqAssertSuccess(children);
        assertTrue("Could not find children for parent id " + d.getId(),
                   children.getAlertDefinition().size() > 0);

        // Cleanup
        cleanup(response.getAlertDefinition());    
    }
}
