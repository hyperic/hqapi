package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MaintenanceApi;
import org.hyperic.hq.hqapi1.types.MaintenanceResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class MaintenanceUnschedule_test extends MaintenanceTestBase {

    private static final long HOUR = 60 * 60 * 1000;
        
    public MaintenanceUnschedule_test(String name) {
        super(name);
    }

    public void testUnscheduleInvalidGroup() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        long start = System.currentTimeMillis() + HOUR;
        long end = start + HOUR;
        MaintenanceResponse response = mApi.schedule(Integer.MAX_VALUE,
                                                     start, end);
        hqAssertFailureObjectNotFound(response);
    }

    public void testUnschedule() throws Exception {

        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();
        long start = System.currentTimeMillis() + HOUR;
        long end = start + HOUR;
        MaintenanceResponse response = mApi.schedule(g.getId(),
                                                     start, end);
        hqAssertSuccess(response);

        StatusResponse unscheduleResponse = mApi.unschedule(g.getId());
        hqAssertSuccess(unscheduleResponse);

        cleanupGroup(g);
    }

    public void testUnscheduleNotInMaintenance() throws Exception {
 
        MaintenanceApi mApi = getApi().getMaintenanceApi();

        Group g = getFileServerMountCompatibleGroup();

        StatusResponse unscheduleResponse = mApi.unschedule(g.getId());
        hqAssertSuccess(unscheduleResponse);

        cleanupGroup(g);
    }
}
