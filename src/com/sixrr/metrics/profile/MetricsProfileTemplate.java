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

package com.sixrr.metrics.profile;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.MetricProvider;
import com.sixrr.metrics.metricModel.MetricInstance;
import com.sixrr.metrics.metricModel.MetricInstanceImpl;
import com.sixrr.metrics.metricModel.MetricsCategoryNameUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"HardCodedStringLiteral"})
class MetricsProfileTemplate {
    private static final Logger LOGGER = Logger.getInstance(MetricsProfileTemplate.class);

    private final List<Class<? extends Metric>> metricsClasses = new ArrayList<Class<? extends Metric>>(200);

    public MetricsProfile instantiate(String name) {
        final List<MetricInstance> metrics = instantiateMetrics();
        return new MetricsProfileImpl(name, metrics);
    }

    public void reconcile(MetricsProfile profile) {
        final List<MetricInstance> metrics = profile.getMetricInstances();
        final List<MetricInstance> newMetrics = new ArrayList<MetricInstance>();
        for (final Class<? extends Metric> metricsClass : metricsClasses) {
            boolean found = false;
            for (final MetricInstance metric : metrics) {
                if (metric.getMetric().getClass().equals(metricsClass)) {
                    newMetrics.add(metric);
                    found = true;
                    break;
                }
            }
            if (!found) {
                final MetricInstance metric = instantiateMetric(metricsClass);
                if (metric != null) {
                    newMetrics.add(metric);
                }
            }
        }
        profile.replaceMetricInstances(newMetrics);
    }

    public void printMetricsDescriptions() {
        final List<MetricInstance> metrics = instantiateMetrics();

        System.out.println(metrics.size() + "  metrics");
        MetricCategory currentCategory = null;
        for (final MetricInstance metricInstance : metrics) {
            final Metric metric = metricInstance.getMetric();
            final MetricCategory category = metric.getCategory();
            if (category != currentCategory) {
                System.out.println(MetricsCategoryNameUtil.getLongNameForCategory(category));
                currentCategory = category;
            }
            System.out.println("    " + metric.getDisplayName());
        }
    }

    private List<MetricInstance> instantiateMetrics() {
        final int numMetrics = metricsClasses.size();
        final List<MetricInstance> metrics = new ArrayList<MetricInstance>(numMetrics);
        for (final Class<? extends Metric> metricsClass : metricsClasses) {
            final MetricInstance metric = instantiateMetric(metricsClass);
            if (metric != null) {
                metrics.add(metric);
            }
        }
        return metrics;
    }

    @Nullable
    private static MetricInstance instantiateMetric(Class<? extends Metric> metricsClass) {
        try {
            final Metric metric = metricsClass.newInstance();
            return new MetricInstanceImpl(metric);
        } catch (InstantiationException e) {
            LOGGER.error(e);
        } catch (IllegalAccessException e) {
            LOGGER.error(e);
        }
        return null;
    }

    public void loadMetricsFromProviders() {
        final Application application = ApplicationManager.getApplication();
        final MetricProvider[] metricProviders = application.getExtensions(MetricProvider.EXTENSION_POINT_NAME);
        for (MetricProvider provider : metricProviders) {
            final List<Class<? extends Metric>> classesForProvider = provider.getMetricClasses();
            metricsClasses.addAll(classesForProvider);
        }
    }
}
