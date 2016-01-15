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
import com.sixrr.stockmetrics.moduleMetrics.LinesOfJSPModuleMetric;
import com.sixrr.stockmetrics.moduleMetrics.NumJSPFilesModuleMetric;
import com.sixrr.stockmetrics.projectMetrics.LinesOfJSPProjectMetric;
import com.sixrr.stockmetrics.projectMetrics.NumJSPFilesProjectMetric;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Bas Leijdekkers
 */
public class JSPMetricProvider implements MetricProvider {

    private final List<Class<? extends Metric>> metricsClasses = new ArrayList<Class<? extends Metric>>();

    @NotNull
    @Override
    public List<Class<? extends Metric>> getMetricClasses() {
        if (metricsClasses.isEmpty()) {
            metricsClasses.add(NumJSPFilesModuleMetric.class);
            metricsClasses.add(LinesOfJSPModuleMetric.class);
            metricsClasses.add(NumJSPFilesProjectMetric.class);
            metricsClasses.add(LinesOfJSPProjectMetric.class);
        }
        return Collections.unmodifiableList(metricsClasses);
    }

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
        profile.addMetric("LinesOfJSPProject");
        profile.addMetric("LinesOfJSPModule");
        return profile;
    }

    private static  PrebuiltMetricProfile createFileCountProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("file.count.metrics.profile.name"));
        profile.addMetric("NumJSPFilesProject");
        profile.addMetric("NumJSPFilesModule");
        return profile;
    }
}
