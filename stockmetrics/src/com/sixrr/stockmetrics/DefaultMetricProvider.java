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

package com.sixrr.stockmetrics;

import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricProvider;
import com.sixrr.metrics.PrebuiltMetricProfile;
import com.sixrr.stockmetrics.classMetrics.SourceLinesOfCodeClassMetric;
import com.sixrr.stockmetrics.fileTypeMetrics.*;
import com.sixrr.stockmetrics.i18n.StockMetricsBundle;
import com.sixrr.stockmetrics.interfaceMetrics.SourceLinesOfCodeInterfaceMetric;
import com.sixrr.stockmetrics.methodMetrics.SourceLinesOfCodeMethodMetric;
import com.sixrr.stockmetrics.moduleMetrics.*;
import com.sixrr.stockmetrics.packageMetrics.*;
import com.sixrr.stockmetrics.projectMetrics.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultMetricProvider implements MetricProvider {

    @NotNull
    @Override
    public List<Metric> getMetrics() {
        final List<Metric> metrics = new ArrayList<Metric>(38);
        initializeFileTypeMetrics(metrics);
        initializeModuleMetrics(metrics);
        initializeProjectMetrics(metrics);
        return metrics;
    }

    private static void initializeFileTypeMetrics(Collection<Metric> metrics) {
        metrics.add(new CommentLinesOfCodeFileTypeMetric());
        metrics.add(new CommentRatioFileTypeMetric());
        metrics.add(new LinesOfCodeFileTypeMetric());
        metrics.add(new NonCommentLinesOfCodeFileTypeMetric());
        metrics.add(new NumFilesFileTypeMetric());
        metrics.add(new TodoCommentCountFileTypeMetric());
    }

    private static void initializeModuleMetrics(Collection<Metric> metrics) {
        metrics.add(new CommentLinesOfCodeModuleMetric());
        metrics.add(new CommentRatioModuleMetric());
        metrics.add(new LinesOfCodeModuleMetric());
        metrics.add(new LinesOfHTMLModuleMetric());
        metrics.add(new LinesOfProductCodeModuleMetric());
        metrics.add(new LinesOfTestCodeModuleMetric());
        metrics.add(new LinesOfXMLModuleMetric());
        metrics.add(new NumFilesModuleMetric());
        metrics.add(new NumHTMLFilesModuleMetric());
        metrics.add(new NumXMLFilesModuleMetric());
        metrics.add(new SourceLinesOfCodeModuleMetric());
        metrics.add(new SourceLinesOfCodeProductModuleMetric());
        metrics.add(new SourceLinesOfCodeTestModuleMetric());
        metrics.add(new TestRatioModuleMetric());
        metrics.add(new TodoCommentCountModuleMetric());
        metrics.add(new TrueCommentRatioModuleMetric());
    }

    private static void initializeProjectMetrics(Collection<Metric> metrics) {
        metrics.add(new CommentLinesOfCodeProjectMetric());
        metrics.add(new CommentRatioProjectMetric());
        metrics.add(new LinesOfCodeProjectMetric());
        metrics.add(new LinesOfHTMLProjectMetric());
        metrics.add(new LinesOfProductCodeProjectMetric());
        metrics.add(new LinesOfTestCodeProjectMetric());
        metrics.add(new LinesOfXMLProjectMetric());
        metrics.add(new NumFilesProjectMetric());
        metrics.add(new NumHTMLFilesProjectMetric());
        metrics.add(new NumXMLFilesProjectMetric());
        metrics.add(new SourceLinesOfCodeProductProjectMetric());
        metrics.add(new SourceLinesOfCodeProjectMetric());
        metrics.add(new SourceLinesOfCodeTestProjectMetric());
        metrics.add(new TestRatioProjectMetric());
        metrics.add(new TodoCommentCountProjectMetric());
        metrics.add(new TrueCommentRatioProjectMetric());
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
        profile.addMetric(LinesOfCodeFileTypeMetric.class);
        profile.addMetric(LinesOfCodeModuleMetric.class);
        profile.addMetric(LinesOfCodeProjectMetric.class);
        profile.addMetric(LinesOfHTMLModuleMetric.class);
        profile.addMetric(LinesOfHTMLProjectMetric.class);
        profile.addMetric(LinesOfProductCodeModuleMetric.class);
        profile.addMetric(LinesOfProductCodeProjectMetric.class);
        profile.addMetric(LinesOfTestCodeModuleMetric.class);
        profile.addMetric(LinesOfTestCodeProjectMetric.class);
        profile.addMetric(LinesOfXMLModuleMetric.class);
        profile.addMetric(LinesOfXMLProjectMetric.class);
        profile.addMetric(NonCommentLinesOfCodeFileTypeMetric.class);
        profile.addMetric(SourceLinesOfCodeModuleMetric.class);
        profile.addMetric(SourceLinesOfCodeProductModuleMetric.class);
        profile.addMetric(SourceLinesOfCodeProductProjectMetric.class);
        profile.addMetric(SourceLinesOfCodeProjectMetric.class);
        profile.addMetric(SourceLinesOfCodeTestModuleMetric.class);
        profile.addMetric(SourceLinesOfCodeTestProjectMetric.class);
        return profile;
    }

    private static PrebuiltMetricProfile createFileCountProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("file.count.metrics.profile.name"));
        profile.addMetric(NumFilesFileTypeMetric.class);
        profile.addMetric(NumFilesModuleMetric.class);
        profile.addMetric(NumFilesProjectMetric.class);
        profile.addMetric(NumHTMLFilesModuleMetric.class);
        profile.addMetric(NumHTMLFilesProjectMetric.class);
        profile.addMetric(NumXMLFilesModuleMetric.class);
        profile.addMetric(NumXMLFilesProjectMetric.class);
        return profile;
    }
}
