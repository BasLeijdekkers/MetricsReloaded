/*
 * Copyright 2005-2013 Sixth and Red River Software, Bas Leijdekkers
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
import com.sixrr.metrics.MetricType;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NonNls;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.JFreeChartConstants;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.XYDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class DistributionDialog extends DialogWrapper {
    private ChartPanel chartPanel;
    private final String metricName;
    private final Double[] datapoints;
    private final String category;
    private final MetricType metricType;

    public DistributionDialog(Project project, String metricName, String category, MetricType metricType,
                              Double[] datapoints) {
        super(project, false);
        this.metricName = metricName;
        this.category = category;
        this.metricType = metricType;
        this.datapoints = datapoints.clone();
        final XYDataset dataset = createDistributionSeries();
        final JFreeChart chart = createChart(dataset);
        createChartPanel(chart);
        init();
    }

    private void createChartPanel(JFreeChart chart) {
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);
    }

    private XYSeriesCollection createDistributionSeries() {
        final double[] strippedData = GraphUtils.stripNulls(datapoints);

        final XYSeries series = new XYSeries(metricName);
        Arrays.sort(strippedData);
        if (strippedData[0] != 0.0) {
            series.add(0.0, 0.0);
        }
        for (int i = 0; i < strippedData.length; i++) {
            int j = i + 1;
            while (j < strippedData.length && strippedData[i] == strippedData[j]) {
                i++;
                j++;
            }
            series.add(strippedData[i], (double) (i + 1) * 100.0 / (double) strippedData.length);
        }
        return new XYSeriesCollection(series);
    }

    private JFreeChart createChart(XYDataset dataset) {
        final String title = getTitle();

        final NumberAxis xAxis = new NumberAxis(metricName);
        xAxis.setAutoRangeIncludesZero(false);
        if (metricType == MetricType.Ratio || metricType == MetricType.RecursiveRatio) {
            xAxis.setNumberFormatOverride(new PercentFormatter());
        }
        final NumberAxis yAxis = new NumberAxis("%");
        final XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
        final XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        renderer.setToolTipGenerator(new StandardXYToolTipGenerator());
        return new JFreeChart(title, JFreeChartConstants.DEFAULT_TITLE_FONT, plot, true);
    }

    @Override
    public JComponent createCenterPanel() {
        return chartPanel;
    }

    @Override
    public Action[] createActions() {
        return new Action[0];
    }

    @Override
    public String getTitle() {
        if (metricName.startsWith(MetricsReloadedBundle.message("number.of"))) {
            final String shortName = metricName.substring(MetricsReloadedBundle.message("number.of").length());
            return MetricsReloadedBundle.message("distribution.diff.dialog.title", shortName.toLowerCase(), category);
        } else {
            return MetricsReloadedBundle.message("distribution.diff.dialog.title", metricName.toLowerCase(), category);
        }
    }

    @Override
    @NonNls
    protected String getDimensionServiceKey() {
        return "MetricsReloaded.DistributionDialog";
    }

}
