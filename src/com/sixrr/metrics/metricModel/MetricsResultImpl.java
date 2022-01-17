/*
 * Copyright 2005-2022 Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.ArrayUtil;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricType;
import com.sixrr.metrics.profile.MetricInstance;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.utils.StringToFractionMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MetricsResultImpl implements MetricsResult {
    private static final double[] EMPTY_DOUBLE_ARRAY = new double[0];

    private final Map<Metric, StringToFractionMap> values = new HashMap<>(32);
    private final Set<String> measuredObjects = new HashSet<>(32);
    private final Set<Metric> metrics = new HashSet<>(32);
    private final Map<String, Object> elements = new HashMap<>(1024);

    @Override
    public void postValue(Metric metric, String measured, double value) {
        postValue(metric, measured, value, 1.0);
    }

    @Override
    public void postValue(Metric metric, String measured, double numerator, double denominator) {
        if (measured == null) {
            return;
        }
        StringToFractionMap metricValues = values.get(metric);
        if (metricValues == null) {
            metricValues = new StringToFractionMap();
            values.put(metric, metricValues);
        }
        metricValues.put(measured, numerator, denominator);
        measuredObjects.add(measured);
        metrics.add(metric);
    }

    @Override
    @Nullable
    public Double getValueForMetric(Metric metric, String measured) {
        final StringToFractionMap metricValues = values.get(metric);
        if (metricValues == null) {
            return null;
        }
        return metricValues.containsKey(measured) ? Double.valueOf(metricValues.get(measured)) : null;
    }

    @Override
    public double[] getValuesForMetric(Metric metric) {
        final StringToFractionMap metricValues = values.get(metric);
        if (metricValues == null) {
            return EMPTY_DOUBLE_ARRAY;
        }
        final String[] measureds = metricValues.getKeys();
        final double[] result = new double[measureds.length];
        for (int i = 0, max = measureds.length; i < max; i++) {
            result[i] = metricValues.get(measureds[i]);
        }
        return result;
    }

    @Override
    public String[] getMeasuredObjects() {
        final String[] array = measuredObjects.toArray(ArrayUtil.EMPTY_STRING_ARRAY);
        Arrays.sort(array);
        return array;
    }

    @Override
    public Metric[] getMetrics() {
        final Metric[] metrics = this.metrics.toArray(Metric.EMPTY_ARRAY);
        Arrays.sort(metrics, new MetricAbbreviationComparator());
        return metrics;
    }

    @Override
    @Nullable
    public Double getMinimumForMetric(Metric metric) {
        final StringToFractionMap metricValues = values.get(metric);
        if (metricValues == null) {
            return Double.valueOf(0.0);
        }
        return Double.valueOf(metricValues.getMinimum());
    }

    @Override
    @Nullable
    public Double getMaximumForMetric(Metric metric) {
        final StringToFractionMap metricValues = values.get(metric);
        if (metricValues == null) {
            return Double.valueOf(0.0);
        }
        return Double.valueOf(metricValues.getMaximum());
    }

    @Override
    @Nullable
    public Double getTotalForMetric(Metric metric) {
        final MetricType metricType = metric.getType();
        if (metricType != MetricType.Count) {
            return null;
        }
        final StringToFractionMap metricValues = values.get(metric);
        if (metricValues == null) {
            return Double.valueOf(0.0);
        }
        return Double.valueOf(metricValues.getTotal());
    }

    @Override
    @Nullable
    public Double getAverageForMetric(Metric metric) {
        final MetricType metricType = metric.getType();
        if (metricType == MetricType.RecursiveCount || metricType == MetricType.RecursiveRatio) {
            return null;
        }
        final StringToFractionMap metricValues = values.get(metric);
        if (metricValues == null) {
            return Double.valueOf(0.0);
        }
        return Double.valueOf(metricValues.getAverage());
    }

    @Override
    public void setElementForMeasuredObject(String measuredObject, PsiElement element) {
        ReadAction.run(() -> {
            final Project project = element.getProject();
            final SmartPointerManager pointerManager = SmartPointerManager.getInstance(project);
            final SmartPsiElementPointer<PsiElement> pointer = pointerManager.createSmartPsiElementPointer(element);
            elements.put(measuredObject, pointer);
        });
    }

    @Override
    @Nullable
    public PsiElement getElementForMeasuredObject(String measuredObject) {
        final Object o = elements.get(measuredObject);
        if (!(o instanceof SmartPsiElementPointer)) {
            return null;
        }
        final SmartPsiElementPointer<PsiElement> pointer = (SmartPsiElementPointer<PsiElement>) o;
        return pointer.getElement();
    }

    @Override
    public void setOriginalForMeasuredObject(String measuredObject, Object original) {
        elements.put(measuredObject, original);
    }

    @Override
    public <T> T getOriginalForMeasuredObject(String measuredObject) {
        final Object o = elements.get(measuredObject);
        if (o instanceof SmartPsiElementPointer) {
            return null;
        }
        return (T)o;
    }

    @Override
    public boolean hasWarnings(MetricsProfile profile) {
        for (Metric metric : metrics) {
            final MetricInstance metricInstance = profile.getMetricInstance(metric);
            assert metricInstance != null : "no instance found for " + metric.getID();
            final StringToFractionMap valuesForMetric = values.get(metric);
            for (String measuredObject : measuredObjects) {
                final double value = valuesForMetric.get(measuredObject);
                if (metricInstance.isUpperThresholdEnabled() && value > metricInstance.getUpperThreshold()) {
                    return true;
                }
                if (metricInstance.isLowerThresholdEnabled() && value < metricInstance.getLowerThreshold()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasValues() {
        return !values.isEmpty();
    }

    @Override
    public MetricsResult filterRowsWithoutWarnings(MetricsProfile profile) {
        final MetricsResultImpl out = new MetricsResultImpl();
        for (String measuredObject : measuredObjects) {
            boolean found = false;
            for (Metric metric : metrics) {
                final MetricInstance metricInstance = profile.getMetricInstance(metric);
                assert metricInstance != null : "no instance found for " + metric.getID();
                if (!metricInstance.isEnabled()) {
                    continue;
                }
                final StringToFractionMap valuesForMetric = values.get(metric);
                if (!valuesForMetric.containsKey(measuredObject)) {
                    continue;
                }
                final double value = valuesForMetric.get(measuredObject);
                if (metricInstance.isUpperThresholdEnabled() && value > metricInstance.getUpperThreshold()) {
                    found = true;
                    break;
                }
                if (metricInstance.isLowerThresholdEnabled() && value < metricInstance.getLowerThreshold()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                for (Metric metric : metrics) {
                    final StringToFractionMap valuesForMetric = values.get(metric);
                    final double value = valuesForMetric.get(measuredObject);
                    out.postValue(metric, measuredObject, value, 1.0); //not quite right
                }
                final Object o = elements.get(measuredObject);
                if (o != null) {
                    out.elements.put(measuredObject, o);
                }
            }
        }
        return out;
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (Map.Entry<Metric, StringToFractionMap> entry : values.entrySet()) {
            result.append(entry.getKey().getDisplayName()).append('\n');
            final StringToFractionMap value = entry.getValue();
            for (String key : value.getKeys()) {
                result.append(key).append(": ").append(value.get(key)).append('\n');
            }
        }
        return result.toString();
    }
}
