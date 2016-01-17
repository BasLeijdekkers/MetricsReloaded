/*
 * Copyright 2016 Sixth and Red River Software, Bas Leijdekkers
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
package com.sixrr.stockmetrics;

import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricProvider;
import com.sixrr.metrics.PrebuiltMetricProfile;
import com.sixrr.stockmetrics.i18n.StockMetricsBundle;
import com.sixrr.stockmetrics.moduleMetrics.LinesOfKotlinModuleMetric;
import com.sixrr.stockmetrics.moduleMetrics.NumKotlinFilesModuleMetric;
import com.sixrr.stockmetrics.projectMetrics.LinesOfKotlinProjectMetric;
import com.sixrr.stockmetrics.projectMetrics.NumKotlinFilesProjectMetric;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Bas Leijdekkers
 */
public class KotlinMetricProvider implements MetricProvider {

    private final List<Class<? extends Metric>> metricsClasses = new ArrayList<Class<? extends Metric>>();

    @NotNull
    @Override
    public List<Class<? extends Metric>> getMetricClasses() {
        if (metricsClasses.isEmpty()) {
            metricsClasses.add(NumKotlinFilesProjectMetric.class);
            metricsClasses.add(NumKotlinFilesModuleMetric.class);
            metricsClasses.add(LinesOfKotlinProjectMetric.class);
            metricsClasses.add(LinesOfKotlinModuleMetric.class);
        }
        return Collections.unmodifiableList(metricsClasses);    }

    @NotNull
    @Override
    public List<PrebuiltMetricProfile> getPrebuiltProfiles() {
        final List<PrebuiltMetricProfile> out = new ArrayList<PrebuiltMetricProfile>();
        out.add(createCodeSizeProfile());
        out.add(createFileCountProfile());
        return out;
    }

    private static PrebuiltMetricProfile createCodeSizeProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("lines.of.code.metrics.profile.name"));
        profile.addMetric(LinesOfKotlinProjectMetric.class);
        profile.addMetric(LinesOfKotlinModuleMetric.class);
        return profile;
    }

    private static  PrebuiltMetricProfile createFileCountProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("file.count.metrics.profile.name"));
        profile.addMetric(NumKotlinFilesProjectMetric.class);
        profile.addMetric(NumKotlinFilesModuleMetric.class);
        return profile;
    }
}
