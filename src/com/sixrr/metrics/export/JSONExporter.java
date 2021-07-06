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

package com.sixrr.metrics.export;

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.metricModel.MetricAbbreviationComparator;
import com.sixrr.metrics.metricModel.MetricsResult;
import com.sixrr.metrics.metricModel.MetricsRun;
import com.sixrr.metrics.utils.FormatSpanUtils;
import org.jetbrains.annotations.NonNls;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class JSONExporter implements Exporter {

    private final MetricsRun run;

    public JSONExporter(MetricsRun run) {
        this.run = run;
    }

    @Override
    public void export(String fileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName))) {
            export(writer);
        }
    }

    @Override
    public void export(@NonNls PrintWriter writer) {
        writer.println("{");
        writer.println("\t\"profile\": \"" + StringUtil.escapeXmlEntities(run.getProfileName()) + "\",");
        writer.println("\t\"timestamp\": \"" + run.getTimestamp() + "\",");
        writer.println("\t\"METRIC\": [");
        writeContext(run.getContext());
        for (MetricCategory category : MetricCategory.values()) {
            writeResultsForCategory(category, writer);
        }
        writer.println("\t]");
        writer.println("}");
    }

    private void writeContext(AnalysisScope context) {
    }

    private void writeResultsForCategory(MetricCategory category, PrintWriter writer) {
        final MetricsResult results = run.getResultsForCategory(category);
        final Metric[] metrics = results.getMetrics();
        Arrays.sort(metrics, new MetricAbbreviationComparator());
        for (final Metric metric : metrics) {
            writeResultsForMetric(category, metric, results, writer);
        }
    }

    private static void writeResultsForMetric(MetricCategory category, Metric metric, MetricsResult results,
                                              @NonNls PrintWriter writer) {
        writer.println("\t\t{");
        writer.println("\t\t\t\"category\": \"" + category.name() + "\",");
        writer.println("\t\t\t\"name\": \"" + metric.getDisplayName() + "\",");
        writer.println("\t\t\t\"abbreviation\": \"" + metric.getAbbreviation() + "\",");
        writer.println("\t\t\t\"VALUE\": [");
        for (final String measuredObject : results.getMeasuredObjects()) {
            writeValue(results, metric, measuredObject, writer);
        }
        writer.println("\t\t\t]");
        MetricCategory[] categories = MetricCategory.values();
        final Metric[] metrics = results.getMetrics();
        if (category == categories[categories.length - 1] && metric == metrics[metrics.length - 1]) {
            writer.println("\t\t}");
        } else {
            writer.println("\t\t},");
        }
    }

    private static void writeValue(MetricsResult results, Metric metric, String measuredObject,
                                   @NonNls PrintWriter writer) {
        final Double value = results.getValueForMetric(metric, measuredObject);
        final PsiElement element = results.getElementForMeasuredObject(measuredObject);
        FormatSpanUtils currentSpan = new FormatSpanUtils();
        currentSpan.calculateSpanValues(element);
        if (value != null) {
            writer.println("\t\t\t\t{");
            writer.println("\t\t\t\t\t\"measured\": \"" + StringUtil.escapeXmlEntities(measuredObject) + "\",");
            if (element != null && element.getContainingFile() != null) {
                final String filePath = getFilePath(element);
                writer.println("\t\t\t\t\t\"filePath\": \"" + filePath + "\",");
                writer.println("\t\t\t\t\t\"startLine\": \"" + currentSpan.getStartLine() + "\",");
                writer.println("\t\t\t\t\t\"endLine\": \"" + currentSpan.getEndLine() + "\",");
                writer.println("\t\t\t\t\t\"startCol\": \"" + currentSpan.getStartCol() + "\",");
                writer.println("\t\t\t\t\t\"endCol\": \"" + currentSpan.getEndCol() + "\",");
            }
            writer.println("\t\t\t\t\t\"value\": \"" + value + "\"");
            String[] measuredObjects = results.getMeasuredObjects();
            if (measuredObjects[measuredObjects.length - 1].equals(measuredObject)) {
                writer.println("\t\t\t\t}");
            } else {
                writer.println("\t\t\t\t},");
            }
        }
    }

    public static String getFilePath(PsiElement element) {
        String absoluteFilePath = element.getContainingFile().getVirtualFile().getPath();
        String baseFilePath = element.getProject().getBasePath();
        String relativeFilePath = getRelativeFilePath(absoluteFilePath, baseFilePath);
        return relativeFilePath;
    }

    public static String getRelativeFilePath(String absolutePath, String projectPath) {
        Path pathAbsolute = Paths.get(absolutePath);
        Path pathBase = Paths.get(projectPath);
        Path pathRelative = pathBase.relativize(pathAbsolute);
        return pathRelative.toString();
    }
}
