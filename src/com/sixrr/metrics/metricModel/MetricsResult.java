/*
 * Copyright 2005, Sixth and Red River Software
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

import com.intellij.psi.PsiElement;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.profile.MetricsProfile;
import org.jetbrains.annotations.Nullable;

public interface MetricsResult {

    void postValue(Metric metric, String measured, double value);

    void postValue(Metric metric, String measured, double numerator, double denominator);

    @Nullable
    Double getValueForMetric(Metric metric, String measured);

    String[] getMeasuredObjects();

    Metric[] getMetrics();

    @Nullable
    Double getTotalForMetric(Metric metric);

    @Nullable
    Double getAverageForMetric(Metric metric);

    @Nullable
    Double getMinimumForMetric(Metric metric);

    @Nullable
    Double getMaximumForMetric(Metric metric);

    void setElementForMeasuredObject(String measuredObject, PsiElement element);

    @Nullable
    PsiElement getElementForMeasuredObject(String measuredObject);

    boolean hasWarnings(MetricsProfile profile);

    MetricsResult filterRowsWithoutWarnings(MetricsProfile profile);
}
