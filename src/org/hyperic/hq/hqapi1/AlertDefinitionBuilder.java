package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.hyperic.hq.hqapi1.types.AlertDefinition;

/**
 * This class is used to create an @{link AlertCondition}.
 */
public class AlertDefinitionBuilder {

    private static AlertCondition createBaseCondition(boolean required) {
        AlertCondition c = new AlertCondition();
        c.setRequired(required);
        return c;
    }

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

    public enum AlertBaseline {

        MEAN("mean"),
        MIN("min"),
        MAX("max;");

        private final String _baselineType;

        AlertBaseline(String baselineType) {
            _baselineType = baselineType;
        }

        public String getBaselineType() {
            return _baselineType;
        }
    }

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

    public static AlertCondition createControlCondition(boolean required,
                                                        String action,
                                                        AlertControlStatus status) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.CONTROL.getType());
        c.setControlAction(action);
        c.setControlStatus(status.getControlStatus());

        return c;
    }

    public static AlertCondition createChangeCondition(boolean required,
                                                       String metric) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.METRIC_CHANGE.getType());
        c.setMetricChange(metric);
        return c;
    }

    public static AlertCondition createRecoveryCondition(boolean required,
                                                         AlertDefinition recover) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.RECOVERY.getType());
        c.setRecover(recover.getName());
        return c;
    }

    public static AlertCondition createPropertyCondition(boolean required,
                                                         String property) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.CUSTOM_PROP.getType());
        c.setProperty(property);
        return c;
    }

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

    public static AlertCondition createLogCondition(boolean required,
                                                    AlertLogLevel logLevel ,
                                                    String matches) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.LOG.getType());
        c.setLogLevel(logLevel.getLevel());
        c.setLogMatches(matches);
        return c;
    }

    public static AlertCondition createConfigCondition(boolean required,
                                                       String matches) {
        AlertCondition c = createBaseCondition(required);
        c.setType(AlertConditionType.CONFIG_CHANGE.getType());
        c.setConfigMatch(matches);
        return c;
    }
}
