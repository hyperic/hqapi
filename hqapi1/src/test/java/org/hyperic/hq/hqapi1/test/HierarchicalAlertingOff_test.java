package org.hyperic.hq.hqapi1.test;

public class HierarchicalAlertingOff_test extends HierarchicalAlertingTestBase {

    public HierarchicalAlertingOff_test(String name) {
        super(name);
    }

    public void testPlatformDownServersDown() throws Exception {
        simulatePlatformDownServersDown(false);
    }
}