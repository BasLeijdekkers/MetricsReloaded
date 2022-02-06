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

package com.sixrr.metrics.export;

import com.intellij.openapi.util.text.StringUtil;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.metricModel.MetricAbbreviationComparator;
import com.sixrr.metrics.metricModel.MetricsResult;
import com.sixrr.metrics.metricModel.MetricsRun;
import com.sixrr.metrics.utils.FormatUtils;
import org.jetbrains.annotations.NonNls;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class CSVExporter implements Exporter {

    private final MetricsRun run;

    public CSVExporter(MetricsRun run) {
        this.run = run;
    }

    @Override
    public void export(String fileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            export(writer);
        }
    }

    @Override
    public void export(PrintWriter writer) throws IOException {
        writer.print(run.getProfileName());
        writer.print(',');
        writer.println(run.getTimestamp());
        for (MetricCategory category : MetricCategory.values()) {
            writeResultsForCategory(category, writer);
        }
    }

    private void writeResultsForCategory(MetricCategory category, PrintWriter writer) {
        final MetricsResult results = run.getResultsForCategory(category);
        final String categoryName = category.name();
        writeResults(results, writer, categoryName);
    }

    private static void writeResults(MetricsResult results, @NonNls PrintWriter writer, String type) {
        final Metric[] metrics = results.getMetrics();
        if (metrics.length == 0) {
            return;
        }
        Arrays.sort(metrics, new MetricAbbreviationComparator());
        writer.print(type);
        for (Metric metric : metrics) {
            writer.print(',' + metric.getAbbreviation());
        }
        writer.println();
        final String[] measuredObjects = results.getMeasuredObjects();
        Arrays.sort(measuredObjects);

        for (String object : measuredObjects) {
            writer.print(escape(object));
            for (Metric metric : metrics) {
                final Double metricValue = results.getValueForMetric(metric, object);
                if (metricValue == null) {
                    writer.print(",n/a");
                } else {
                    writer.print(',' + escape(FormatUtils.formatValue(metric, metricValue)));
                }
            }
            writer.println();
        }
        writer.println();
    }

    /**
     * Wraps the specified string into double-quotes if it contains commas, newlines or double-quotes.
     * Any double-quote that is part of the string will be represented by two double-quote characters
     * @param s  the string to escape
     * @return the escaped string
     */
    static String escape(String s) {
        if (!StringUtil.containsAnyChar(s, "\",\n")) {
            return s;
        }
        final StringBuilder result = new StringBuilder("\"");
        int i = 0;
        final int length = s.length();
        while (i < length) {
            final int codePoint = s.codePointAt(i);
            if (codePoint == '"') {
                result.append("\"\"");
            }
            else {
                result.appendCodePoint(codePoint);
            }
            i += Character.charCount(codePoint);
        }
        result.append("\"");
        return result.toString();
    }
}
