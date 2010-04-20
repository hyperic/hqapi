package org.hyperic.hq.hqapi1.test;

public class HierarchicalAlertingOn_test extends HierarchicalAlertingTestBase {

    public HierarchicalAlertingOn_test(String name) {
        super(name);
    }

    public void testPlatformDownServersDown() throws Exception {
        simulatePlatformDownServersDown(true);
    }
}