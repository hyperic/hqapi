package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLMaintenance_test extends WADLTestBase {

    public void testGet() throws Exception {
        Endpoint.MaintenanceGetHqu maintGet = new Endpoint.MaintenanceGetHqu();

        MaintenanceResponse response = maintGet.getAsMaintenanceResponse(Integer.MAX_VALUE);
        hqAssertFailure(response);
    }

    public void testSchedule() throws Exception {
        Endpoint.MaintenanceScheduleHqu maintSchedule =
                new Endpoint.MaintenanceScheduleHqu();

        MaintenanceResponse response =
                maintSchedule.getAsMaintenanceResponse(Integer.MAX_VALUE,
                                                       System.currentTimeMillis(),
                                                       System.currentTimeMillis());
        hqAssertFailure(response);
    }

    public void testUnschedule() throws Exception {
        Endpoint.MaintenanceUnscheduleHqu maintUnschedule =
                new Endpoint.MaintenanceUnscheduleHqu();

        StatusResponse response =
                maintUnschedule.getAsStatusResponse(Integer.MAX_VALUE);
        hqAssertFailure(response);
    }
}
