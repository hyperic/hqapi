/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
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

package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.EscalationAction;
import org.hyperic.hq.hqapi1.types.Notify;
import org.hyperic.hq.hqapi1.types.Role;
import org.hyperic.hq.hqapi1.types.User;

import java.util.List;

/**
 * Utility class to build {@link org.hyperic.hq.hqapi1.types.EscalationAction}s.
 */
public class EscalationActionBuilder {

    public enum EscalationActionType {

        EMAIL("EmailAction"),
        NOOP("NoOpAction"),
        SYSLOG("SyslogAction");
        
        private final String _type;

        EscalationActionType(String type) {
            _type = type;
        }

        public String getType() {
            return _type;
        }
    }

    public enum EscalationNotifyType {

        EMAILS("email"),
        USERS("users"),
        ROLES("roles");

        private final String _type;

        EscalationNotifyType(String type) {
            _type = type;
        }

        public String getType() {
            return _type;
        }
    }

    /**
     * Create an EmailAction to notify Roles
     *
     * @param wait The wait time in ms before escalating.
     * @param sms Flag to indicate whether email or sms notification should be used.
     * @param roles The list of Roles to notify.
     *
     * @return An email {@link org.hyperic.hq.hqapi1.types.EscalationAction}
     */
    public static EscalationAction createEmailRolesAction(long wait, boolean sms,
                                                          List<Role> roles) {
        EscalationAction a = new EscalationAction();
        a.setActionType(EscalationActionType.EMAIL.getType());
        a.setNotifyType(EscalationNotifyType.ROLES.getType());
        a.setWait(wait);
        a.setSms(sms);
        for (Role r : roles) {
            Notify n = new Notify();
            n.setName(r.getName());
            a.getNotify().add(n);
        }

        return a;
    }

    /**
     * Create an EmailAction to notify Users
     *
     * @param wait The wait time in ms before escalating.
     * @param sms Flag to indicate whether email or sms notification should be used.
     * @param users The list of Users to notify.
     *
     * @return An email {@link org.hyperic.hq.hqapi1.types.EscalationAction}
     */
    public static EscalationAction createEmailUsersAction(long wait, boolean sms,
                                                          List<User> users) {
        EscalationAction a = new EscalationAction();
        a.setActionType(EscalationActionType.EMAIL.getType());
        a.setNotifyType(EscalationNotifyType.USERS.getType());
        a.setWait(wait);
        a.setSms(sms);
        for (User u : users) {
            Notify n = new Notify();
            n.setName(u.getName());
            a.getNotify().add(n);
        }

        return a;
    }

    /**
     * Create an EmailAction to notify emails
     *
     * @param wait The wait time in ms before escalating.
     * @param sms Flag to indicate whether email or sms notification should be used.
     * @param notify The list of names to notify.
     *
     * @return An email {@link org.hyperic.hq.hqapi1.types.EscalationAction}
     */
    public static EscalationAction createEmailAction(long wait, boolean sms,
                                                     List<String> notify) {
        EscalationAction a = new EscalationAction();
        a.setActionType(EscalationActionType.EMAIL.getType());
        a.setNotifyType(EscalationNotifyType.EMAILS.getType());
        a.setWait(wait);
        a.setSms(sms);
        for (String email : notify) {
            Notify n = new Notify();
            n.setName(email);
            a.getNotify().add(n);
        }

        return a;
    }

    /**
     * Create a Syslog action
     *
     * @param wait The wait time in ms before escalating.
     * @param meta The meta flag for this action.
     * @param product The product value for this action.
     * @param version The version value for this action.
     *
     * @return A syslog {@link org.hyperic.hq.hqapi1.types.EscalationAction}
     */
    public static EscalationAction createSyslogAction(long wait, String meta,
                                                      String product, String version) {
        EscalationAction a = new EscalationAction();
        a.setActionType(EscalationActionType.SYSLOG.getType());
        a.setWait(wait);
        a.setSyslogMeta(meta);
        a.setSyslogProduct(product);
        a.setSyslogVersion(version);
        return a;
    }

    /**
     * Create a NoOp action
     *
     * @param wait The wait time in ms before escalating.
     *
     * @return A no-op {EscalationAction}
     */
    public static EscalationAction createNoOpAction(long wait) {
        EscalationAction a = new EscalationAction();
        a.setActionType(EscalationActionType.NOOP.getType());
        a.setWait(wait);
        return a;
    }
}
