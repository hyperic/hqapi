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

import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.hyperic.hq.hqapi1.types.AlertDefinition;
import org.hyperic.hq.hqapi1.types.AlertAction;
import org.hyperic.hq.hqapi1.types.AlertActionConfig;
import org.hyperic.hq.hqapi1.types.Resource;

/**
 * This class is used to create {@link org.hyperic.hq.hqapi1.types.AlertCondition}s.
 *
 * An AlertCondition is a check that done within the processing of an
 * {@link org.hyperic.hq.hqapi1.types.AlertDefinition}.  Each AlertDefinition must
 * have 1 or more AlertConditions associated with it.  When more than one AlertCondition is
 * specified, the required flag will indicate if all or one of the conditions are
 * required for the AlertDefinition to fire.
 *
 * @see org.hyperic.hq.hqapi1.types.AlertDefinition#getAlertCondition()
 *
 */
public class AlertDefinitionBuilder {

    private static AlertCondition createBaseCondition(boolean required) {
        AlertCondition c = new AlertCondition();
        c.setRequired(required);
        return c;
    }

    /**
     * The {@link org.hyperic.hq.hqapi1.types.AlertCondition} type.  These
     * should not be used directly.  Instead use the createCondition APIs
     * provided in this class.
     *
     * @see org.hyperic.hq.hqapi1.types.AlertCondition#getType()
     */
    public enum AlertConditionType {

        THRESHOLD(1),
        BASELINE(2),
        CONTROL(3),
        METRIC_CHANGE(4),
        RECOVERY(5),
        CUSTOM_PROP(6),
        LOG(7),
        CONFIG_CHANGE(8);

        private final int _type;

        AlertConditionType(int type) {
            _type = type;
        }

        public int getType() {
            return _type;
        }
    }

    /**
     * The {@link org.hyperic.hq.hqapi1.types.AlertDefinition} priority.
     *
     * @see org.hyperic.hq.hqapi1.types.AlertDefinition#getPriority()
     */
    public enum AlertPriority {

        HIGH(3),
        MEDIUM(2),
        LOW(1);

        private final int _priority;

        AlertPriority(int priority) {
            _priority = priority;
        }

        public int getPriority() {
            return _priority;
        }
    }

    /**
     * Comparator used with {@link org.hyperic.hq.hqapi1.types.AlertCondition}s
     * that use thresholds.  This should be used in conjunction with the static
     * createCondition APIs in this class.
     */
    public enum AlertComparator {

        EQUALS("="),
        NOT_EQUALS("!="),
        LESS_THAN("<"),
        GREATER_THAN(">");

        private final String _comparator;

        AlertComparator(String comparator) {
            _comparator = comparator;
        }

        public String getComparator() {
            return _comparator;
        }
    }

