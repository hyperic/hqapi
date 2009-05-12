package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MaintenanceApi;
import org.hyperic.hq.hqapi1.types.MaintenanceResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class MaintenanceSchedule_test extends MaintenanceTestBase {

    private static final long HOUR = 60 * 60 * 1000;

    public MaintenanceSchedule_test(String name) {
        super(name);
    }

    public void testScheduleInvalidGroup() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        long start = System.currentTimeMillis() + HOUR;
        long end = start + HOUR;
        MaintenanceResponse response = mApi.schedule(Integer.MAX_VALUE,
                                                     start, end);
        hqAssertFailureObjectNotFound(response);
    }

    public void testScheduleInvalidWindow() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();
        long end = System.currentTimeMillis() + HOUR;
        long start = end + HOUR;
        MaintenanceResponse response = mApi.schedule(g.getId(),
                                                     start, end);
        hqAssertFailureInvalidParameters(response);
        cleanupGroup(g);
    }

    public void testScheduleInPast() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();
        long start = 0;
        long end = 100;
        MaintenanceResponse response = mApi.schedule(g.getId(),
                                                     start, end);
        hqAssertFailureInvalidParameters(response);
        cleanupGroup(g);         
    }

    public void testSchedule() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();
        long start = System.currentTimeMillis() + HOUR;
        long end = start + HOUR;
        MaintenanceResponse response = mApi.schedule(g.getId(),
                                                     start, end);
        hqAssertSuccess(response);

        assertNotNull(response.getMaintenanceEvent());
        valididateMaintenanceEvent(response.getMaintenanceEvent(), g, start, end);

        StatusResponse unscheduleResponse = mApi.unschedule(g.getId());
        hqAssertSuccess(unscheduleResponse);

        cleanupGroup(g);         
    }
}
