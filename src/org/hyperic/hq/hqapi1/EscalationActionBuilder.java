package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.EscalationAction;
import org.hyperic.hq.hqapi1.types.Notify;

import java.util.List;

/**
 * Utility class to build @{link org.hyperic.hq.hqapi1.types.EscalationAction}s.
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

    /**
     * Create an EmailAction
     *
     * @param wait The wait time in ms before escalating.
     * @param sms Flag to indicate whether email or sms notification should be used.
     * @param notify The list of names to notify.
     *
     * TODO: Make notify list a list of Roles or Users?
     * @return An email {@link org.hyperic.hq.hqapi1.types.EscalationAction}
     */
    public static EscalationAction createEmailAction(long wait, boolean sms,
                                                     List<Notify> notify) {
        EscalationAction a = new EscalationAction();
        a.setActionType(EscalationActionType.EMAIL.getType());
        a.setWait(wait);
        a.setSms(sms);
        a.getNotify().addAll(notify);
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
        a.setWait(wait);
        return a;
    }
}
