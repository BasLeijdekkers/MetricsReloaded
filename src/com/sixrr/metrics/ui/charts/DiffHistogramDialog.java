/*
 * Copyright 2005-2013 Sixth and Red River Software
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
import org.jetbrains.annotations.NotNull;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.JFreeChartConstants;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.XYBarRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.HistogramDataset;
import org.jfree.data.IntegerHistogramDataset;
import org.jfree.data.IntervalXYDataset;

import javax.swing.*;
import java.awt.*;

public class DiffHistogramDialog extends DialogWrapper {
    private static final int DEFAULT_BIN_COUNT = 20;
    private final ChartPanel chartPanel;
    private final String metricCategory;
    private final String metricName;
    private final MetricType metricType;
    private final Double[] datapoints;
    private final Double[] prevDatapoints;
    private static final double EPSILON = 0.0000001;

    public DiffHistogramDialog(Project project, String metricCategory, String metricName, MetricType metricType,
                               Double[] datapoints, Double[] prevDatapoints) {
        super(project, false);
        this.metricCategory = metricCategory;
        this.metricName = metricName;
        this.metricType = metricType;
        this.datapoints = datapoints.clone();
        this.prevDatapoints = prevDatapoints.clone();
        final boolean isIntegral = isDataIntegral(datapoints, prevDatapoints);

        final IntervalXYDataset dataset = createDataset(isIntegral);
        final JFreeChart chart = createChart(dataset, isIntegral);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);
        init();
    }

    private static boolean isDataIntegral(Double[] datapoints, Double[] prevDatapoints) {
        final double[] strippedData = GraphUtils.stripNulls(datapoints);
        final double[] strippedPrevData = GraphUtils.stripNulls(prevDatapoints);

        boolean isIntegral = true;
        double maximum = Double.MIN_VALUE;
        for (double aStrippedData : strippedData) {
            if (!isIntegral(aStrippedData)) {
                isIntegral = false;
            }
            maximum = Math.max(maximum, aStrippedData);
        }
        for (double aStrippedPrevData : strippedPrevData) {
            if (!isIntegral(aStrippedPrevData)) {
                isIntegral = false;
            }
            maximum = Math.max(maximum, aStrippedPrevData);
        }
        return isIntegral && maximum < 2 * DEFAULT_BIN_COUNT;
    }

    private IntervalXYDataset createDataset(boolean isIntegral) {
        final double[] clonedData = GraphUtils.stripNulls(datapoints);
        final double[] clonedData1 = GraphUtils.stripNulls(prevDatapoints);
        if (isIntegral) {
            final IntegerHistogramDataset dataset = new IntegerHistogramDataset();
            dataset.setType(HistogramDataset.FREQUENCY);
            dataset.addSeries(metricName, clonedData);
            dataset.addSeries(MetricsReloadedBundle.message("previous") +
                    " " + metricName, clonedData1);
            return dataset;
        } else {
            final HistogramDataset dataset = new HistogramDataset();
            dataset.setType(HistogramDataset.FREQUENCY);

            dataset.addSeries(metricName, clonedData, DEFAULT_BIN_COUNT);
            dataset.addSeries(MetricsReloadedBundle.message("previous") +
                    " " + metricName, clonedData1, DEFAULT_BIN_COUNT);
            return dataset;
        }
    }

    private static boolean isIntegral(double v) {
        if (Math.abs(Math.ceil(v) - v) < EPSILON) {
            return true;
        }
        return Math.abs(v - Math.floor(v)) < EPSILON;
    }

    private JFreeChart createChart(IntervalXYDataset dataset, boolean isIntegral) {
        final String title = getTitle();
        final NumberAxis xAxis = new NumberAxis();
        if (metricType.equals(MetricType.Ratio) || metricType.equals(MetricType.RecursiveRatio)) {
            xAxis.setNumberFormatOverride(new PercentFormatter());
        }
        if (isIntegral) {
            xAxis.setTickUnit(new NumberTickUnit(1.0));
        }

        final XYToolTipGenerator tooltipGenerator = new StandardXYToolTipGenerator();

        final XYItemRenderer renderer = new XYBarRenderer();
        renderer.setToolTipGenerator(tooltipGenerator);
        renderer.setURLGenerator(null);

        final ValueAxis yAxis = new NumberAxis();
        final XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        plot.setRenderer(renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);

        return new JFreeChart(title, JFreeChartConstants.DEFAULT_TITLE_FONT, plot, true);
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
        return MetricsReloadedBundle.message("diff.histogram.dialog.title", metricName, metricCategory);
    }

    @Override
    @NonNls
    protected String getDimensionServiceKey() {
        return "MetricsReloaded.DiffHistogramDialog";
    }

}
