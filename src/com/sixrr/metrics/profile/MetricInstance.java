/*
 * Copyright 2005-2020 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.metrics.profile;

import com.sixrr.metrics.Metric;

public interface MetricInstance extends Cloneable, Comparable<MetricInstance> {

    MetricInstance[] EMPTY_ARRAY = {};

    Metric getMetric();

    void copyFrom(MetricInstance o);

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
