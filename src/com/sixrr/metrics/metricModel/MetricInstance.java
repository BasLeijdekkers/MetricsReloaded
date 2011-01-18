package com.sixrr.metrics.metricModel;

import com.sixrr.metrics.Metric;

public interface MetricInstance extends Cloneable {
    Metric getMetric();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isUpperThresholdEnabled();

    void setUpperThresholdEnabled(boolean upperThresholdEnabled);

    void setUpperThreshold(double upperThreshold);

    boolean isLowerThresholdEnabled();

    void setLowerThresholdEnabled(boolean lowerThresholdEnabled);

    void setLowerThreshold(double lowerThreshold);

    double getUpperThreshold();

    double getLowerThreshold();

    MetricInstance clone() throws CloneNotSupportedException;
}
