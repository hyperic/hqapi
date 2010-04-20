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
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertConditionType;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertControlStatus;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.Resource;

import java.util.ArrayList;
import java.util.List;

public class AlertDefinitionSyncControlCondition_test extends AlertDefinitionTestBase {

    public AlertDefinitionSyncControlCondition_test(String name) {
        super(name);
    }

    public void testValidControlConditon() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResource(platform);
        final String ACTION = "start";
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createControlCondition(true,
                                                              ACTION,
                                                              AlertControlStatus.COMPLETED));

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(def);
            for (AlertCondition c : def.getAlertCondition()) {
                assertNotNull("Condition was null", c);
                assertEquals(c.getType(), AlertConditionType.CONTROL.getType());
                assertEquals(c.getControlAction(), ACTION);
                assertEquals(c.getControlStatus(), AlertControlStatus.COMPLETED.getControlStatus());
            }
        }

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testValidControlConditonTypeAlert() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());
        final String ACTION = "start";
        d.getAlertCondition().add(
                AlertDefinitionBuilder.createControlCondition(true,
                                                              ACTION,
                                                              AlertControlStatus.COMPLETED));

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        for (AlertDefinition def : response.getAlertDefinition()) {
            validateDefinition(def);
            for (AlertCondition c : def.getAlertCondition()) {
                assertNotNull("Condition was null", c);
                assertEquals(c.getType(), AlertConditionType.CONTROL.getType());
                assertEquals(c.getControlAction(), ACTION);
                assertEquals(c.getControlStatus(), AlertControlStatus.COMPLETED.getControlStatus());
            }
        }

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testControlConditionMissingAttributes() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());

        AlertCondition c = new AlertCondition();
        c.setType(AlertDefinitionBuilder.AlertConditionType.CONTROL.getType());
        d.getAlertCondition().add(c);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);

        c.setControlAction("start");
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);

        c.setControlStatus(AlertDefinitionBuilder.AlertControlStatus.COMPLETED.getControlStatus());
        response = defApi.syncAlertDefinitions(definitions);
        hqAssertSuccess(response);

        // cleanup
        cleanup(response.getAlertDefinition());
    }

    public void testControlConditionInvalidControlStatus() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();

        Resource platform = getLocalPlatformResource(false, false);

        AlertDefinition d = generateTestDefinition();
        d.setResourcePrototype(platform.getResourcePrototype());

        AlertCondition c = new AlertCondition();
        c.setType(AlertDefinitionBuilder.AlertConditionType.CONTROL.getType());
        c.setControlAction("start");
        c.setControlStatus("Invalid Status");
        d.getAlertCondition().add(c);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(d);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertFailureInvalidParameters(response);       
    }
}
