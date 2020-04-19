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

package com.sixrr.metrics.ui.charts;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.JFreeChartConstants;
import org.jfree.chart.labels.PieItemLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.DefaultPieDataset;
import org.jfree.data.PieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PieChartDialog extends DialogWrapper {

    private final ChartPanel chartPanel;
    private final String metricName;
    private final String metricTypeName;
    private final Double[] values;
    private final String[] measuredItems;
    private static final double SMALLEST_PIE_PIECE = 0.03;

    public PieChartDialog(Project project, String metricName,
                          String metricTypeName, String[] measuredItems,
                          Double[] values) {
        super(project, true);
        this.metricName = metricName;
        this.metricTypeName = metricTypeName;
        this.measuredItems = measuredItems.clone();
        this.values = values.clone();
        final PieDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);
        init();
    }

    private PieDataset createDataset() {
        final List<Pair<String, Double>> namedValues = new ArrayList<>();
        double total = 0.0;
        for (int j = 0; j < values.length; j++) {
            final Double value = values[j];
            final String measuredItem = measuredItems[j];
            if (value != null && value != 0.0) {
                namedValues.add(new Pair<>(measuredItem, value));
                total += value;
            }
        }
        Collections.sort(namedValues, (pair1, pair2) -> {
            final Double value1 = pair1.getSecond();
            final Double value2 = pair2.getSecond();
            return -value1.compareTo(value2);
        });
        final DefaultPieDataset dataset = new DefaultPieDataset();

        double totalForOther = 0.0;
        for (final Pair<String, Double> namedValue : namedValues) {
            final double value = namedValue.getSecond();
            if (value > total * SMALLEST_PIE_PIECE) {
                dataset.setValue(namedValue.getFirst(), value);
            } else {
                totalForOther += value;
            }
        }
        if (totalForOther != 0.0) {
            dataset.setValue(MetricsReloadedBundle.message("other"), totalForOther);
        }
        return dataset;
    }

    private JFreeChart createChart(PieDataset dataset) {
        final String title = getTitle();
        final PiePlot plot = new PiePlot(dataset);
        plot.setInsets(new Insets(0, 5, 5, 5));
        final int numItems = dataset.getItemCount();
        int total = 0;
        for (int i = 0; i < numItems; i++) {
            final Number value = dataset.getValue(i);
            total += value.intValue();
        }
        final PieItemLabelGenerator tooltipGenerator = new PieChartTooltipGenerator(total);
        plot.setItemLabelGenerator(tooltipGenerator);
        plot.setURLGenerator(null);
        return new JFreeChart(title, JFreeChartConstants.DEFAULT_TITLE_FONT, plot,
                false);
    }

    @Override
    public JComponent createCenterPanel() {
        return chartPanel;
    }

    @NotNull
    @Override
    public Action[] createActions() {
        return new Action[0];
    }

    @Override
    public String getTitle() {
        return MetricsReloadedBundle.message("pie.chart.title.message", metricName, metricTypeName);
    }

    @Override
    @NonNls
    protected String getDimensionServiceKey() {
        return "MetricsReloaded.PieChartDialog";
    }

    private static class PieChartTooltipGenerator
            implements PieItemLabelGenerator {
        private final int total;

        private PieChartTooltipGenerator(int total) {
            super();
            this.total = total;
        }

        @Override
        public String generateToolTip(PieDataset pieDataset,
                                      Comparable comparable, int i) {
            final int value = pieDataset.getValue(comparable).intValue();
            return MetricsReloadedBundle.message("pie.chart.tool.tip", value, total);
        }
    }
}
