/*
 * Copyright 2005-2011 Sixth and Red River Software, Bas Leijdekkers
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.sixrr.metrics.metricModel;

import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;

public class MetricInstanceImpl implements MetricInstance {

    private final Metric metric;
    private boolean enabled = false;
    private boolean upperThresholdEnabled = false;
    private double upperThreshold = 0.0;
    private boolean lowerThresholdEnabled = false;
    private double lowerThreshold = 0.0;

    public MetricInstanceImpl(Metric metric) {
        this.metric = metric;
    }

    public void copyFrom(MetricInstance o) {
        upperThresholdEnabled = o.isUpperThresholdEnabled();
        upperThreshold = o.getUpperThreshold();
        lowerThresholdEnabled = o.isLowerThresholdEnabled();
        lowerThreshold = o.getLowerThreshold();
    }

    public int compareTo(MetricInstance o) {
        final MetricCategory category1 = metric.getCategory();
        final MetricCategory category2 = o.getMetric().getCategory();
        final int categoryComparison = category1.compareTo(category2);
        if (categoryComparison != 0) {
            return -categoryComparison;
        }
        final String displayName1 = metric.getDisplayName();
        final String displayName2 = o.getMetric().getDisplayName();
        return displayName1.compareTo(displayName2);
    }

    public boolean equals(Object o) {
        if (o.getClass() != MetricInstanceImpl.class) {
            return false;
        }
        final MetricInstanceImpl other = (MetricInstanceImpl) o;
        final MetricCategory category1 = metric.getCategory();
        final MetricCategory category2 = other.metric.getCategory();
        if (category1 != category2) {
            return false;
        }
        final String displayName1 = metric.getDisplayName();
        final String displayName2 = other.metric.getDisplayName();
        return displayName1.equals(displayName2);
    }

    @Override
    public int hashCode() {
        return 31 * metric.getCategory().hashCode() + metric.getDisplayName().hashCode();
    }

    public Metric getMetric() {
        return metric;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUpperThresholdEnabled() {
        return upperThresholdEnabled;
    }

    public void setUpperThresholdEnabled(boolean upperThresholdEnabled) {
        this.upperThresholdEnabled = upperThresholdEnabled;
    }

    public double getUpperThreshold() {
        return upperThreshold;
    }

    public void setUpperThreshold(double upperThreshold) {
        this.upperThreshold = upperThreshold;
    }

    public boolean isLowerThresholdEnabled() {
        return lowerThresholdEnabled;
    }

    public void setLowerThresholdEnabled(boolean lowerThresholdEnabled) {
        this.lowerThresholdEnabled = lowerThresholdEnabled;
    }

    public double getLowerThreshold() {
        return lowerThreshold;
    }

    public void setLowerThreshold(double lowerThreshold) {
        this.lowerThreshold = lowerThreshold;
    }

    @Override
    public MetricInstanceImpl clone() throws CloneNotSupportedException {
        return (MetricInstanceImpl) super.clone();
    }

    public String toString() {
        return metric.getCategory() + "/" + metric.getDisplayName();
    }
}
