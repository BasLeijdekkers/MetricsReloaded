/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sixrr.stockmetrics;

import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricProvider;
import com.sixrr.metrics.PrebuiltMetricProfile;
import com.sixrr.stockmetrics.i18n.StockMetricsBundle;
import com.sixrr.stockmetrics.moduleMetrics.LinesOfScalaModuleMetric;
import com.sixrr.stockmetrics.moduleMetrics.NumScalaFilesModuleMetric;
import com.sixrr.stockmetrics.projectMetrics.LinesOfScalaProjectMetric;
import com.sixrr.stockmetrics.projectMetrics.NumScalaFilesProjectMetric;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bas Leijdekkers
 */
public class ScalaMetricProvider implements MetricProvider {

    @NotNull
    @Override
    public List<Metric> getMetrics() {
        final List<Metric> metrics = new ArrayList<Metric>(4);
        metrics.add(new NumScalaFilesProjectMetric());
        metrics.add(new NumScalaFilesModuleMetric());
        metrics.add(new LinesOfScalaProjectMetric());
        metrics.add(new LinesOfScalaModuleMetric());
        return metrics;
    }

    @NotNull
    @Override
    public List<PrebuiltMetricProfile> getPrebuiltProfiles() {
        final List<PrebuiltMetricProfile> out = new ArrayList<PrebuiltMetricProfile>(2);
        out.add(createCodeSizeProfile());
        out.add(createFileCountProfile());
        return out;
    }

    private static PrebuiltMetricProfile createCodeSizeProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("lines.of.code.metrics.profile.name"));
        profile.addMetric(LinesOfScalaProjectMetric.class);
        profile.addMetric(LinesOfScalaModuleMetric.class);
        return profile;
    }

    private static  PrebuiltMetricProfile createFileCountProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("file.count.metrics.profile.name"));
        profile.addMetric(NumScalaFilesProjectMetric.class);
        profile.addMetric(NumScalaFilesModuleMetric.class);
        return profile;
    }
}
