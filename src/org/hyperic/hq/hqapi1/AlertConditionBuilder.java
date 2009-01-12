package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.AlertCondition;
import org.hyperic.hq.hqapi1.types.AlertDefinition;

/**
 * This class is used to create an @{link AlertCondition}.
 */
public class AlertConditionBuilder {

    private AlertCondition createBaseCondition(boolean required) {
        AlertCondition c = new AlertCondition();
        c.setRequired(required);
        return c;
    }

    public enum AlertComparator {

        EQUALS("="),
        NOT_EQUALS("!="),
        LESS_THAN("&lt;"),
        GREATER_THAN("&gt");

        private final String _comparator;

        AlertComparator(String comparator) {
            _comparator = comparator;
        }

        public String getComparator() {
            return _comparator;
        }
    }

    public AlertCondition createThresholdCondition(boolean required,
                                                   String metric,
                                                   AlertComparator comparator,
                                                   double threshold) {
        AlertCondition c = createBaseCondition(required);
        c.setType(1);
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

    public AlertCondition createBaselineCondition(boolean required,
                                                  String metric,
                                                  AlertComparator comparator,
                                                  double percentage,
                                                  AlertBaseline type) {
        AlertCondition c = createBaseCondition(required);
        c.setType(2);
        c.setBaselineMetric(metric);
        c.setBaselineComparator(comparator.getComparator());
        c.setBaselinePercentage(percentage);
        c.setBaselineType(type.getBaselineType());

        return c;
    }

    public enum AlertControlStatus {

        COMPLETED("Completed"),
        IN_PROGRESS("In Progress");

        private final String _controlStatus;

        AlertControlStatus(String controlStatus) {
            _controlStatus = controlStatus;
        }

        public String getControlStatus() {
            return _controlStatus;
        }
    }

    public AlertCondition createControlCondition(boolean required,
                                                 String action,
                                                 AlertControlStatus status) {
        AlertCondition c = createBaseCondition(required);
        c.setType(3);
        c.setControlAction(action);
        c.setControlStatus(status.getControlStatus());

        return c;
    }

    public AlertCondition createChangeCondition(boolean required,
                                                String metric) {
        AlertCondition c = createBaseCondition(required);
        c.setType(4);
        c.setMetricChange(metric);
        return c;
    }

    public AlertCondition createRecoveryCondition(boolean required,
                                                  AlertDefinition recover) {
        AlertCondition c = createBaseCondition(required);
        c.setType(5);
        c.setRecover(recover.getName());
        return c;
    }

    public AlertCondition createPropertyCondition(boolean required,
                                                  String property) {
        AlertCondition c = createBaseCondition(required);
        c.setType(6);
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

    public AlertCondition createLogCondition(boolean required,
                                             AlertLogLevel logLevel ,
                                             String matches) {
        AlertCondition c = createBaseCondition(required);
        c.setType(7);
        c.setLogLevel(logLevel.getLevel());
        c.setLogMatches(matches);
        return c;
    }

    public AlertCondition createConfigCondition(boolean required,
                                                String matches) {
        AlertCondition c = createBaseCondition(required);
        c.setType(8);
        c.setConfigMatch(matches);
        return c;
    }
}
