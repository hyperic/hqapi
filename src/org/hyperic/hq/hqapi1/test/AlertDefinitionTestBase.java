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
import org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertPriority;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public abstract class AlertDefinitionTestBase extends HQApiTestBase {

    public AlertDefinitionTestBase(String name) {
        super(name);
    }

    protected AlertDefinition generateTestDefinition() {
        AlertDefinition d = new AlertDefinition();

        Random r = new Random();
        d.setName("Test Alert Definition" + r.nextInt());
        d.setDescription("Test Alert Description");
        d.setPriority(AlertPriority.MEDIUM.getPriority());
        d.setActive(true);
        return d;
    }

    protected void validateDefinition(AlertDefinition d) {
        assertNotNull(d.getName());
        assertTrue("Invalid frequency " + d.getFrequency(),
                   d.getFrequency() >= 0 && d.getFrequency() <= 4);
        assertTrue("Invalid priority " + d.getPriority(),
                   d.getPriority() >= 1 & d.getPriority() <= 3);
    }

    protected void cleanup(List<AlertDefinition> definitions) throws IOException {

        AlertDefinitionApi api = getApi().getAlertDefinitionApi();
        for (AlertDefinition d : definitions) {
            StatusResponse deleteResponse = api.deleteAlertDefinition(d.getId());
            hqAssertSuccess(deleteResponse);
        }
    }
}
