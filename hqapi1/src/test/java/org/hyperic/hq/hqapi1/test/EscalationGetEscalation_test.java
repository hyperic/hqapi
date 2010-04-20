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
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.EscalationResponse;

public class EscalationGetEscalation_test extends EscalationTestBase {

    public EscalationGetEscalation_test(String name) {
        super(name);
    }

    public void testGetEscalationById() throws Exception {
        
        EscalationApi escApi = getEscalationApi();

        Escalation e = generateEscalation();
        e.getAction().add(EscalationActionBuilder.createNoOpAction(1000));

        EscalationResponse response = escApi.createEscalation(e);
        hqAssertSuccess(response);

        // Look up escalation by id
        response  = escApi.getEscalation(response.getEscalation().getId());
        hqAssertSuccess(response);

        // Cleanup
        cleanup(response.getEscalation());
    }

    public void testGetEscalationByName() throws Exception {

        EscalationApi escApi = getEscalationApi();

        Escalation e = generateEscalation();
        e.getAction().add(EscalationActionBuilder.createNoOpAction(1000));

        EscalationResponse response = escApi.createEscalation(e);
        hqAssertSuccess(response);

        // Look up escalation by id
        response  = escApi.getEscalation(response.getEscalation().getName());
        hqAssertSuccess(response);

        // Cleanup
        cleanup(response.getEscalation());
    }
    
}