    /**
     * Create a threshold alert condition.  (i.e. When x > y)
     *
     * @param required Indicates if this condition is required or optional for
     * this alert to fire.
     * @param metric The metric name to evaluate.
     * @param comparator The comparison to perform.
     * @param threshold The threshold value that will be compared.
     *
     * @return A threshold AlertCondition.
     */
    public static AlertCondition createThresholdCondition(boolean required,
                                                          String metric,
                                                          AlertComparator comparator,
                                                          double threshold) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.THRESHOLD.getType());
        c.setThresholdMetric(metric);
        c.setThresholdComparator(comparator.getComparator());
        c.setThresholdValue(threshold);

        return c;
    }

    /**
     * Indicates the type of Baseline condition to create.
     * @see org.hyperic.hq.hqapi1.AlertDefinitionBuilder#createBaselineCondition(boolean, String, org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertComparator, double, org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertBaseline)
     */
    public enum AlertBaseline {

        MEAN("mean"),
        MIN("min"),
        MAX("max");

        private final String _baselineType;

        AlertBaseline(String baselineType) {
            _baselineType = baselineType;
        }

        public String getBaselineType() {
            return _baselineType;
        }
    }

    /**
     * Create a baseline AlertCondition.
     *
     * @param required Indicates if this condition is required or optional for
     * this alert to fire.
     * @param metric The metric name to evaluate.
     * @param comparator The comparison to perform.
     * @param percentage The percentage value to use in comparison.
     * @param type The type of baseline to compare against.
     *
     * @return A baseline AlertCondition.
     */
    public static AlertCondition createBaselineCondition(boolean required,
                                                         String metric,
                                                         AlertComparator comparator,
                                                         double percentage,
                                                         AlertBaseline type) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.BASELINE.getType());
        c.setBaselineMetric(metric);
        c.setBaselineComparator(comparator.getComparator());
        c.setBaselinePercentage(percentage);
        c.setBaselineType(type.getBaselineType());

        return c;
    }

    /**
     * Indicates the control status to check.
     * @see org.hyperic.hq.hqapi1.AlertDefinitionBuilder#createControlCondition(boolean, String, org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertControlStatus)
     */
    public enum AlertControlStatus {

        COMPLETED("Completed"),
        FAILED("Failed"),
        IN_PROGRESS("In Progress");

        private final String _controlStatus;

        AlertControlStatus(String controlStatus) {
            _controlStatus = controlStatus;
        }

        public String getControlStatus() {
            return _controlStatus;
        }
    }

    /**
     * Create a control AlertCondition.
     *
     * @param required Indicates if this condition is required or optional for
     * this alert to fire.
     * @param action The control action name to evaluate.
     * @param status The control action status to compare against.
     *
     * @return A control AlertCondition.
     */
    public static AlertCondition createControlCondition(boolean required,
                                                        String action,
                                                        AlertControlStatus status) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.CONTROL.getType());
        c.setControlAction(action);
        c.setControlStatus(status.getControlStatus());

        return c;
    }

    /**
     * Create a metric change AlertCondition.
     *
     * @param required Indicates if this condition is required or optional for
     * this alert to fire.
     * @param metric The metric name to evaluate.
     *
     * @return A metric change AlertCondition.
     */
    public static AlertCondition createChangeCondition(boolean required,
                                                       String metric) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.METRIC_CHANGE.getType());
        c.setMetricChange(metric);
        return c;
    }

    /**
     * Create a recovery AlertCondition.
     *
     * @param required Indicates if this condition is required or optional for
     * this alert to fire.
     * @param recover The {@link org.hyperic.hq.hqapi1.types.AlertDefinition} this
     * condition will recover.
     *
     * @return A recovery AlertCondition.
     */
    public static AlertCondition createRecoveryCondition(boolean required,
                                                         AlertDefinition recover) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.RECOVERY.getType());
        c.setRecover(recover.getName());
        return c;
    }

    /**
     * Create a property AlertCondition.
     *
     * @param required Indicates if this condition is required or optional for
     * this alert to fire.
     * @param property The custom property to evaluate for changes.
     *
     * @return A property AlertCondition.
     */
    public static AlertCondition createPropertyCondition(boolean required,
                                                         String property) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.CUSTOM_PROP.getType());
        c.setProperty(property);
        return c;
    }

    /**
     * Indicates the event log level to check.
     * @see org.hyperic.hq.hqapi1.AlertDefinitionBuilder#createLogCondition(boolean, org.hyperic.hq.hqapi1.AlertDefinitionBuilder.AlertLogLevel, String)
     */
    public enum AlertLogLevel {

        ANY("ANY"),
        ERROR("ERR"),
        WARN("WRN"),
        INFO("INF"),
        DEBUG("DGB");

        private final String _level;

        AlertLogLevel(String level) {
            _level = level;
        }

        public String getLevel() {
            return _level;
        }
    }

    /**
     * Create a log AlertCondition.
     *
     * @param required Indicates if this condition is required or optional for
     * this alert to fire.
     * @param logLevel The log level to evaluate.
     * @param matches The string regex to search for.
     *
     * @return A log AlertCondition.
     */
    public static AlertCondition createLogCondition(boolean required,
                                                    AlertLogLevel logLevel,
                                                    String matches) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.LOG.getType());
        c.setLogLevel(logLevel.getLevel());
        c.setLogMatches(matches);
        return c;
    }

    /**
     * Create a config change AlertCondition.
     *
     * @param required Indicates if this condition is required or optional for
     * this alert to fire.
     * @param matches Optional file name to check for changes.  Set this to null
     * to evaluate against all items watched for configuration changes.
     *
     * @return A config change AlertCondition.
     */
    public static AlertCondition createConfigCondition(boolean required,
                                                       String matches) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.CONFIG_CHANGE.getType());
        c.setConfigMatch(matches);
        return c;
    }

    /**
     * Create a Script AlertAction
     *
     * @param script The script to execute when the alert fires.
     * @return An {@link org.hyperic.hq.hqapi1.types.AlertAction} that can be
     * included in {@link org.hyperic.hq.hqapi1.types.AlertDefinition#getAlertAction()}.
     */
    public static AlertAction createScriptAction(String script) {
        AlertAction a = new AlertAction();
        a.setClassName("com.hyperic.hq.bizapp.server.action.control.ScriptAction");

        AlertActionConfig cfg = new AlertActionConfig();
        cfg.setKey("script");
        cfg.setValue(script);

        a.getAlertActionConfig().add(cfg);
        return a;
    }

    /**
     * Create a ControlAction AlertAction
     *
     * @param r The {@link Resource} to run the action on
     * @param action The control action to run
     *
     * @return An {@link org.hyperic.hq.hqapi1.types.AlertAction} that can be
     * included in {@link org.hyperic.hq.hqapi1.types.AlertDefinition#getAlertAction()}.
     */
    public static AlertAction createControlAction(Resource r, String action) {
        AlertAction a = new AlertAction();
        a.setClassName("com.hyperic.hq.bizapp.server.action.control.ControlAction");

        AlertActionConfig resourceCfg = new AlertActionConfig();
        resourceCfg.setKey("resourceId");
        resourceCfg.setValue(Integer.toString(r.getId()));

        AlertActionConfig actionCfg = new AlertActionConfig();
        actionCfg.setKey("action");
        actionCfg.setValue(action);

        a.getAlertActionConfig().add(resourceCfg);
        a.getAlertActionConfig().add(actionCfg);

        return a;
    }

    /**
     * Create an OpenNMS AlertAction
     *
     * @param server The address of the OpenNMS server
     * @param port The port the OpenNMS server is listening on
     *
     * @return An {@link org.hyperic.hq.hqapi1.types.AlertAction} that can be
     * included in {@link org.hyperic.hq.hqapi1.types.AlertDefinition#getAlertAction()}.
     */
    public static AlertAction createOpenNMSAction(String server, int port) {
        AlertAction a = new AlertAction();
        a.setClassName("org.hyperic.hq.bizapp.server.action.integrate.OpenNMSAction");

        AlertActionConfig serverCfg = new AlertActionConfig();
        serverCfg.setKey("server");
        serverCfg.setValue(server);

        AlertActionConfig portCfg = new AlertActionConfig();
        portCfg.setKey("port");
        portCfg.setValue(Integer.toString(port));

        a.getAlertActionConfig().add(serverCfg);
        a.getAlertActionConfig().add(portCfg);

        return a;
    }
}
