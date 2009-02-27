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

import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.EscalationActionBuilder;
import org.hyperic.hq.hqapi1.EscalationActionBuilder.EscalationActionType;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.EscalationResponse;
import org.hyperic.hq.hqapi1.types.Notify;

import java.util.ArrayList;

public class EscalationUpdateEscalation_test extends EscalationTestBase {

    public EscalationUpdateEscalation_test(String name) {
        super(name);
    }

    public void testUpdateEscalation() throws Exception {

        EscalationApi api = getEscalationApi();

        Escalation e = generateEscalation();
        e.getAction().add(EscalationActionBuilder.createNoOpAction(1000));
        EscalationResponse createResponse = api.createEscalation(e);
        hqAssertSuccess(createResponse);

        e = createResponse.getEscalation();

        final Integer ID = e.getId();
        final String U_NAME = e.getName() + "Updated";
        final String U_DESC = e.getDescription() + "Updated";
        final long   U_PAUSE = e.getMaxPauseTime() * 2;
        final boolean U_NOTIFY = !e.isNotifyAll();
        final boolean U_PAUSE_ALLOWED = !e.isNotifyAll();
        final boolean U_REPEAT = !e.isRepeat();

        // Update everything
        e.setName(U_NAME);
        e.setDescription(U_DESC);
        e.setMaxPauseTime(U_PAUSE);
        e.setNotifyAll(U_NOTIFY);
        e.setPauseAllowed(U_PAUSE_ALLOWED);
        e.setRepeat(U_REPEAT);
        e.getAction().clear();
        e.getAction().add(EscalationActionBuilder.createEmailAction(1000, false, new ArrayList<String>()));

        EscalationResponse updateResponse = api.updateEscalation(e);
        hqAssertSuccess(updateResponse);

        Escalation updated = updateResponse.getEscalation();
        assertEquals(ID, updated.getId());
        assertEquals(updated.getName(), U_NAME);
        assertEquals(updated.getDescription(), U_DESC);
        assertEquals(updated.getMaxPauseTime(), U_PAUSE);
        assertEquals(updated.isNotifyAll(), U_NOTIFY);
        assertEquals(updated.isPauseAllowed(), U_PAUSE_ALLOWED);
        assertEquals(updated.isRepeat(), U_REPEAT);
        assertEquals(updated.getAction().size(), 1);
        assertEquals(updated.getAction().get(0).getActionType(),
                     EscalationActionType.EMAIL.getType());

        cleanup(updated);  
    }

    public void testUpdateNonExistantEscalation() throws Exception {

        EscalationApi api = getEscalationApi();

        Escalation e = generateEscalation();
        e.getAction().add(EscalationActionBuilder.createNoOpAction(1000));
        EscalationResponse createResponse = api.updateEscalation(e);
        hqAssertFailureObjectNotFound(createResponse);       
    }

    public void testUpdateEmptyEscalation() throws Exception {

        EscalationApi api = getEscalationApi();

        Escalation e = generateEscalation();
        EscalationResponse createResponse = api.updateEscalation(e);
        hqAssertFailureObjectNotFound(createResponse);
    }
}
