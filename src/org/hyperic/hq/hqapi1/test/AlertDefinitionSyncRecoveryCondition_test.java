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

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.AlertDefinitionApi;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder;
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertComparator;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.hyperic.hq.hqapi1.types.Metric;

import java.util.ArrayList;
import java.util.List;

public class AlertDefinitionSyncRecoveryCondition_test extends AlertDefinitionTestBase {

    public AlertDefinitionSyncRecoveryCondition_test(String name) {
        super(name);
    }

    public void testValidRecoveryCondition() throws Exception {

        Resource platform = getLocalPlatformResource(false, false);
        
        // Find availability metric for the passed in resource
        Metric m = findAvailabilityMetric(platform);

        AlertDefinition def = generateTestDefinition("Test Problem Alert");
        def.setResource(platform);
        final double THRESHOLD = 0;
        def.getAlertCondition().add(
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.EQUALS,
                                                                THRESHOLD));
        AlertDefinition recoveryDef = generateTestDefinition("Test Recovery Alert");
        recoveryDef.setResource(platform);
        AlertCondition threshold =
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.NOT_EQUALS,
                                                                THRESHOLD);
        AlertCondition recovery =
                AlertDefinitionBuilder.createRecoveryCondition(true, def);
        recoveryDef.getAlertCondition().add(threshold);
        recoveryDef.getAlertCondition().add(recovery);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(def);
        definitions.add(recoveryDef);
        
        List<AlertDefinition> createdDefinitions = createAlertDefinitions(definitions);
        
        // validate recovery alert definition
        validateRecoveryAlertDefinition(createdDefinitions);
        
        // cleanup
        cleanup(createdDefinitions);
    }

    public void testValidRecoveryConditionTypeAlert() throws Exception {

        Resource platform = getLocalPlatformResource(false, false);

        // Find availability metric for the passed in resource
        Metric m = findAvailabilityMetric(platform);

        AlertDefinition def = generateTestDefinition("Test Problem Alert");
        def.setResourcePrototype(platform.getResourcePrototype());
        final double THRESHOLD = 0;
        def.getAlertCondition().add(
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.EQUALS,
                                                                THRESHOLD));
        AlertDefinition recoveryDef = generateTestDefinition("Test Recovery Alert");
        recoveryDef.setResourcePrototype(platform.getResourcePrototype());
        AlertCondition threshold =
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.NOT_EQUALS,
                                                                THRESHOLD);
        AlertCondition recovery =
                AlertDefinitionBuilder.createRecoveryCondition(true, def);
        recoveryDef.getAlertCondition().add(threshold);
        recoveryDef.getAlertCondition().add(recovery);

        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(def);
        definitions.add(recoveryDef);

        List<AlertDefinition> createdDefinitions = createAlertDefinitions(definitions);
        
        // validate recovery alert definition
        validateRecoveryAlertDefinition(createdDefinitions);
                
        // cleanup
        cleanup(createdDefinitions);
    }

    public void testSyncRecoveryWithoutProblemDef() throws Exception {

        Resource platform = getLocalPlatformResource(false, false);

        // Find availability metric for the passed in resource
        Metric m = findAvailabilityMetric(platform);

        // First sync the problem definition
        AlertDefinition def = generateTestDefinition("Test Problem Alert");
        def.setResource(platform);
        final double THRESHOLD = 0;
        def.getAlertCondition().add(
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.EQUALS,
                                                                THRESHOLD));
        AlertDefinition problemDef = createAlertDefinition(def);
        validateDefinition(problemDef);

        // Next, sync the recovery
        AlertDefinition def2 = generateTestDefinition("Test Recovery Alert");
        def2.setResource(platform);
        AlertCondition threshold =
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.NOT_EQUALS,
                                                                THRESHOLD);
        AlertCondition recovery =
                AlertDefinitionBuilder.createRecoveryCondition(true, problemDef);
        def2.getAlertCondition().add(threshold);
        def2.getAlertCondition().add(recovery);

        AlertDefinition recoveryDef = createAlertDefinition(def2);
        validateDefinition(recoveryDef);

        // validate recovery alert definition
        validateRecoveryAlertDefinition(recoveryDef, problemDef);

        // cleanup
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(recoveryDef);
        definitions.add(problemDef);
        cleanup(definitions);
    }

    public void testSyncRecoveryWithoutProblemDefTypeAlert() throws Exception {

        Resource platform = getLocalPlatformResource(false, false);

        // Find availability metric for the passed in resource
        Metric m = findAvailabilityMetric(platform);

        // First sync the problem definition
        AlertDefinition def = generateTestDefinition("Test Problem Alert");
        def.setResourcePrototype(platform.getResourcePrototype());
        final double THRESHOLD = 0;
        def.getAlertCondition().add(
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.EQUALS,
                                                                THRESHOLD));
        AlertDefinition parentDown = createAlertDefinition(def);        
        validateTypeDefinition(parentDown);

        // Next, sync the recovery
        AlertDefinition recoveryDef = generateTestDefinition("Test Recovery Alert");
        recoveryDef.setResourcePrototype(platform.getResourcePrototype());
        AlertCondition threshold =
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.NOT_EQUALS,
                                                                THRESHOLD);
        AlertCondition recovery =
                AlertDefinitionBuilder.createRecoveryCondition(true, def);
        recoveryDef.getAlertCondition().add(threshold);
        recoveryDef.getAlertCondition().add(recovery);

        AlertDefinition parentRecovery = createAlertDefinition(recoveryDef);
        validateTypeDefinition(parentRecovery);

        // validate recovery alert definition
        validateRecoveryAlertDefinition(parentRecovery, parentDown);
        
        // cleanup
        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(parentRecovery);
        definitions.add(parentDown);
        cleanup(definitions);
    }

    public void testInvalidRecoveryAlert() throws Exception {

        HQApi api = getApi();
        AlertDefinitionApi defApi = api.getAlertDefinitionApi();
        Resource platform = getLocalPlatformResource(false, false);

        // Find availability metric for the passed in resource
        Metric m = findAvailabilityMetric(platform);

        AlertDefinition problemDef = generateTestDefinition();
        AlertDefinition recoveryDef = generateTestDefinition();

        recoveryDef.setResource(platform);

        AlertCondition thresholdCond =
                AlertDefinitionBuilder.createThresholdCondition(true, m.getName(),
                                                                AlertComparator.EQUALS, 1);
        AlertCondition recoveryCond =
                AlertDefinitionBuilder.createRecoveryCondition(true, problemDef);
        recoveryDef.getAlertCondition().add(thresholdCond);
        recoveryDef.getAlertCondition().add(recoveryCond);


        List<AlertDefinition> definitions = new ArrayList<AlertDefinition>();
        definitions.add(recoveryDef);
        AlertDefinitionsResponse response = defApi.syncAlertDefinitions(definitions);
        hqAssertFailureObjectNotFound(response);
    }
    
    // TODO: Missing attributes
    // TODO: Invalid recovery alert (wrong resource type)
}
