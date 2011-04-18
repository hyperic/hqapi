/*
 * NOTE: This copyright does *not* cover user programs that use Hyperic
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 *
 * Copyright (C) [2004-2011], VMware, Inc.
 * This file is part of Hyperic.
 *
 * Hyperic is free software; you can redistribute it and/or modify
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
 */

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
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.MaintenanceEvent;
import org.hyperic.hq.hqapi1.types.MaintenanceResponse;
import org.hyperic.hq.hqapi1.types.MaintenancesResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.springframework.stereotype.Component;
@Component
public class MaintenanceCommand extends AbstractCommand {

    private static final String CMD_SCHEDULE   = "schedule";
    private static final String CMD_UNSCHEDULE = "unschedule";
    private static final String CMD_GET        = "get";

    private static final String[] COMMANDS = { CMD_SCHEDULE, CMD_UNSCHEDULE,
                                               CMD_GET };

    private static final String OPT_RESOURCEID    = "resourceId";
    private static final String OPT_GROUPID    = "groupId";
    private static final String OPT_ALL		   = "all";
    private static final String OPT_START      = "start";
    private static final String OPT_END        = "end";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }
    
    public String getName() {
        return "maintenance";
     }

    public int handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return 1;
        }

        if (args[0].equals(CMD_SCHEDULE)) {
            schedule(trim(args));
        } else if (args[0].equals(CMD_UNSCHEDULE)) {
            unschedule(trim(args));
        } else if (args[0].equals(CMD_GET)) {
            get(trim(args));
        } else {
            printUsage();
            return 1;
        }
        return 0;
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
        final DateFormat df = SimpleDateFormat.getInstance();
        try {
            return df.parse(str);
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
        String[] ONE_REQUIRED = { OPT_RESOURCEID, OPT_GROUPID };

    	OptionParser p = getOptionParser();

        p.accepts(OPT_RESOURCEID, "The id of the resource to schedule for maintenance").
        		withRequiredArg().ofType(Integer.class);

        p.accepts(OPT_GROUPID, "The id of the group to schedule for maintenance").
                withRequiredArg().ofType(Integer.class);

        p.accepts(OPT_START, "Start time for maintenance.").withRequiredArg();
        p.accepts(OPT_END, "End time for maintenance.").withRequiredArg();

        OptionSet options = getOptions(p, args);

        int criteria = 0;
        for (String opt : ONE_REQUIRED) {
            if (options.has(opt)) {
                criteria++;
            }
        }

        if (criteria == 0) {
            System.err.println("One of " + Arrays.toString(ONE_REQUIRED) + " is required.");
            System.exit(-1);
        } else if (criteria > 1) {
            System.err.println("Only one of " + Arrays.toString(ONE_REQUIRED) + " may be specified");
            System.exit(-1);
        }
        
        HQApi api = getApi(options);
        MaintenanceApi maintenanceApi = api.getMaintenanceApi();
        MaintenanceResponse response = null;
        
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

        if (options.has(OPT_GROUPID)) {
        	Integer groupId = (Integer)getRequired(options, OPT_GROUPID);
            response = maintenanceApi.schedule(groupId, 
            								   startDate.getTime(),
            								   endDate.getTime());
        } else {
    		Integer resourceId = (Integer)getRequired(options, OPT_RESOURCEID);
    		ResourceResponse resourceResponse = api.getResourceApi().getResource(resourceId, false, false);
	        checkSuccess(resourceResponse);
    		response = maintenanceApi.schedule(resourceResponse.getResource(),
    										   startDate.getTime(), 
    										   endDate.getTime());        	
        }

        checkSuccess(response);
        MaintenanceEvent event = response.getMaintenanceEvent();

        if (event.getGroupId() > 0) {
        	System.out.println("Maintenance scheduled for group " + event.getGroupId());
        } else {
        	System.out.println("Maintenance scheduled for resource " + event.getResourceId());        	
        }
    }

    private void unschedule(String[] args) throws Exception {
        String[] ONE_REQUIRED = { OPT_RESOURCEID, OPT_GROUPID };

        OptionParser p = getOptionParser();

        p.accepts(OPT_RESOURCEID, "The id of the resource to unschedule from maintenance").
				withRequiredArg().ofType(Integer.class);

        p.accepts(OPT_GROUPID, "The id of the group to unschedule from maintenance").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        int criteria = 0;
        for (String opt : ONE_REQUIRED) {
            if (options.has(opt)) {
                criteria++;
            }
        }

        if (criteria == 0) {
            System.err.println("One of " + Arrays.toString(ONE_REQUIRED) + " is required.");
            System.exit(-1);
        } else if (criteria > 1) {
            System.err.println("Only one of " + Arrays.toString(ONE_REQUIRED) + " may be specified");
            System.exit(-1);
        }
        
        HQApi api = getApi(options);
        MaintenanceApi maintenanceApi = api.getMaintenanceApi();
        StatusResponse response = null;
        Integer id = null;
        
        if (options.has(OPT_GROUPID)) {
        	id = (Integer)getRequired(options, OPT_GROUPID);
            response = maintenanceApi.unschedule(id);
        } else if (options.has(OPT_RESOURCEID)) {
    		id = (Integer)getRequired(options, OPT_RESOURCEID);
    		ResourceResponse resourceResponse = api.getResourceApi().getResource(id, false, false);
	        checkSuccess(resourceResponse);        	
            response = maintenanceApi.unschedule(resourceResponse.getResource());
        }

        checkSuccess(response);

        if (options.has(OPT_GROUPID)) {
        	System.out.println("Maintenance for group " + id + " unscheduled.");
        } else {
        	System.out.println("Maintenance for resource " + id + " unscheduled.");        	
        }
    }

    private void get(String[] args) throws Exception {
        String[] ONE_REQUIRED = { OPT_RESOURCEID, OPT_GROUPID, OPT_ALL };

    	final DateFormat df = SimpleDateFormat.getInstance();
        OptionParser p = getOptionParser();

        p.accepts(OPT_RESOURCEID, "The id of the resource to query for maintenance").
        		withRequiredArg().ofType(Integer.class);

        p.accepts(OPT_GROUPID, "The id of the group to query for maintenance").
                withRequiredArg().ofType(Integer.class);

        p.accepts(OPT_ALL, "Get all maintenance schedules");

        OptionSet options = getOptions(p, args);

        int criteria = 0;
        for (String opt : ONE_REQUIRED) {
            if (options.has(opt)) {
                criteria++;
            }
        }
        
        if (criteria == 0) {
            System.err.println("One of " + Arrays.toString(ONE_REQUIRED) + " is required.");
            System.exit(-1);
        } else if (criteria > 1) {
            System.err.println("Only one of " + Arrays.toString(ONE_REQUIRED) + " may be specified");
            System.exit(-1);
        }
        
        HQApi api = getApi(options);
        MaintenanceApi maintenanceApi = api.getMaintenanceApi();

        if (options.has(OPT_ALL)) {
        	MaintenancesResponse schedules = maintenanceApi.getAll(null);
	        checkSuccess(schedules);
            XmlUtil.serialize(schedules, System.out, Boolean.TRUE);
        } else {
        	MaintenanceResponse response = null;
        	Integer id = null;
        	
        	if (options.has(OPT_GROUPID)) {
        		id = (Integer)getRequired(options, OPT_GROUPID);
        		response = maintenanceApi.get(id);
        	} else if (options.has(OPT_RESOURCEID)) {
        		id = (Integer)getRequired(options, OPT_RESOURCEID);
        		ResourceResponse resourceResponse = api.getResourceApi().getResource(id, false, false);
    	        checkSuccess(resourceResponse);
        		response = maintenanceApi.get(resourceResponse.getResource());
        	}
	        checkSuccess(response);	        
	        MaintenanceEvent event = response.getMaintenanceEvent();
	
	        if (event != null &&
	        	event.getState() != null &&
	        	event.getStartTime() != 0 &&
	        	event.getEndTime() != 0) {
	        	
	        	if (event.getGroupId() > 0) {
	        		System.out.println("Maintenance schedule for group " + event.getGroupId());
	        	} else {
	        		System.out.println("Maintenance schedule for resource " + event.getResourceId());	        		
	        	}
	            System.out.println("State: " + event.getState().value());
	            System.out.println("Start Time: " + df.format(event.getStartTime()));
	            System.out.println("End Time: " + df.format(event.getEndTime()));
	        } else {
	        	if (options.has(OPT_GROUPID)) {
	        		System.out.println("No maintenance events found for group " + id);
	        	} else if (options.has(OPT_RESOURCEID)) {
	        		System.out.println("No maintenance events found for resource " + id);	        		
	        	}
	        }
        }
    }
}
