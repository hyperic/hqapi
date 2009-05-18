package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.Arrays;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.MaintenanceApi;
import org.hyperic.hq.hqapi1.types.MaintenanceResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class MaintenanceCommand extends Command {

    private static final String CMD_SCHEDULE   = "schedule";
    private static final String CMD_UNSCHEDULE = "unschedule";
    private static final String CMD_GET        = "get";

    private static final String[] COMMANDS = { CMD_SCHEDULE, CMD_UNSCHEDULE,
                                               CMD_GET };

    private static final String OPT_GROUPID    = "groupId";
    private static final String OPT_START      = "start";
    private static final String OPT_END        = "end";

    private static final DateFormat DF = SimpleDateFormat.getInstance();

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }

    protected void handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            System.exit(-1);
        }

        if (args[0].equals(CMD_SCHEDULE)) {
            schedule(trim(args));
        } else if (args[0].equals(CMD_UNSCHEDULE)) {
            unschedule(trim(args));
        } else if (args[0].equals(CMD_GET)) {
            get(trim(args));
        } else {
            printUsage();
            System.exit(-1);
        }
    }


    /**
     * Function that takes a user given date string and attempts to convert it to
     * a Java Date object.  One of 2 formats are supported:
     *
     * 1) Date/Time format as defined by SimpleDateFormat (i.e. 5/18/2008 4:00 PM)
     * 2) Hour/Minutes notation.  In this case, the current date is used.
     *
     * @param str The date string to parse.
     * @return The equivenlent Date object, or null if the date could not be parsed.
     */
    private Date parseDateString(String str) {
        try {
            return DF.parse(str);
        } catch (ParseException e) {
            // Ignored
        }

        // Fall back to HH:MM notation if date parse fails.
        String time[] = str.split(":");
        if (time.length != 2) {
            return null;
        }

        try {
            int hours = Integer.parseInt(time[0]);
            int minutes = Integer.parseInt(time[1]);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hours);
            c.set(Calendar.MINUTE, minutes);
            return c.getTime();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void schedule(String[] args) throws Exception {
        OptionParser p = getOptionParser();

        p.accepts(OPT_GROUPID, "The id of the group to schedule for maintenance").
                withRequiredArg().ofType(Integer.class);

        p.accepts(OPT_START, "Start time for maintenance.").withRequiredArg();
        p.accepts(OPT_END, "End time for maintenance.").withRequiredArg();

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        MaintenanceApi maintenanceApi = api.getMaintenanceApi();

        Integer groupId = (Integer)getRequired(options, OPT_GROUPID);
        String startStr = (String)getRequired(options, OPT_START);
        String endStr = (String)getRequired(options, OPT_END);

        Date startDate = parseDateString(startStr);
        if (startDate == null) {
            System.err.println("Unable to parse date/time string: '" + startStr + "'");
            System.exit(-1);
        }

        Date endDate = parseDateString(endStr);
        if (endDate == null) {
            System.err.println("Unable to parse date/time string: '" + endStr + "'");
            System.exit(-1);
        }

        MaintenanceResponse response = maintenanceApi.schedule(groupId,
                                                               startDate.getTime(),
                                                               endDate.getTime());
        checkSuccess(response);

        System.out.println("Maintenance scheduled for group " + groupId);
    }

    private void unschedule(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_GROUPID, "The id of the group to unschedule from maintenance").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        MaintenanceApi maintenanceApi = api.getMaintenanceApi();

        Integer groupId = (Integer)getRequired(options, OPT_GROUPID);

        StatusResponse response = maintenanceApi.unschedule(groupId);
        checkSuccess(response);

        System.out.println("Maintenance for group " + groupId + " unscheduled.");
    }

    private void get(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_GROUPID, "The id of the group to query for maintenance").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        MaintenanceApi maintenanceApi = api.getMaintenanceApi();

        Integer groupId = (Integer)getRequired(options, OPT_GROUPID);

        MaintenanceResponse response = maintenanceApi.get(groupId);
        checkSuccess(response);

        if (response.getMaintenanceEvent().getState() != null &&
            response.getMaintenanceEvent().getStartTime() != 0 &&
            response.getMaintenanceEvent().getEndTime() != 0) {
            System.out.println("Maintenance scheudle for group " + groupId);
            System.out.println("State: " + response.getMaintenanceEvent().getState().value());
            System.out.println("Start Time: " + DF.format(response.getMaintenanceEvent().getStartTime()));
            System.out.println("End Time: " + DF.format(response.getMaintenanceEvent().getEndTime()));
        } else {
            System.out.println("No maintenance events found for group " + groupId);
        }
    }
}
