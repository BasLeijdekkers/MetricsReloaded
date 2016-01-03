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

package com.sixrr.metrics.ui.metricdisplay;

import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricType;
import com.sixrr.metrics.metricModel.MetricInstance;
import com.sixrr.metrics.ui.FormatUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;

class MetricCellRenderer extends DefaultTableCellRenderer {
    private static final NumberFormat numberFormatter = NumberFormat.getNumberInstance();

    static {
        numberFormatter.setMaximumFractionDigits(2);
        numberFormatter.setMinimumFractionDigits(2);
    }

    private final MetricInstance metricInstance;

    MetricCellRenderer(MetricInstance metricInstance) {
        this.metricInstance = metricInstance;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setHorizontalAlignment(RIGHT);
        final MetricTableModel model = (MetricTableModel) table.getModel();
        if (model.hasDiff()) {
            if (model.hasSummaryRows() && row >= model.getRowCount() - 2) {
                return getDiffTableSummaryCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            } else {
                return getDiffTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        } else {
            if (model.hasSummaryRows() && row >= model.getRowCount() - 2) {
                return getDoubleTableSummaryCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            } else {
                return getDoubleTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }
    }

    private Component getDoubleTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int column) {
        double doubleValue = value != null ? ((Double) value).doubleValue() : 0.0;
        final Metric metric = metricInstance.getMetric();
        final MetricType metricType = metric.getType();
        if (metricType == MetricType.Ratio || metricType == MetricType.RecursiveRatio) {
            doubleValue *= 100.0;
        }
        final String stringValue = FormatUtils.formatValue(metric, (Double) value);
        final JLabel label = (JLabel) super
                .getTableCellRendererComponent(table, stringValue, isSelected, hasFocus, row, column);
        if (value != null) {
            if (metricInstance.isUpperThresholdEnabled() && doubleValue > metricInstance.getUpperThreshold() ||
                    metricInstance.isLowerThresholdEnabled() && doubleValue < metricInstance.getLowerThreshold()) {
                label.setForeground(JBColor.RED);
            } else {
                label.setForeground(UIUtil.getTableForeground(isSelected));
            }
        } else {
            label.setForeground(UIUtil.getTableForeground(isSelected));
        }
        return label;
    }

    private Component getDoubleTableSummaryCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                                 boolean hasFocus, int row, int column) {
        final MetricTableModel model = (MetricTableModel) table.getModel();
        final String stringValue;
        final Metric metric = metricInstance.getMetric();
        if (row == model.getRowCount() - 1) {
            stringValue = FormatUtils.formatAverageValue(metric, (Double) value);
        } else {
            stringValue = FormatUtils.formatValue(metric, (Double) value);
        }

        return super.getTableCellRendererComponent(table, stringValue, isSelected, hasFocus, row, column);
    }

    private Component getDiffTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                        boolean hasFocus, int row, int column) {
        if (!(value instanceof Pair)) {
            return super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
        }
        final Pair<Double, Double> pair = (Pair<Double, Double>) value;
        final Metric metric = metricInstance.getMetric();
        final Double currentValue = pair.getFirst();
        final Double prevValue = pair.getSecond();
        final StringBuilder stringValue = new StringBuilder(16);
        if (currentValue != null && prevValue != null && prevValue.equals(currentValue)) {
            stringValue.append(FormatUtils.formatValue(metric, currentValue));
        } else {
            if (currentValue != null) {
                stringValue.append(FormatUtils.formatValue(metric, currentValue));
            }
            if (currentValue != null && prevValue != null) {
                stringValue.append('/');
            }
            if (prevValue != null) {
                stringValue.append(FormatUtils.formatValue(metric, prevValue));
            }
        }
        final JLabel label = (JLabel) super
                .getTableCellRendererComponent(table, stringValue, isSelected, hasFocus, row, column);
        final EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
        //noinspection IfStatementWithTooManyBranches
        if (isSelected) {
            label.setBackground(table.getSelectionBackground());
        } else if (prevValue != null && currentValue != null) {
            final String formattedCurrentVal = FormatUtils.formatValue(metric, currentValue);
            final String formattedPrevVal = FormatUtils.formatValue(metric, prevValue);
            if (formattedPrevVal.equals(formattedCurrentVal)) {
                label.setBackground(UIUtil.getTableBackground());
            } else {
                label.setBackground(colorsScheme.getColor(EditorColors.MODIFIED_LINES_COLOR));
            }
        } else if (prevValue == null && currentValue != null) {
            label.setBackground(colorsScheme.getColor(EditorColors.ADDED_LINES_COLOR));
        } else if (prevValue != null) {
            label.setBackground(colorsScheme.getColor(EditorColors.DELETED_LINES_COLOR));
        } else {
            label.setBackground(UIUtil.getTableBackground());
        }
        return label;
    }

    private Component getDiffTableSummaryCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
        if (!(value instanceof Pair)) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        final Metric metric = metricInstance.getMetric();
        final Pair<Double, Double> pair = (Pair<Double, Double>) value;
        final Double currentValue = pair.getFirst();
        final Double prevValue = pair.getSecond();
        final MetricTableModel model = (MetricTableModel) table.getModel();
        final StringBuilder stringValue = new StringBuilder(16);
        if (row == model.getRowCount() - 1) {
            if (currentValue != null && prevValue != null && prevValue.equals(currentValue)) {
                stringValue.append(FormatUtils.formatAverageValue(metric, currentValue));
            } else {
                if (currentValue != null) {
                    stringValue.append(FormatUtils.formatAverageValue(metric, currentValue));
                }
                if (currentValue != null && prevValue != null) {
                    stringValue.append('/');
                }
                if (prevValue != null) {
                    stringValue.append(FormatUtils.formatAverageValue(metric, prevValue));
                }
            }
        } else {
            if (currentValue != null && prevValue != null && prevValue.equals(currentValue)) {
                stringValue.append(FormatUtils.formatValue(metric, currentValue));
            } else {
                if (currentValue != null) {
                    stringValue.append(FormatUtils.formatValue(metric, currentValue));
                }
                if (currentValue != null && prevValue != null) {
                    stringValue.append('/');
                }
                if (prevValue != null) {
                    stringValue.append(FormatUtils.formatAverageValue(metric, prevValue));
                }
            }
        }
        return super.getTableCellRendererComponent(table, stringValue, isSelected, hasFocus, row, column);
    }
}
