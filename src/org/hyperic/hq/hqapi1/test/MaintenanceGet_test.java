package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MaintenanceApi;
import org.hyperic.hq.hqapi1.types.MaintenanceResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.MaintenanceEvent;

public class MaintenanceGet_test extends MaintenanceTestBase {

    private static final long HOUR = 60 * 60 * 1000;

    public MaintenanceGet_test(String name) {
        super(name);
    }

    public void testGetInvalidGroup() throws Exception {
        MaintenanceApi mApi = getApi().getMaintenanceApi();
        MaintenanceResponse response = mApi.get(Integer.MAX_VALUE);
        hqAssertFailureObjectNotFound(response);
    }

    public void testGetNotInMaintenance() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();
        MaintenanceResponse response = mApi.get(g.getId());
        hqAssertSuccess(response);

        // TODO: Shouldn't this return null?
        // assertNull("Maintenance event found for group not in maintenance",
        //            response.getMaintenanceEvent());

        cleanupGroup(g);
    }

    public void testGet() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();
        long start = System.currentTimeMillis() + HOUR;
        long end = start + HOUR;
        MaintenanceResponse scheduleResponse = mApi.schedule(g.getId(),
                                                             start, end);
        hqAssertSuccess(scheduleResponse);

        MaintenanceResponse getResponse = mApi.get(g.getId());
        hqAssertSuccess(getResponse);

        MaintenanceEvent e = getResponse.getMaintenanceEvent();
        assertNotNull("MaintenanceEvent not found for valid group " + g.getName());
        valididateMaintenanceEvent(e, g, start, end);

        StatusResponse unscheduleResponse = mApi.unschedule(g.getId());
        hqAssertSuccess(unscheduleResponse);

        cleanupGroup(g);
    }
}
