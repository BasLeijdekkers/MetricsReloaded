/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricType;
import com.sixrr.metrics.profile.MetricInstance;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.utils.StringToFractionMap;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MetricsResultImpl implements MetricsResult {
    private final Map<Metric, StringToFractionMap> values = new HashMap<Metric, StringToFractionMap>(32);
    private final Set<String> measuredObjects = new HashSet<String>(32);
    private final Set<Metric> metrics = new HashSet<Metric>(32);
    private final Map<String, SmartPsiElementPointer<PsiElement>> elements =
            new HashMap<String, SmartPsiElementPointer<PsiElement>>(1024);

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
    public String[] getMeasuredObjects() {
        return measuredObjects.toArray(new String[measuredObjects.size()]);
    }

    @Override
    public Metric[] getMetrics() {
        return metrics.toArray(new Metric[metrics.size()]);
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
        final Project project = element.getProject();
        final SmartPointerManager pointerManager = SmartPointerManager.getInstance(project);
        final SmartPsiElementPointer<PsiElement> pointer = pointerManager.createSmartPsiElementPointer(element);
        elements.put(measuredObject, pointer);
    }

    @Override
    @Nullable
    public PsiElement getElementForMeasuredObject(String measuredObject) {
        final SmartPsiElementPointer<PsiElement> pointer = elements.get(measuredObject);
        if (pointer == null) {
            return null;
        }
        return pointer.getElement();
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
    public MetricsResult filterRowsWithoutWarnings(MetricsProfile profile) {
        final MetricsResult out = new MetricsResultImpl();
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
                final PsiElement elementForMeasuredObject = getElementForMeasuredObject(measuredObject);
                if (elementForMeasuredObject != null) {
                    out.setElementForMeasuredObject(measuredObject, elementForMeasuredObject);
                }
            }
        }
        return out;

    }
}
