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

import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;

public class ResourceGetPrototype_test extends HQApiTestBase {

    public ResourceGetPrototype_test(String name) {
        super(name);
    }

    public void testGetValidResourcePrototype() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        final String TYPE = "Linux";
        ResourcePrototypeResponse response = api.getResourcePrototype(TYPE);
        hqAssertSuccess(response);

        ResourcePrototype type = response.getResourcePrototype();
        assertNotNull("Requested prototype " + TYPE + " was null", type);
        assertTrue("Requested prototype id " + type.getId() + " invalid",
                   type.getId() > 10000);
        assertEquals(TYPE, type.getName());
    }

    public void testGetInvalidResourcePrototype() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        final String TYPE = "Some unknown type";
        ResourcePrototypeResponse response = api.getResourcePrototype(TYPE);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetNullPrototype() throws Exception {

        ResourceApi api = getApi().getResourceApi();

        ResourcePrototypeResponse response = api.getResourcePrototype(null);
        hqAssertFailureInvalidParameters(response);
    }
}
