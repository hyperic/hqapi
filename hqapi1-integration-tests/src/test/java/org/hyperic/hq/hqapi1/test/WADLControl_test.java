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

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLControl_test extends WADLTestBase {

    public void testActions() throws Exception {
        HttpLocalhost8080HquHqapi1.ControlActionsHqu actions =
                new HttpLocalhost8080HquHqapi1.ControlActionsHqu();

        ControlActionResponse response =
                actions.getAsControlActionResponse(Integer.MAX_VALUE);
        hqAssertFailure(response); // Id won't exist
    }

    public void testHistory() throws Exception {
        HttpLocalhost8080HquHqapi1.ControlHistoryHqu history =
                new HttpLocalhost8080HquHqapi1.ControlHistoryHqu();

        ControlHistoryResponse response =
                history.getAsControlHistoryResponse(Integer.MAX_VALUE);
        hqAssertFailure(response); // Id won't exist
    }

    public void testExecute() throws Exception {
        HttpLocalhost8080HquHqapi1.ControlExecuteHqu execute =
                new HttpLocalhost8080HquHqapi1.ControlExecuteHqu();

        StatusResponse response =
                execute.getAsStatusResponse(Integer.MAX_VALUE, "none",
                                            null);
        hqAssertFailure(response); // Id won't exist
    }
}
