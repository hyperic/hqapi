/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2011], VMWare, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
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
 * 
 */

package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

@Component
public class GroovyShellCommand extends AbstractCommand {

    private static String CMD_RUN = "run";

    private static String[] COMMANDS = { CMD_RUN };

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }
    
    public String getName() {
        return "groovyshell";
     }

    public int handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return 1;
        }

        if (args[0].equals(CMD_RUN)) {
            run(trim(args));
        } else {
            printUsage();
            return 1;
        }
        return 0;
    }

    private void run(String[] args) throws Exception {
        OptionParser p = getOptionParser();
        OptionSet options = getOptions(p, args);
        InputStream is = null;
        
        try {
            is = getInputStream(options);
        } catch (IOException e) {
            printUsage();
            System.exit(-1);
        }
        
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class groovyClass = loader.parseClass(is);

        // let's call some method on an instance
        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
        groovyObject.setProperty("api", getApi(options)); // pass the api to the script
        groovyObject.invokeMethod("run", args);    
    }

    public HQApi getApi() {
        return hqApi;
    }
    
}
