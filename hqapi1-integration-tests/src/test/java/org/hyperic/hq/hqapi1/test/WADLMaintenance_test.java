package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLMaintenance_test extends WADLTestBase {

    public void testGet() throws Exception {
        HttpLocalhost8080HquHqapi1.MaintenanceGetHqu maintGet = new HttpLocalhost8080HquHqapi1.MaintenanceGetHqu();

        MaintenanceResponse response = maintGet.getAsMaintenanceResponse(Integer.MAX_VALUE);
        hqAssertFailure(response);
    }

    public void testSchedule() throws Exception {
        HttpLocalhost8080HquHqapi1.MaintenanceScheduleHqu maintSchedule =
                new HttpLocalhost8080HquHqapi1.MaintenanceScheduleHqu();

        MaintenanceResponse response =
                maintSchedule.getAsMaintenanceResponse(Integer.MAX_VALUE,
                                                       System.currentTimeMillis(),
                                                       System.currentTimeMillis());
        hqAssertFailure(response);
    }

    public void testUnschedule() throws Exception {
        HttpLocalhost8080HquHqapi1.MaintenanceUnscheduleHqu maintUnschedule =
                new HttpLocalhost8080HquHqapi1.MaintenanceUnscheduleHqu();

        StatusResponse response =
                maintUnschedule.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(response);
    }
}
