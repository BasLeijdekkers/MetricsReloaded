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
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.metricModel.MetricAbbreviationComparator;
import com.sixrr.metrics.metricModel.MetricsResult;
import com.sixrr.metrics.metricModel.MetricsRun;
import org.jetbrains.annotations.NonNls;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class XMLExporter implements Exporter {

    private final MetricsRun run;

    public XMLExporter(MetricsRun run) {
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
        writer.println("<METRICS profile=\"" + StringUtil.escapeXmlEntities(run.getProfileName()) + "\" timestamp=\"" +
                run.getTimestamp() + "\">");
        writeContext(run.getContext());
        for (MetricCategory category : MetricCategory.values()) {
            writeResultsForCategory(category, writer);
        }
        writer.println("</METRICS>");
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
        writer.println("\t<METRIC category=\"" + category.name() + "\" name=\"" +
                metric.getDisplayName() + "\" abbreviation=\"" + metric.getAbbreviation() + "\">");
        for (final String measuredObject : results.getMeasuredObjects()) {
            writeValue(results, metric, measuredObject, writer);
        }
        writer.println("\t</METRIC>");
    }

    private static void writeValue(MetricsResult results, Metric metric, String measuredObject,
                                   @NonNls PrintWriter writer) {
        final Double value = results.getValueForMetric(metric, measuredObject);
        if (value != null) {
            writer.println("\t\t<VALUE measured=\"" + StringUtil.escapeXmlEntities(measuredObject) + "\" value=\"" + value + "\"/>");
        }
    }
}
