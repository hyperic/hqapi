package org.hyperic.hq.hqapi1.tools;

public class MaintenanceCommand extends Command {

    private static final String CMD_SCHEDULE   = "schedule";
    private static final String CMD_UNSCHEDULE = "unschedule";
    private static final String CMD_GET        = "get";

    private static final String OPT_GROUPID    = "groupId";
    private static final String OPT_START      = "start";
    private static final String OPT_END        = "end";

    protected void handleCommand(String[] args) throws Exception {
    }
}
