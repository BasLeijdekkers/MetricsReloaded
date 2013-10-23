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

public class HistogramDialog extends DialogWrapper {
    private static final int DEFAULT_BIN_COUNT = 20;
    private final ChartPanel chartPanel;
    private final String metricName;
    private final String metricCategoryName;
    private final MetricType metricType;
    private static final double EPSILON = 0.0000001;

    public HistogramDialog(Project project, String metricName, String metricCategoryName, Double[] datapoints,
                           MetricType metricType) {
        super(project, false);
        this.metricName = metricName;
        this.metricCategoryName = metricCategoryName;
        this.metricType = metricType;
        final double[] strippedData = GraphUtils.stripNulls(datapoints);
        final boolean isIntegral = isIntegralData(strippedData);

        final IntervalXYDataset dataset = createDataset(strippedData, isIntegral);
        final JFreeChart chart = createChart(dataset, isIntegral);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);
        init();
    }

    private IntervalXYDataset createDataset(double[] strippedData, boolean isIntegral) {
        if (isIntegral) {
            final IntegerHistogramDataset dataset = new IntegerHistogramDataset();
            dataset.setType(HistogramDataset.FREQUENCY);
            dataset.addSeries(metricName, strippedData);
            return dataset;
        } else {
            final HistogramDataset dataset = new HistogramDataset();
            dataset.setType(HistogramDataset.FREQUENCY);
            dataset.addSeries(metricName, strippedData, DEFAULT_BIN_COUNT);
            return dataset;
        }
    }

    private static boolean isIntegralData(double[] data) {
        boolean isIntegral = true;
        double maximum = Double.MIN_VALUE;
        for (double aData : data) {
            if (!isIntegral(aData)) {
                isIntegral = false;
            }
            maximum = Math.max(maximum, aData);
        }
        return isIntegral && maximum < 2 * DEFAULT_BIN_COUNT;
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
        if (isIntegral) {
            xAxis.setTickUnit(new NumberTickUnit(1.0));
        }
        if (metricType == MetricType.Ratio || metricType == MetricType.RecursiveRatio) {
            xAxis.setNumberFormatOverride(new PercentFormatter());
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
        return MetricsReloadedBundle.message("diff.histogram.dialog.title", metricName, metricCategoryName);
    }

    @Override
    @NonNls
    protected String getDimensionServiceKey() {
        return "MetricsReloaded.HistogramDialog";
    }

}
