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
import org.hyperic.hq.hqapi1.types.AlertDefinitionsResponse;
import org.hyperic.hq.hqapi1.types.AlertDefinition;

public class AlertDefinitionGetTypeBased_test extends AlertDefinitionTestBase {

    public AlertDefinitionGetTypeBased_test(String name) {
        super(name);
    }

    public void testGetTypeDefinitionsWithIds() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        AlertDefinitionsResponse response = api.getTypeAlertDefinitions(false);
        hqAssertSuccess(response);

        for (AlertDefinition d : response.getAlertDefinition()) {
            validateTypeDefinition(d);
        }
    }

    public void testGetTypeDefinitionsWithoutIds() throws Exception {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();

        AlertDefinitionsResponse response = api.getTypeAlertDefinitions(true);
        hqAssertSuccess(response);

        for (AlertDefinition d : response.getAlertDefinition()) {
            assertNotNull("Alert definition name is null",
                          d.getName());
            assertTrue("Invalid frequency " + d.getFrequency(),
                       d.getFrequency() >= 0 && d.getFrequency() <= 4);
            assertTrue("Invalid priority " + d.getPriority(),
                       d.getPriority() >= 1 & d.getPriority() <= 3);
            // Type alerts have parent == 0
            assertTrue("Invalid parent id " + d.getParent() +
                       " for type definition " + d.getName(),
                       d.getParent() == 0);
            assertTrue("No ResourcePrototype found for type based alert",
                       d.getResourcePrototype() != null);
            // Should not have ids
            assertNull("Alert id is not null", d.getId());
        }
    }
}
