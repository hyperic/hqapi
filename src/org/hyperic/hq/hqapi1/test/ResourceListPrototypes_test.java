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
import org.hyperic.hq.hqapi1.types.ResourcePrototypesResponse;

public class ResourceListPrototypes_test extends HQApiTestBase {

    public ResourceListPrototypes_test(String name) {
        super(name);
    }

    public void testListAllPrototypes() throws Exception {
        ResourceApi api = getApi().getResourceApi();
        
        ResourcePrototypesResponse resp = api.getAllResourcePrototypes();
        
        hqAssertSuccess(resp);
        assertTrue(resp.getResourcePrototype().size() != 0);

        for (ResourcePrototype pt : resp.getResourcePrototype()) {
            assertTrue(pt.getId() > 0);
            assertTrue(pt.getName() != null && pt.getName().length() > 0);
        }
    }

    public void testListPrototypes() throws Exception {
        ResourceApi api = getApi().getResourceApi();

        ResourcePrototypesResponse allResponse = api.getAllResourcePrototypes();
        hqAssertSuccess(allResponse);

        ResourcePrototypesResponse response = api.getResourcePrototypes();
        hqAssertSuccess(response);

        assertTrue("All prototypes not greater than existing prototypes " +
                   "all=" + allResponse.getResourcePrototype().size() +
                   " existing=" + response.getResourcePrototype().size(),
                   allResponse.getResourcePrototype().size() >
                   response.getResourcePrototype().size());

        assertTrue("Existing prototypes not > 1",
                   response.getResourcePrototype().size() > 1);
    }
}
